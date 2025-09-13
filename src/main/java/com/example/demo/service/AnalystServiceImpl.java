package com.example.demo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.AnalystListResponseDto;
import com.example.demo.dto.AnalystRandomDto;
import com.example.demo.dto.AnalystUpdateRequestDto;
import com.example.demo.dto.AnalystUpdateResponseDto;
import com.example.demo.entity.Analyst;
import com.example.demo.entity.AnalystSpecialty;
import com.example.demo.entity.Specialty;
import com.example.demo.entity.User;
import com.example.demo.repository.AnalystRepository;
import com.example.demo.repository.AnalystSpecialtyRepository;
import com.example.demo.repository.SpecialtyRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.FileUploadUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalystServiceImpl implements AnalystService {

    private final AnalystRepository analystRepository;
    private final AnalystSpecialtyRepository analystSpecialtyRepository;
    private final SpecialtyRepository specialtyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Spring Security 的加密工具
    
    public AnalystServiceImpl(AnalystRepository analystRepository,
            AnalystSpecialtyRepository analystSpecialtyRepository,
            SpecialtyRepository specialtyRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
			this.analystRepository = analystRepository;
			this.analystSpecialtyRepository = analystSpecialtyRepository;
			this.specialtyRepository = specialtyRepository;
			this.userRepository = userRepository;
			this.passwordEncoder = passwordEncoder;
			}

    @Override
    public Optional<Analyst> getAnalystByUserId(Integer userId) {
        return analystRepository.findByUserId(userId);
    }

    @Override
    public AnalystUpdateResponseDto getProfileByUserId(Integer userId) {
        return null;
        // 從 repository 查找 analyst
        // mapping 成 ResponseDto
    }

    @Transactional
    @Override
    public void updateProfile(Integer userId, AnalystUpdateRequestDto dto,
                            MultipartFile profileImg, MultipartFile certificateImg) {

        // 1️⃣ 找出對應的分析師資料
        Analyst analyst = analystRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("找不到分析師資料"));

        // 2️⃣ 更新文字欄位（只更新有填寫的）
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            analyst.setTitle(dto.getTitle());
        }

        if (dto.getBio() != null && !dto.getBio().isBlank()) {
            analyst.setBio(dto.getBio());
        }

        // 3️⃣ 儲存圖片（使用 FileUploadUtil ）
        try {
            if (profileImg != null && !profileImg.isEmpty()) {
                String profilePath = FileUploadUtil.saveFile(analyst.getId(), profileImg, "profile");
                analyst.setProfileImgPath(profilePath);
            }

            if (certificateImg != null && !certificateImg.isEmpty()) {
                String certPath = FileUploadUtil.saveFile(analyst.getId(), certificateImg, "certificate");
                analyst.setCertificateImgPath(certPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("圖片上傳失敗：" + e.getMessage(), e);
        }

        // 4️⃣ 專長同步（刪除舊的，加入新的）
        if (dto.getSpecialtyIds() != null) {
            // 1. 清除舊資料
            analystSpecialtyRepository.deleteByAnalystId(analyst.getId());

            // 2. 儲存新選擇的專長（確認專長存在）
            List<AnalystSpecialty> specialties = dto.getSpecialtyIds().stream()
                .map(specialtyId -> {
                    Specialty specialty = specialtyRepository.findById(specialtyId)
                        .orElseThrow(() -> new RuntimeException("找不到專長 ID: " + specialtyId));
                    AnalystSpecialty as = new AnalystSpecialty();
                    as.setAnalyst(analyst);
                    as.setSpecialty(specialty);
                    return as;
                }).toList();

            analystSpecialtyRepository.saveAll(specialties);
        }

        // 5️⃣ 如果有密碼 → 加密後更新
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            User user = analyst.getUser(); // 假設 Analyst 有連 user
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            userRepository.save(user);
        }

        // 6️⃣ 最後更新分析師主表
        analystRepository.save(analyst);
    }

    // @Override
    // public AnalystProfileResponseDto getProfile(Integer userId) {
        
    //     // 查找分析師主檔
    //     Analyst analyst = analystRepository.findByUserId(userId)
    //         .orElseThrow(() -> new RuntimeException("找不到分析師資料"));

    //     // // 查找專長 ID 清單
    //     // List<Integer> specialtyIds = analystSpecialtyRepository.findByAnalystId(analyst.getId())
    //     //     .stream()
    //     //     .map(AnalystSpecialty::getSpecialtyId)
    //     //     .toList();

        
    //     // 查詢分析師的專長名稱
    //     List<String> specialtyNames = analystSpecialtyRepository.findByAnalystId(analyst.getId()).stream()
    //         .map(as -> specialtyRepository.findById(as.getSpecialtyId())
    //             .map(Specialty::getName)
    //             .orElse("未知專長")) // 防止找不到
    //         .toList();


    //     // 建立回傳 DTO
    //     AnalystProfileResponseDto response = new AnalystProfileResponseDto();
    //     // response.setName(analyst.getUser().getName()); // 🔁 從關聯 User 中取得 name
    //     response.setTitle(analyst.getTitle());
    //     response.setBio(analyst.getBio());
    //     response.setProfileImgPath(analyst.getProfileImgPath());
    //     response.setCertificateImgPath(analyst.getCertificateImgPath());
    //     // response.setSpecialtyIds(specialtyIds);
    //     response.setSpecialties(specialtyNames);

    //     return response;
    // }

    @Override
    public AnalystUpdateResponseDto getProfile(Integer userId) {
        // 1. 查找分析師主檔
        Analyst analyst = analystRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("找不到分析師"));

        // 2. 查詢分析師的專長名稱清單（防呆處理）
        List<AnalystSpecialty> analystSpecialties = analystSpecialtyRepository.findByAnalystId(analyst.getId());
        
        List<String> specialtyNames = analystSpecialties.stream()
            .map(as -> {
                Integer specialtyId = as.getSpecialty() != null ? as.getSpecialty().getId() : null;
                if (specialtyId == null) return "未知專長";
                return specialtyRepository.findById(specialtyId)
                        .map(Specialty::getName)
                        .orElse("未知專長");
            })
            .toList();

        // 3. 回傳資料封裝成 DTO
        AnalystUpdateResponseDto dto = new AnalystUpdateResponseDto();
        dto.setTitle(analyst.getTitle());
        dto.setBio(analyst.getBio());
        dto.setProfileImgPath(analyst.getProfileImgPath());
        dto.setCertificateImgPath(analyst.getCertificateImgPath());
        dto.setSpecialties(specialtyNames);
        return dto;
    }

    @Override
    public List<AnalystListResponseDto> getAllVerifiedAnalysts() {
        List<Analyst> analysts = analystRepository.findByVerifiedTrue();

        return analysts.stream().map(a -> {
            User user = a.getUser();

            List<AnalystSpecialty> specialties = analystSpecialtyRepository.findByAnalystId(a.getId());

            List<String> specialtyNames = specialties.stream()
                .map(as -> {
                    Specialty specialty = as.getSpecialty(); // ⬅ 從關聯取得
                    return specialty != null ? specialty.getName() : "未知專長";
                })
                .collect(Collectors.toList());

            AnalystListResponseDto dto = new AnalystListResponseDto();
            dto.setAnalystId(a.getId());
            dto.setName(user != null ? user.getName() : "未知分析師");
            dto.setTitle(a.getTitle());
            dto.setBio(a.getBio());
            dto.setProfileImgPath(a.getProfileImgPath());
            dto.setSpecialties(specialtyNames);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AnalystRandomDto> getRandomAnalysts(int count) {
        List<Analyst> all = analystRepository.findAll();
        Collections.shuffle(all); // 隨機排序

        return all.stream()
        	    .filter(analyst -> analyst.getVerified() != null && analyst.getVerified())
        	    .limit(count)
        	    .map(analyst -> {
        	        String name = analyst.getUser() != null && analyst.getUser().getName() != null
        	                ? analyst.getUser().getName()
        	                : "匿名分析師";

        	        List<AnalystSpecialty> relationList = analystSpecialtyRepository.findByAnalyst(analyst);
        	        List<String> specialties = new ArrayList<>();
        	        for (AnalystSpecialty as : relationList) {
        	            Specialty spec = as.getSpecialty();
        	            specialties.add(spec != null && spec.getName() != null ? spec.getName() : "未知");
        	        }

        	        AnalystRandomDto dto = new AnalystRandomDto();
        	        dto.setAnalystId(analyst.getId());
        	        dto.setName(name);
        	        dto.setTitle(analyst.getTitle());
        	        dto.setProfileImg(analyst.getProfileImgPath());
        	        dto.setSpecialties(specialties);
        	        return dto;
        	    })
        	    .collect(Collectors.toList());
    }




}


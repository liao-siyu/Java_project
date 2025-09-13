package com.example.demo.service;

import com.example.demo.dto.AnalystUpdateRequestDto;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.LoginResponseDto;
import com.example.demo.dto.RegisterRequestDto;
import com.example.demo.entity.Analyst;
import com.example.demo.entity.AnalystSpecialty;
import com.example.demo.entity.Specialty;
import com.example.demo.entity.User;
import com.example.demo.enums.UserRole;
import com.example.demo.repository.AnalystRepository;
import com.example.demo.repository.AnalystSpecialtyRepository;
import com.example.demo.repository.SpecialtyRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.FileUploadUtil;
import com.example.demo.util.PasswordEncoderUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AnalystRepository analystRepository;
    private final PasswordEncoderUtil passwordEncoderUtil;
    
    public AuthServiceImpl(UserRepository userRepository,
            AnalystRepository analystRepository,
            PasswordEncoderUtil passwordEncoderUtil) {
			this.userRepository = userRepository;
			this.analystRepository = analystRepository;
			this.passwordEncoderUtil = passwordEncoderUtil;
			}

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    private AnalystSpecialtyRepository analystSpecialtyRepository;

    @Override
    @Transactional
    public User register(RegisterRequestDto request) {
        // 檢查 email 是否已存在
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("此 Email 已被註冊");
        }

        // ✅ 加密密碼
        String encryptedPassword = passwordEncoderUtil.encode(request.getPassword());

        // 建立 User 實體
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(encryptedPassword);
        user.setName(request.getName());
        user.setUserRole(UserRole.valueOf(request.getRole()));
        user.setVerified(false);

        // User savedUser = userRepository.save(user);

        // 若角色為 analyst，則新增對應的分析師資料
        if (user.getUserRole() == UserRole.analyst) {
        	Analyst analyst = new Analyst();
            analyst.setUser(user);
            analyst.setVerified(false);
            user.setAnalyst(analyst);
            user.setAnalyst(analyst); // 建立雙向關聯
        }

        return userRepository.save(user);
    }

    @Override
    // @Transactional
    public void registerAnalyst(AnalystUpdateRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("此 Email 已註冊");
        }

        // 建立使用者帳號
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoderUtil.encode(dto.getPassword()));
        user.setUserRole(UserRole.analyst);;
        userRepository.save(user);

        // 建立分析師資料
        Analyst analyst = new Analyst();
        analyst.setUser(user);
        analyst.setTitle(dto.getTitle());
        analyst.setBio(dto.getBio());
        analystRepository.save(analyst); // 儲存以拿到 analystId

        // 上傳圖片
        try {
            String profilePath = FileUploadUtil.saveFile(analyst.getId(), dto.getProfileImg(), "profile");
            String certPath = FileUploadUtil.saveFile(analyst.getId(), dto.getCertificateImg(), "certificate");
            analyst.setProfileImgPath(profilePath);
            analyst.setCertificateImgPath(certPath);
            analystRepository.save(analyst);
        } catch (IOException e) {
            throw new RuntimeException("圖片上傳失敗");
        }
        analystRepository.save(analyst);

        // 4. 儲存分析師專長
        List<Integer> specialtyIds = dto.getSpecialtyIds();
        if (specialtyIds != null) {
            for (Integer specialtyId : specialtyIds) {
                AnalystSpecialty as = new AnalystSpecialty();
                as.setAnalyst(analyst);
                Specialty specialty = specialtyRepository.findById(specialtyId).orElse(null);
                if (specialty != null) {
                    as.setSpecialty(specialty);
                    analystSpecialtyRepository.save(as);
                }
            }
        }
    }

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
    // 1. 找使用者
    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("帳號或密碼錯誤"));

    // 2. 比對密碼
    boolean passwordMatch = passwordEncoderUtil.matches(request.getPassword(), user.getPassword());
    if (!passwordMatch) {
        throw new IllegalArgumentException("帳號或密碼錯誤");
    }

    // 3. 查分析師 ID（若是分析師）
    Integer analystId = null;
    if (user.getUserRole() == UserRole.analyst) {
        Analyst analyst = analystRepository.findByUser(user)
                .orElse(null);
        if (analyst != null) {
            analystId = analyst.getId();
        }
    }

    // 4. 回傳登入資訊
    LoginResponseDto dto = new LoginResponseDto();
    dto.setUserId(user.getId());
    dto.setEmail(user.getEmail());
    dto.setName(user.getName());
    dto.setUserRole(user.getUserRole().name());
    dto.setAnalystId(analystId);

    return dto;
    }
}

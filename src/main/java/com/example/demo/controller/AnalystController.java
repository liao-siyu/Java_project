package com.example.demo.controller;

import com.example.demo.dto.AnalystListResponseDto;
import com.example.demo.dto.AnalystRandomDto;
import com.example.demo.dto.AnalystUpdateRequestDto;
import com.example.demo.dto.AnalystUpdateResponseDto;
import com.example.demo.entity.Analyst;
import com.example.demo.service.AnalystService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analyst")
public class AnalystController {

    private final AnalystService analystService;
    
    public AnalystController(AnalystService analystService) {
        this.analystService = analystService;
    }

    @GetMapping("/list")
    public List<AnalystListResponseDto> getAllVerifiedAnalysts() {
        return analystService.getAllVerifiedAnalysts();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Integer>> getAnalystIdByUserId(@PathVariable Integer userId) {
        Optional<Analyst> optionalAnalyst = analystService.getAnalystByUserId(userId);
        return optionalAnalyst
                .map(analyst -> ResponseEntity.ok(Map.of("analystId", analyst.getId())))
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ 查詢分析師帳號設定資料
    @GetMapping("/profile")
    public ResponseEntity<AnalystUpdateResponseDto> getProfile(@RequestParam Integer userId) {
        AnalystUpdateResponseDto profile = analystService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    // ✅ 更新帳號設定資料（含圖片上傳）
    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
            @ModelAttribute AnalystUpdateRequestDto dto,
            @RequestPart(value = "profileImg", required = false) MultipartFile profileImg,
            @RequestPart(value = "certificateImg", required = false) MultipartFile certificateImg
    ) {
        try {
            analystService.updateProfile(dto.getUserId(), dto, profileImg, certificateImg);
            return ResponseEntity.ok("帳號資料更新成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("後端更新失敗：" + e.getMessage());
        }
    }

    @GetMapping("recommend")
    public List<AnalystRandomDto> getRandomAnalysts() {
        return analystService.getRandomAnalysts(4); // 固定4筆
    }







}

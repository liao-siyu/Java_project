package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.AnalystListResponseDto;
import com.example.demo.dto.AnalystRandomDto;
import com.example.demo.dto.AnalystUpdateRequestDto;
import com.example.demo.dto.AnalystUpdateResponseDto;
import com.example.demo.entity.Analyst;

public interface AnalystService {
    
    Optional<Analyst> getAnalystByUserId(Integer userId);

    AnalystUpdateResponseDto getProfileByUserId(Integer userId);
    
    void updateProfile(Integer userId, AnalystUpdateRequestDto dto, MultipartFile profileImg, MultipartFile certificateImg);

    AnalystUpdateResponseDto getProfile(Integer userId);

    List<AnalystListResponseDto> getAllVerifiedAnalysts();

    List<AnalystRandomDto> getRandomAnalysts(int count);

}

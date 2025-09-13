package com.example.demo.service;

import com.example.demo.dto.AnalystUpdateRequestDto;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.LoginResponseDto;
import com.example.demo.dto.RegisterRequestDto;
import com.example.demo.entity.User;

public interface AuthService {
    User register(RegisterRequestDto request);
    LoginResponseDto login(LoginRequestDto request);
    void registerAnalyst(AnalystUpdateRequestDto dto);

}

package com.example.demo.controller;

import com.example.demo.dto.AnalystUpdateRequestDto;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.LoginResponseDto;
import com.example.demo.dto.RegisterRequestDto;
import com.example.demo.entity.Analyst;
import com.example.demo.entity.User;
import com.example.demo.repository.AnalystRepository;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin // 若為前後端分離，可允許跨域
public class AuthController {

	@Autowired
	private UserService userService;

	@Autowired
	private AnalystRepository analystRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	private final AuthService authService;

	public AuthController(UserService userService, AnalystRepository analystRepository,
			BCryptPasswordEncoder passwordEncoder, AuthService authService) {
		this.userService = userService;
		this.analystRepository = analystRepository;
		this.passwordEncoder = passwordEncoder;
		this.authService = authService;
	}

	// ✅ 註冊 API：POST /api/auth/register
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequestDto request) {
		try {
			User registeredUser = authService.register(request);

			// // ✅ 若為分析師，建立對應的 analysts 資料
			// if (registeredUser.getUserRole().name().equals("analyst")) {
			// Analyst analyst = new Analyst();
			// analyst.setUser(registeredUser);
			// analystRepository.save(analyst);
			// }

			// 建立回傳物件（不包含密碼）
			return ResponseEntity.ok().body(new Object() {
				public final Integer id = registeredUser.getId();
				public final String email = registeredUser.getEmail();
				public final String name = registeredUser.getName();
				public final String role = registeredUser.getUserRole().name();
			});

		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// // ✅ 登入
	// @PostMapping("/login")
	// public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
	// try {
	// LoginResponseDto response = authService.login(request);
	// return ResponseEntity.ok(response);
	// } catch (IllegalArgumentException e) {
	// return ResponseEntity.badRequest().body(e.getMessage());
	// }
	// }
	// @PostMapping("/login")
	// public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto
	// request) {
	// User user = userService.findByEmail(request.getEmail());

	// if (user == null || !user.getPassword().equals(request.getPassword())) {
	// return ResponseEntity.badRequest().build();
	// }

	// // System.out.println("✅ 登入帳號 role 欄位值: " + user.getUserRole()); // 印出 role
	// 方便除錯

	// Optional<Analyst> optionalAnalyst =
	// analystRepository.findByUserId(user.getId());

	// Integer analystId = optionalAnalyst.map(Analyst::getId).orElse(null);

	// LoginResponseDto response = new LoginResponseDto(
	// user.getId(),
	// user.getName(),
	// user.getEmail(),
	// user.getUserRole().toString(), // ✅ 加上 .toString() 轉成字串
	// analystId
	// );

	// return ResponseEntity.ok(response);
	// }
	// @PostMapping("/login")
	// public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
	// User user = userService.findByEmail(request.getEmail());

	// // if (user == null || !user.getPassword().equals(request.getPassword())) {
	// // return ResponseEntity.badRequest().body("帳號或密碼錯誤");
	// // }

	// if (user == null || !passwordEncoder.matches(request.getPassword(),
	// user.getPassword())) {
	// return ResponseEntity.badRequest().body("帳號或密碼錯誤");
	// }

	// System.out.println("✅ 登入帳號 role 欄位值: " + user.getUserRole());

	// Optional<Analyst> optionalAnalyst =
	// analystRepository.findByUserId(user.getId());

	// Integer analystId = optionalAnalyst.map(Analyst::getId).orElse(null);

	// LoginResponseDto response = new LoginResponseDto(
	// user.getId(),
	// user.getName(),
	// user.getEmail(),
	// user.getUserRole().toString(),
	// analystId
	// );

	// return ResponseEntity.ok(response);
	// }
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
		User user = userService.findByEmail(request.getEmail());

		if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			return ResponseEntity.badRequest().body(null);
		}

		System.out.println("✅ 登入帳號 role 欄位值: " + user.getUserRole());

		Optional<Analyst> optionalAnalyst = analystRepository.findByUserId(user.getId());
		Integer analystId = optionalAnalyst.map(Analyst::getId).orElse(null);

		LoginResponseDto response = new LoginResponseDto();
		response.setUserId(user.getId());
		response.setName(user.getName());
		response.setEmail(user.getEmail());
		response.setUserRole(user.getUserRole().toString());
		response.setAnalystId(analystId);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/register/analyst")
	public ResponseEntity<?> registerAnalyst(@ModelAttribute AnalystUpdateRequestDto dto) {
		try {
			authService.registerAnalyst(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("message", "註冊成功"));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
		}
	}

}

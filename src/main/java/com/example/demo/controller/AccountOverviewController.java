package com.example.demo.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.AccountOverviewDTO;
import com.example.demo.service.AccountOverviewService;

@RestController
@RequestMapping("/api/account-overview")
public class AccountOverviewController {
    
    private final AccountOverviewService accountService;
    
    public AccountOverviewController(AccountOverviewService accountService) {
        this.accountService = accountService;
    }
    
    @GetMapping("/{userId}")		//獲取前端使用者帳戶資料
    public ResponseEntity<AccountOverviewDTO> getAccountOverview(@PathVariable Long userId) {	//從URL路徑取得userId				
        AccountOverviewDTO account = accountService.getAccountOverview(userId);		//呼叫service層方法獲取帳戶資料
        return ResponseEntity.ok(account);		//返回帳戶資料給前端
    }
    
    @GetMapping("/{userId}/balance")
    public ResponseEntity<BigDecimal> getAccountBalance(@PathVariable Long userId) {
        BigDecimal balance = accountService.getAccountBalance(userId);
        return ResponseEntity.ok(balance);
    }
    
    @PostMapping("/{userId}/reset")
    public ResponseEntity<AccountOverviewDTO> resetAccount(
            @PathVariable Long userId,
            @RequestBody(required = false) Map<String, Object> body) {
        
    	BigDecimal initialBalance;
        try {
            initialBalance = new BigDecimal(
                body != null && body.get("initialBalance") != null 
                    ? body.get("initialBalance").toString() 
                    : "100000.00"
            );
        } catch (NumberFormatException e) {
            initialBalance = new BigDecimal("100000.00");
        }
        
        AccountOverviewDTO account = accountService.resetAccount(userId, initialBalance);
        return ResponseEntity.ok(account);
    }
}
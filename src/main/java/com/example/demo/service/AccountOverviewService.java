package com.example.demo.service;

import java.math.BigDecimal;

import com.example.demo.exception.AccountNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.AccountOverviewDTO;
import com.example.demo.model.AccountOverview;
import com.example.demo.repository.AccountOverviewRepository;

@Service
public class AccountOverviewService {
    
    private final AccountOverviewRepository accountRepository;
    
    private AccountOverviewDTO convertToDTO(AccountOverview account) {		//把資料庫的 AccountOverview（Entity）轉成 AccountOverviewDTO
        AccountOverviewDTO dto = new AccountOverviewDTO();
        dto.setAccountId(account.getAccountId());
        dto.setUserId(account.getUserId());
        dto.setAccountName(account.getAccountName());
        dto.setBalance(account.getBalance());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setUpdatedAt(account.getUpdatedAt());
        dto.setIsActive(account.getIsActive());
        return dto;
    }

    public AccountOverviewService(AccountOverviewRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    public AccountOverviewDTO getAccountOverview(Long userId) {		//根據使用者ID獲取帳戶概覽
        AccountOverview account = accountRepository.findByUserId(userId)	//呼叫repository層方法獲取帳戶資料
                .orElseThrow(() -> new AccountNotFoundException("Account not found for user ID: " + userId));	//如果找不到帳戶，拋出異常
        return convertToDTO(account);	//將實體轉換為DTO並返回
    }
    
    public BigDecimal getAccountBalance(Long userId) {
        return accountRepository.findBalanceByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException("Account balance not found for user ID: " + userId));
    }
    
    @Transactional
    public AccountOverviewDTO resetAccount(Long userId, BigDecimal initialBalance) {
        AccountOverview account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for user ID: " + userId));
        
        account.setBalance(initialBalance);
        AccountOverview savedAccount = accountRepository.save(account);
        return convertToDTO(savedAccount);
    }
    
}
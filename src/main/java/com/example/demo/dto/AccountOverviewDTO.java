package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountOverviewDTO {	//帳戶概覽資料傳輸對象，定義要給前端傳的欄位（結構）

	
	private Long accountId;		//帳戶ID(前端未使用
    private Long userId;		//使用者ID(前端未使用
    private String accountName;		//帳戶名稱
    private BigDecimal balance;		//帳戶餘額
    private LocalDateTime createdAt;		//帳戶創建時間(前端未使用
    private LocalDateTime updatedAt;		//帳戶更新時間	(前端未使用
    private Boolean isActive;		//帳戶是否啟用(前端未使用
    
	public Long getAccountId() {
		return accountId;
	}
	
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getAccountName() {
		return accountName;
	}
	
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	public BigDecimal getBalance() {
		return balance;
	}
	
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public Boolean getIsActive() {
		return isActive;
	}
	
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
}

package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {

    private Integer userId;
    private String email;
    private String name;
    private String userRole;
    private Integer analystId; // 若是 analyst，回傳對應 analystId；否則為 null
    
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public Integer getAnalystId() {
        return analystId;
    }

    public void setAnalystId(Integer analystId) {
        this.analystId = analystId;
    }
}

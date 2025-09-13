package com.example.demo.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

@Data
public class AnalystUpdateRequestDto {
    private Integer userId; // 從前端帶進來
    private String name;
    private String email;
    private String password;
    private String title;
    private String bio;
    // private List<Integer> specialties; // 多選
    private MultipartFile profileImg;
    private MultipartFile certificateImg;
    // private List<Integer> specialtyIds;
    private List<Integer> specialtyIds = new ArrayList<>();
    
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public MultipartFile getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(MultipartFile profileImg) {
        this.profileImg = profileImg;
    }

    public MultipartFile getCertificateImg() {
        return certificateImg;
    }

    public void setCertificateImg(MultipartFile certificateImg) {
        this.certificateImg = certificateImg;
    }

    public List<Integer> getSpecialtyIds() {
        return specialtyIds;
    }

    public void setSpecialtyIds(List<Integer> specialtyIds) {
        this.specialtyIds = specialtyIds;
    }
	
}

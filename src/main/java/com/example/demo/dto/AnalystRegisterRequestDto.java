package com.example.demo.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class AnalystRegisterRequestDto {
    private String name;
    private String email;
    private String password;
    private String title;
    private String bio;
    private List<Integer> specialties;
    private MultipartFile profileImg;
    private MultipartFile certificateImg;
    
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

    public List<Integer> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<Integer> specialties) {
        this.specialties = specialties;
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
}

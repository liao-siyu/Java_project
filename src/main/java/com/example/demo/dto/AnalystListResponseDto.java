package com.example.demo.dto;

import java.util.List;

import lombok.Data;

@Data
public class AnalystListResponseDto {
    private Integer analystId;
    private String name;
    private String title;
    private String bio;
    private String profileImgPath;
    private List<String> specialties;
    
    public Integer getAnalystId() {
        return analystId;
    }

    public void setAnalystId(Integer analystId) {
        this.analystId = analystId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getProfileImgPath() {
        return profileImgPath;
    }

    public void setProfileImgPath(String profileImgPath) {
        this.profileImgPath = profileImgPath;
    }

    public List<String> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<String> specialties) {
        this.specialties = specialties;
    }
}

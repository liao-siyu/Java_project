package com.example.demo.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnalystRandomDto {
    private Integer analystId;
    private String name;
    private String title;
    private String profileImg;
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

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public List<String> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<String> specialties) {
        this.specialties = specialties;
    }
}

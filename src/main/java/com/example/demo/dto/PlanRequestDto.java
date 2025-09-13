package com.example.demo.dto;

import lombok.Data;

@Data
public class PlanRequestDto {

    private Integer analystId;

    private String name;

    private String description;

    private Integer price;

    private Boolean status;
    
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}

package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponseDto {
    private Integer id;
    private String title;
    private String content;
    private String reportDate; // 傳字串方便前端處理
    private String planName; // 顯示報告對應的方案名稱（來自 plan.getName()）
    
    public ReportResponseDto(Integer id, String title, String content, String reportDate, String planName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.reportDate = reportDate;
        this.planName = planName;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }
}
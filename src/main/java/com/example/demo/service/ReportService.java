package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.ReportRequestDto;
import com.example.demo.dto.ReportResponseDto;

public interface ReportService {
    List<ReportResponseDto> getReportsByAnalystId(Integer analystId);

    void createReport(ReportRequestDto dto);

    void updateReport(Integer reportId, ReportRequestDto dto);

}

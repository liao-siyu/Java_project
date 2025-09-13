package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ReportRequestDto;
import com.example.demo.dto.ReportResponseDto;
import com.example.demo.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin
public class ReportController {

    private final ReportService reportService;
    
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/analyst/{analystId}")
    public List<ReportResponseDto> getReportsByAnalystId(@PathVariable Integer analystId) {
        return reportService.getReportsByAnalystId(analystId);
    }

    @PostMapping
    public ResponseEntity<Void> createReport(@RequestBody ReportRequestDto dto) {
        reportService.createReport(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created
    }

    // @PostMapping
    // public ResponseEntity<Void> createReport(@RequestBody ReportRequestDto dto) {
    //     reportService.createReport(dto);
    //     return ResponseEntity.ok().build(); // 這是 200
    // }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReport(@PathVariable Integer id,
                                        @RequestBody ReportRequestDto dto) {
        try {
            reportService.updateReport(id, dto);
            return ResponseEntity.ok("報告修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("報告修改失敗：" + e.getMessage());
        }
    }


    


}

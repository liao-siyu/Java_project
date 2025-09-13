package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.dto.ReportRequestDto;
import com.example.demo.dto.ReportResponseDto;
import com.example.demo.entity.Analyst;
import com.example.demo.entity.Plan;
import com.example.demo.entity.Report;
import com.example.demo.repository.AnalystRepository;
import com.example.demo.repository.PlanRepository;
import com.example.demo.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final AnalystRepository analystRepository;
    private final PlanRepository planRepository;
    
    public ReportServiceImpl(ReportRepository reportRepository,
            AnalystRepository analystRepository,
            PlanRepository planRepository) {
		this.reportRepository = reportRepository;
		this.analystRepository = analystRepository;
		this.planRepository = planRepository;
		}

    @Override
    public List<ReportResponseDto> getReportsByAnalystId(Integer analystId) {
        List<Report> reports = reportRepository.findByAnalystId(analystId);

        return reports.stream()
            .map(r -> new ReportResponseDto(
                r.getId(),
                r.getTitle(),
                r.getContent(),
                r.getReportDate() != null ? r.getReportDate().toString() : "",
                r.getPlan().getName() // 💡 顯示方案名稱
            ))
            .collect(Collectors.toList());
    }

    @Override
    public void createReport(ReportRequestDto dto) {
        Analyst analyst = analystRepository.findById(dto.getAnalystId())
            .orElseThrow(() -> new RuntimeException("找不到分析師 ID：" + dto.getAnalystId()));

        Plan plan = planRepository.findById(dto.getPlanId())
            .orElseThrow(() -> new RuntimeException("找不到方案 ID：" + dto.getPlanId()));

        Report report = new Report();
        report.setTitle(dto.getTitle());
        report.setContent(dto.getContent());
        report.setReportDate(LocalDate.parse(dto.getReportDate()));
        report.setAnalyst(analyst);
        report.setPlan(plan);
        reportRepository.save(report);
    }

    @Override
    public void updateReport(Integer reportId, ReportRequestDto dto) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("找不到報告 ID：" + reportId));

        report.setTitle(dto.getTitle());
        report.setContent(dto.getContent());

        if (dto.getReportDate() != null) {
            report.setReportDate(LocalDate.parse(dto.getReportDate())); // 字串轉 LocalDate
        }

        if (dto.getPlanId() != null) {
            Plan plan = planRepository.findById(dto.getPlanId())
                    .orElseThrow(() -> new RuntimeException("找不到方案 ID：" + dto.getPlanId()));
            report.setPlan(plan); // ✅ 現在 plan 有定義了
        }

        reportRepository.save(report);
    }


}

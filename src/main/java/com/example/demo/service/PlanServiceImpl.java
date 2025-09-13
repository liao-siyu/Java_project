package com.example.demo.service;

import com.example.demo.entity.Plan;
import com.example.demo.dto.PlanRequestDto;
import com.example.demo.dto.PlanResponseDto;
import com.example.demo.entity.Analyst;
import com.example.demo.repository.PlanRepository;
import com.example.demo.repository.AnalystRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final AnalystRepository analystRepository;
    
    public PlanServiceImpl(PlanRepository planRepository, AnalystRepository analystRepository) {
        this.planRepository = planRepository;
        this.analystRepository = analystRepository;
    }

    // @Override
    // @Transactional
    // public Plan createPlan( String name, String description, Integer price) {
    //     Analyst analyst = analystRepository.findById(analystId)
    //             .orElseThrow(() -> new IllegalArgumentException("找不到該分析師 ID：" + analystId));

    //     if (price == null || price < 0) {
    //         throw new IllegalArgumentException("價格必須大於等於 0");
    //     }

    //     Plan plan = Plan.builder()
    //             .analyst(analyst)
    //             .name(name)
    //             .description(description)
    //             .price(price)
    //             .status(false) // 預設未上架
    //             .build();

    //     return planRepository.save(plan);
    // }

    @Override
    public List<PlanResponseDto> getPlansByAnalystId(Integer analystId) {
        return planRepository.findByAnalystId(analystId)
            .stream()
            .map(plan -> new PlanResponseDto(
                plan.getId(),
                plan.getName(),
                plan.getDescription(),
                plan.getPrice(),
                plan.getStatus()
            ))
            .toList();
    }

    @Override
    public void togglePlanStatus(Integer planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("找不到此方案"));

        Boolean currentStatus = plan.getStatus();
        plan.setStatus(!currentStatus); // 切換 true/false
        planRepository.save(plan);
    }

    @Override
    public void updatePlan(Integer id, PlanRequestDto dto) {
        Plan plan = planRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("找不到此方案"));

        plan.setName(dto.getName());
        plan.setDescription(dto.getDescription());
        plan.setPrice(dto.getPrice());

        planRepository.save(plan);
    }

    @Override
    public void createPlan(PlanRequestDto dto) {
        Analyst analyst = analystRepository.findById(dto.getAnalystId())
            .orElseThrow(() -> new RuntimeException("找不到分析師"));

        Plan plan = new Plan();
        plan.setName(dto.getName());
        plan.setDescription(dto.getDescription());
        plan.setPrice(dto.getPrice());
        plan.setStatus(false);
        plan.setAnalyst(analyst);

        planRepository.save(plan);
    }



}

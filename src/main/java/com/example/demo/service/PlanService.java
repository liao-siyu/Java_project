package com.example.demo.service;

import com.example.demo.dto.PlanRequestDto;
import com.example.demo.dto.PlanResponseDto;

import java.util.List;

public interface PlanService {

    // Plan createPlan(String name, String description, Integer price);

    // List<Plan> getPlansByAnalystId(Integer analystId);
    // 取得分析師方案
    List<PlanResponseDto> getPlansByAnalystId(Integer analystId);

    // 切換上架狀態
    void togglePlanStatus(Integer planId);

    // 更新方案
    void updatePlan(Integer id, PlanRequestDto dto);

    // ✅ 新增方案
    void createPlan(PlanRequestDto dto);
    
}

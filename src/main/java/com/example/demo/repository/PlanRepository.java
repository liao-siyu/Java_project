package com.example.demo.repository;

import com.example.demo.entity.Plan;
import com.example.demo.entity.Analyst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {

    // 取得特定分析師的所有方案
    List<Plan> findByAnalyst(Analyst analyst);

    // 或用 ID 查（更常見）
    List<Plan> findByAnalystId(Integer analystId);
}

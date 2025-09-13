package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Analyst;
import com.example.demo.entity.AnalystSpecialty;

public interface AnalystSpecialtyRepository extends JpaRepository<AnalystSpecialty, Integer> {

    List<AnalystSpecialty> findByAnalystId(Integer analystId);

    void deleteByAnalystId(Integer analystId);
    
    List<AnalystSpecialty> findByAnalyst(Analyst analyst);
}

package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Specialty;

public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {
    // 已內建 findById(Integer id) 方法，不需額外寫
}

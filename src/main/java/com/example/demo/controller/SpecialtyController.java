package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.AnalystSpecialty;
import com.example.demo.entity.Specialty;
import com.example.demo.repository.AnalystSpecialtyRepository;
import com.example.demo.repository.SpecialtyRepository;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api/specialties")
public class SpecialtyController {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    private AnalystSpecialtyRepository analystSpecialtyRepository;

    // ✅ 1. 所有專長（無需登入）
    @GetMapping
    public ResponseEntity<?> getAllSpecialties() {
        try {
            List<Specialty> list = specialtyRepository.findAll();
            System.out.println("✅ 找到專長數量：" + list.size());
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("取得專長失敗：" + e.getMessage());
        }
    }

    // ✅ 2. 取得特定分析師的專長名稱清單（需登入或 userId）
    @GetMapping("/{analystId}")
    public ResponseEntity<?> getSpecialtiesByAnalyst(@PathVariable Integer analystId) {
        try {
            List<AnalystSpecialty> relations = analystSpecialtyRepository.findByAnalystId(analystId);
            List<String> specialtyNames = new ArrayList<>();

            for (AnalystSpecialty as : relations) {
                Specialty s = as.getSpecialty();
                if (s != null) {
                    specialtyNames.add(s.getName());
                }
            }

            return ResponseEntity.ok(specialtyNames);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("分析師專長查詢失敗：" + e.getMessage());
        }
    }

    @PostConstruct
    public void init() {
        System.out.println("✅ SpecialtyController 已載入");
    }
}
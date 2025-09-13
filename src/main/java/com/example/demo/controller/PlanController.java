package com.example.demo.controller;

import com.example.demo.dto.PlanRequestDto;
import com.example.demo.dto.PlanResponseDto;
import com.example.demo.service.PlanService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@CrossOrigin // 如有前後端分離，允許跨域
public class PlanController {

    private final PlanService planService;
    
    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    // 上架新訂閱方案
//     @PostMapping
//     public ResponseEntity<PlanResponseDto> createPlan(@RequestBody PlanRequestDto requestDto) {
//         Plan plan = planService.createPlan(
//                 // requestDto.getAnalystId(),
//                 requestDto.getName(),
//                 requestDto.getDescription(),
//                 requestDto.getPrice()
//         );

//         PlanResponseDto responseDto = PlanResponseDto.builder()
//                 .id(plan.getId())
//                 .name(plan.getName())
//                 .description(plan.getDescription())
//                 .price(plan.getPrice())
//                 .status(plan.getStatus())
//                 // .analystId(plan.getAnalyst().getId())
//                 .build();

//         return ResponseEntity.ok(responseDto);
//     }

    // 查詢某分析師所有訂閱方案
//     @GetMapping("/analyst/{id}")
//     public ResponseEntity<List<PlanResponseDto>> getPlansByAnalyst(@PathVariable Integer id) {
//         List<Plan> plans = planService.getPlansByAnalystId(id);

//         List<PlanResponseDto> result = plans.stream()
//                 .map(plan -> PlanResponseDto.builder()
//                         .id(plan.getId())
//                         .name(plan.getName())
//                         .description(plan.getDescription())
//                         .price(plan.getPrice())
//                         .status(plan.getStatus())
//                         .analystId(plan.getAnalyst().getId())
//                         .build()
//                 )
//                 .collect(Collectors.toList());

//         return ResponseEntity.ok(result);
//     }
        // @GetMapping("/analyst/{analystId}")
        // public List<Plan> getPlansByAnalyst(@PathVariable Integer analystId) {
        //         return planService.getPlansByAnalystId(analystId);
        // }
    @GetMapping("/analyst/{analystId}")
    public List<PlanResponseDto> getPlansByAnalyst(@PathVariable Integer analystId) {
        return planService.getPlansByAnalystId(analystId);
    }

    // 切換方案狀態
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> togglePlanStatus(@PathVariable Integer id) {
        planService.togglePlanStatus(id);
        return ResponseEntity.ok().build(); // 204 No Content 也可以
    }

    // 修改方案內容
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePlan(@PathVariable Integer id, @RequestBody PlanRequestDto dto) {
        planService.updatePlan(id, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> createPlan(@RequestBody PlanRequestDto dto) {
        planService.createPlan(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }





}


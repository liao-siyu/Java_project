package com.example.demo.controller;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.MarketHistoricalService;
import com.example.demo.service.MarketTwseStockService;

@RestController
@RequestMapping("/api/stock")
public class MarketStockController {

    private final MarketTwseStockService twseStockService;
    private final MarketHistoricalService finMindService;

    public MarketStockController(MarketTwseStockService twseStockService, MarketHistoricalService finMindService) {
        this.twseStockService = twseStockService;
        this.finMindService = finMindService;
    }

    @GetMapping
    public Object getStockInfo(
            @RequestParam String stockId,
            @RequestParam String type,
            @RequestParam(required = false) String queryDate) throws Exception {

        if ("twse".equalsIgnoreCase(type) || "realtime".equalsIgnoreCase(type)) {
            return twseStockService.getRealtimeStock(stockId);
        } else if ("finmind".equalsIgnoreCase(type) || "historical".equalsIgnoreCase(type)) {
            if (queryDate == null) throw new IllegalArgumentException("queryDate 不能為空");
            return finMindService.getHistoricalStockData(stockId, LocalDate.parse(queryDate));
        } else {
            throw new IllegalArgumentException("不支援的 type 參數：" + type);
        }
    }
}

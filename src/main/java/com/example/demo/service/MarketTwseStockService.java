package com.example.demo.service;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.List;

import com.example.demo.dto.MarketTwseStockDTO;
import com.google.common.util.concurrent.RateLimiter;

import jakarta.annotation.PostConstruct;

@Service
public class MarketTwseStockService {
    private static final Logger logger = LoggerFactory.getLogger(MarketTwseStockService.class);

    private Set<String> validStockCodes; // 缓存有效的股票代码
    
    @PostConstruct
    public void init() {
        try {
            validStockCodes = stockInfoService.getAllValidStockCodes();
            logger.info("Loaded {} valid stock codes", validStockCodes);
        } catch (Exception e) {
            logger.error("Failed to load stock codes, using empty set", e);
            validStockCodes = Set.of(); // Empty set as fallback
        }
    }
    
    // 檢查股票代碼是否有效
    public boolean isValidStock(String symbol) {	
        return validStockCodes != null && (validStockCodes.contains(symbol) || 
                validStockCodes.contains("tse_" + symbol + ".tw"));
    }

    
    
 // 限制每10秒1次请求
    private final RateLimiter twseRateLimiter = RateLimiter.create(0.1);
    
    // FinMind 配置
    private final String finmindToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJkYXRlIjoiMjAyNS0wNy0yOSAxNTowOToyNSIsInVzZXJfaWQiOiJzaXl1MTIxM0BpY2xvdWQuY29tIiwiaXAiOiI2MS4yMTYuMTM5LjExNSJ9.j6gdd5r-yRK3HU7pcCI7eETnEgFwsX0eyaNEyNLHU4U";
    private final WebClient finmindClient = WebClient.create("https://api.finmindtrade.com");

    // TWSE 配置
    private static final String TWSE_STOCK_URL = "https://mis.twse.com.tw/stock/api/getStockInfo.jsp?ex_ch=tse_%s.tw&json=1&delay=0";

    @Autowired
    private MarketStockInfoService stockInfoService;
    
    public boolean isMarketOpen() {
        LocalDate today = LocalDate.now();
        return isWeekday(today) && isNowBetween("09:00", "13:30");
    }
    
    public MarketTwseStockDTO getRealtimeStock(String stockId) throws Exception {
        LocalDate today = LocalDate.now();
        boolean isTradingTime = isWeekday(today) && isNowBetween("09:00", "13:30");

        MarketTwseStockDTO dto = new MarketTwseStockDTO();
        dto.setStock_id(stockId);
        dto.setDate(today.format(DateTimeFormatter.ISO_DATE));

        try {
            dto.setStockName(stockInfoService.getStockName(stockId));
        } catch (Exception e) {
            logger.warn("獲取股票名稱失敗: {}", e.getMessage());
            dto.setStockName("N/A");
        }

        if (isTradingTime) {
            logger.info("交易時段，從TWSE獲取即時數據: {}", stockId);
            return parseFromTWSE(stockId, dto);
        } else {
            logger.info("非交易時段，從FinMind獲取歷史數據: {}", stockId);
            return fetchFromFinMind(stockId, dto);
        }
    }

    private MarketTwseStockDTO parseFromTWSE(String stockId, MarketTwseStockDTO dto) throws Exception {
     
    	twseRateLimiter.acquire();// 阻塞直到可以呼叫 TWSE API
    	
        if (!twseRateLimiter.tryAcquire(5, TimeUnit.SECONDS)) {
            logger.warn("TWSE API请求过于频繁，转为使用FinMind");
            return fetchFromFinMind(stockId, dto);
        }
        
        String url = String.format(TWSE_STOCK_URL, stockId);
        try {
            // 使用TWSE API獲取JSON數據（替代網頁爬蟲）
            String jsonResponse = Jsoup.connect(url)
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0")
                .timeout(5000)
                .execute()
                .body();

            // 解析JSON數據
            Map<String, Object> response = JsonParser.parseJson(jsonResponse);
            List<Map<String, String>> msgArray = (List<Map<String, String>>) response.get("msgArray");
            
            if (msgArray == null || msgArray.isEmpty()) {
                throw new RuntimeException("TWSE API返回無效數據");
            }

            Map<String, String> stockData = msgArray.get(0);
            
            dto.setOpen(toBigDecimal(stockData.get("o")));
            dto.setHigh(toBigDecimal(stockData.get("h")));
            dto.setLow(toBigDecimal(stockData.get("l")));
            dto.setClose(toBigDecimal(stockData.get("z")));
            dto.setTrading_Volume(toLong(stockData.get("v")));
            dto.setTime(stockData.get("t"));
            dto.setIsClosed(false);

            // 計算漲跌幅
            BigDecimal prevClose = toBigDecimal(stockData.get("y"));
            if (prevClose != null && dto.getClose() != null) {
                BigDecimal change = dto.getClose().subtract(prevClose);
                dto.setChange(change);
                dto.setPercent(change.divide(prevClose, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")));
                dto.setPreviousClose(prevClose);
            }

            return dto;
        } catch (Exception e) {
            logger.error("TWSE數據獲取失敗: {}", e.getMessage());
            throw e;
        }
    }

    private MarketTwseStockDTO fetchFromFinMind(String stockId, MarketTwseStockDTO dto) throws Exception {
        String today = LocalDate.now().toString();
        String startDate = LocalDate.now().minusDays(30).toString(); // 擴大查詢範圍
        
        try {
            Map<String, Object> response = finmindClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v4/data")
                    .queryParam("dataset", "TaiwanStockPrice")
                    .queryParam("data_id", stockId)
                    .queryParam("start_date", startDate)
                    .queryParam("end_date", today)
                    .queryParam("token", finmindToken)
                    .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

            List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("data");
            if (dataList == null || dataList.isEmpty()) {
                throw new RuntimeException("FinMind無有效數據");
            }

            // 獲取最新數據
            Map<String, Object> latestData = dataList.get(dataList.size() - 1);
            dto.setOpen(toBigDecimal(latestData.get("open")));
            dto.setHigh(toBigDecimal(latestData.get("max")));
            dto.setLow(toBigDecimal(latestData.get("min")));
            dto.setClose(toBigDecimal(latestData.get("close")));
            dto.setTrading_Volume(toLong(latestData.get("Trading_Volume")));
            dto.setIsClosed(true);

            // 計算漲跌幅（使用前一日收盤價）
            if (dataList.size() >= 2) {
                Map<String, Object> prevData = dataList.get(dataList.size() - 2);
                BigDecimal prevClose = toBigDecimal(prevData.get("close"));
                if (prevClose != null && dto.getClose() != null) {
                    BigDecimal change = dto.getClose().subtract(prevClose);
                    dto.setChange(change);
                    dto.setPercent(change.divide(prevClose, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")));
                    dto.setPreviousClose(prevClose);
                }
            }

            return dto;
        } catch (Exception e) {
            logger.error("FinMind數據獲取失敗: {}", e.getMessage());
            throw new RuntimeException("無法從任何數據源獲取股票數據");
        }
    }
    
    public Map<String, BigDecimal> getCurrentPrices(List<String> symbols) {
        Map<String, BigDecimal> result = new HashMap<>();
        
        for (String symbol : symbols) {
            try {
                MarketTwseStockDTO dto = getRealtimeStock(symbol);
                result.put(symbol, dto.getClose()); // 使用收盤價作為當前價格
            } catch (Exception e) {
                logger.warn("獲取股票 {} 價格失敗: {}", symbol, e.getMessage());
                result.put(symbol, null); // 標記為獲取失敗
            }
        }
        
        return result;
    }

    // 輔助方法
    private boolean isWeekday(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY;
    }

    private boolean isNowBetween(String start, String end) {
        LocalTime now = LocalTime.now();
        return !now.isBefore(LocalTime.parse(start)) && !now.isAfter(LocalTime.parse(end));
    }

    private BigDecimal toBigDecimal(Object val) {
        try {
            return val == null ? null : new BigDecimal(val.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Long toLong(Object val) {
        try {
            return val == null ? null : Long.parseLong(val.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
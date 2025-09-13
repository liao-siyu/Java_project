package com.example.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.dto.MarketHistoricalStockDTO;

@Service
public class MarketHistoricalService {

	private final RestTemplate restTemplate = new RestTemplate();
	
	private final WebClient webClient = WebClient.create("https://api.finmindtrade.com");

	private final String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJkYXRlIjoiMjAyNS0wNy0yOSAxNTowOToyNSIsInVzZXJfaWQiOiJzaXl1MTIxM0BpY2xvdWQuY29tIiwiaXAiOiI2MS4yMTYuMTM5LjExNSJ9.j6gdd5r-yRK3HU7pcCI7eETnEgFwsX0eyaNEyNLHU4U";

	@Autowired
	private MarketStockInfoService stockInfoService;
	
	
	public MarketHistoricalStockDTO getHistoricalStockData(String stockId, LocalDate date) throws Exception {
		Map<String, Object> response = webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/api/v4/data").queryParam("dataset", "TaiwanStockPrice")
						.queryParam("data_id", stockId).queryParam("start_date", date.toString())
						.queryParam("end_date", date.toString()).queryParam("token", token).build())
				.retrieve().bodyToMono(Map.class).block();

		if (response == null || !response.containsKey("data")) {
			throw new Exception("FinMind API 無資料回傳");
		}

		List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("data");

		Optional<Map<String, Object>> target = dataList.stream()
				.filter(item -> date.toString().equals(item.get("date"))).findFirst();

		if (!target.isPresent()) {
			throw new Exception("查無指定日期的歷史資料");
		}

		Map<String, Object> stockData = target.get();

		MarketHistoricalStockDTO dto = new MarketHistoricalStockDTO();
		
        dto.setStockId(stockId);
		dto.setDate((String) stockData.get("date"));
		dto.setOpen(toBigDecimal(stockData.get("open")));
		dto.setMax(toBigDecimal(stockData.get("max")));
		dto.setMin(toBigDecimal(stockData.get("min")));
		dto.setClose(toBigDecimal(stockData.get("close")));
		dto.setChange(toBigDecimal(stockData.get("change")));
		dto.setPercent(toBigDecimal(stockData.get("percent")));
		dto.setTradingVolume(toLong(stockData.get("Trading_Volume")));
		
		// ✅ 股票名稱
	    try {
	        System.out.println("正在查詢股票 ID: " + stockId); // 除錯用
	        String name = stockInfoService.getStockName(stockId);
	        dto.setStockName(name);
	    } catch (Exception e) {
	        System.err.println("股票名稱查詢失敗: " + e.getMessage()); // 除錯用
	        dto.setStockName("查無名稱");
	    }
		
		if (dto.getClose() != null && dto.getPreviousClose() != null) {
			BigDecimal changeFromPrev = dto.getClose().subtract(dto.getPreviousClose());
			dto.setChange(changeFromPrev);

			if (dto.getPreviousClose().compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal percentFromPrev = changeFromPrev
						.divide(dto.getPreviousClose(), 4, RoundingMode.HALF_UP)
						.multiply(BigDecimal.valueOf(100));
				dto.setPercent(percentFromPrev);
			}
		}

		if (dto.getOpen() != null && dto.getClose() != null) {
			BigDecimal change = dto.getClose().subtract(dto.getOpen());
			dto.setChange(change);

			if (dto.getOpen().compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal percent = change.divide(dto.getOpen(), 4, RoundingMode.HALF_UP)
						.multiply(BigDecimal.valueOf(100));
				dto.setPercent(percent);
			}
		}
		BigDecimal previousClose = getPreviousClosePrice(stockId, date);
		dto.setPreviousClose(previousClose);


		
		return dto;
	}
	
	private BigDecimal getPreviousClosePrice(String stockId, LocalDate date) {
		LocalDate startDate = date.minusDays(14);
		LocalDate endDate = date.minusDays(1);

		Map<String, Object> response = webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/api/v4/data")
						.queryParam("dataset", "TaiwanStockPrice")
						.queryParam("data_id", stockId)
						.queryParam("start_date", startDate.toString())
						.queryParam("end_date", endDate.toString())
						.queryParam("token", token)
						.build())
				.retrieve().bodyToMono(Map.class).block();

		if (response != null && response.containsKey("data")) {
			List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("data");

			// 依日期倒序排序，取最近一筆有收盤價資料的
			return dataList.stream()
					.sorted((a, b) -> ((String) b.get("date")).compareTo((String) a.get("date")))
					.map(item -> toBigDecimal(item.get("close")))
					.filter(close -> close != null)
					.findFirst()
					.orElse(null);
		}

		return null;
	}

	private BigDecimal toBigDecimal(Object val) {
		if (val == null)
			return null;
		try {
			return new BigDecimal(val.toString());
		} catch (Exception e) {
			return null;
		}
	}

	private Long toLong(Object val) {
		if (val == null)
			return null;
		try {
			return Long.parseLong(val.toString());
		} catch (Exception e) {
			return null;
		}
	}
}

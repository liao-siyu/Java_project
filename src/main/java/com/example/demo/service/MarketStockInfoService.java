package com.example.demo.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;

@Service
public class MarketStockInfoService {

	private final WebClient webClient ;

	private final String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJkYXRlIjoiMjAyNS0wNy0yOSAxNTowOToyNSIsInVzZXJfaWQiOiJzaXl1MTIxM0BpY2xvdWQuY29tIiwiaXAiOiI2MS4yMTYuMTM5LjExNSJ9.j6gdd5r-yRK3HU7pcCI7eETnEgFwsX0eyaNEyNLHU4U";

	public MarketStockInfoService() {
        this.webClient = WebClient.builder()
            .baseUrl("https://api.finmindtrade.com")
            .defaultHeader(HttpHeaders.ACCEPT_CHARSET, "UTF-8")
            .exchangeStrategies(ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build())
            .build();
    }
	
	public String getStockName(String stockId) throws Exception {
		try {String responseJson = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v4/data")
                        .queryParam("dataset", "TaiwanStockInfo")
                        .queryParam("data_id", stockId)
                        .queryParam("token", token)
                        .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .acceptCharset(StandardCharsets.UTF_8)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> response = mapper.readValue(responseJson, Map.class);

                List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("data");
                if (dataList == null || dataList.isEmpty()) {
                    throw new Exception("無此股票資料: " + stockId);
                }

                String stockName = dataList.stream()
                    .filter(item -> stockId.equals(String.valueOf(item.get("stock_id"))))
                    .map(item -> (String) item.get("stock_name"))
                    .findFirst()
                    .orElse(stockId);

                // 強制轉換編碼（如果亂碼仍未解決）
                if (stockName != null && stockName.contains("�")) {
                    stockName = new String(stockName.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                }

                return stockName;
            } catch (Exception e) {
                throw new Exception("獲取股票名稱失敗: " + e.getMessage());
            }
	}
	
	public Set<String> getAllValidStockCodes() throws Exception {
	    try {
	        String responseJson = webClient.get()
	            .uri(uriBuilder -> uriBuilder.path("/api/v4/data")
	                .queryParam("dataset", "TaiwanStockInfo")
	                .queryParam("token", token)
	                .build())
	            .accept(MediaType.APPLICATION_JSON)
	            .retrieve()
	            .bodyToMono(String.class)
	            .block();

	        ObjectMapper mapper = new ObjectMapper();
	        Map<String, Object> response = mapper.readValue(responseJson, Map.class);

	        List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("data");
	        if (dataList == null || dataList.isEmpty()) {
	            throw new Exception("No stock data available");
	        }

	        return dataList.stream()
	            .map(item -> String.valueOf(item.get("stock_id")))
	            .collect(Collectors.toSet());
	    } catch (Exception e) {
	        throw new Exception("Failed to load stock codes: " + e.getMessage());
	    }
	}
}

package com.example.demo.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.MarketTwseStockDTO;
import com.example.demo.model.Watchlist;
import com.example.demo.service.MarketTwseStockService;
import com.example.demo.service.WatchlistService;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

	@Autowired
	private WatchlistService watchlistService;
	
	 @Autowired
	    private MarketTwseStockService marketTwseStockService;
 
	@GetMapping("/with-details")
	public ResponseEntity<List<MarketTwseStockDTO>> getWatchlistWithDetails(@RequestParam Long userId) {
	    try {
	        List<Watchlist> watchlists = watchlistService.getWatchlistByUserId(userId);
	        
	        // 使用 parallelStream 加速處理
	        List<MarketTwseStockDTO> result = watchlists.parallelStream()
	            .map(watchlist -> {
	                MarketTwseStockDTO dto = new MarketTwseStockDTO();
	                dto.setStock_id(watchlist.getSymbol());
	                
	                try {
	                    // 獲取即時數據
	                    MarketTwseStockDTO stockData = marketTwseStockService.getRealtimeStock(watchlist.getSymbol());
	                    
	                    // 複製所有屬性 (使用 BeanUtils 或手動設置)
	                    BeanUtils.copyProperties(stockData, dto);
	                    
	                } catch (Exception e) {
	                    // 錯誤處理
	                    dto.setStockName("資料獲取失敗");
	                    dto.setOpen(null);
	                    dto.setHigh(null);
	                    // 設置其他字段為 null...
	                }
	                
	                return dto;
	            })
	            .collect(Collectors.toList());
	        
	        return ResponseEntity.ok(result);
	        
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(Collections.emptyList());
	    }
	}

	@PostMapping
	public ResponseEntity<?> addWatchlist(@RequestBody Map<String, String> body) {
		try {
			Long userId = Long.parseLong(body.get("userId").toString());
			String symbol = body.get("stockId");
			watchlistService.addWatchlist(userId, symbol);
			return ResponseEntity.ok(Map.of("message", "加入成功"));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage())); // 統一使用 message 欄位
		}
	}

	@DeleteMapping
	public ResponseEntity<?> removeWatchlist(@RequestBody Map<String, String> body) {
		try {
			Long userId = Long.parseLong(body.get("userId").toString());
			String symbol = body.get("stockId");
			// 先檢查是否存在
			if (!watchlistService.existsByUserIdAndSymbol(userId, symbol)) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "該股票不在觀察列表中"));
			}

			// 執行刪除
			watchlistService.removeWatchlist(userId, symbol);
			return ResponseEntity.ok(Map.of(
	                "message", "已成功移除",
	                "removedSymbol", symbol));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage())); // 統一使用 message 欄位
		}
	}
}

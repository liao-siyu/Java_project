package com.example.demo.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.PlaceOrderRequest;
import com.example.demo.dto.PositionResponse;
import com.example.demo.model.Order;
import com.example.demo.model.Position;
import com.example.demo.model.RealizedProfit;
import com.example.demo.service.MarketTwseStockService;
import com.example.demo.service.TradeService;

@RestController
@RequestMapping("/api/trade")
public class TradeController {
    
    @Autowired
    private TradeService tradeService;
    private MarketTwseStockService marketTwseStockService;
    
    @Autowired
    public TradeController(
        TradeService tradeService,
        MarketTwseStockService marketTwseStockService
    ) {
        this.tradeService = tradeService;
        this.marketTwseStockService = marketTwseStockService;
    }
    
    @PostMapping("/place-order")
    public ResponseEntity<?> placeOrder(@RequestBody PlaceOrderRequest request) {
        try {
            Order order = tradeService.placeOrder(
                request.getAccountId(),
                request.getSymbol(),
                request.getOrderType(),
                request.getQuantity(),
                request.getPrice()
            );
            
            return ResponseEntity.ok(new ApiResponse(true, "訂單已提交", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    @PostMapping("/cancel-order/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        try {
            tradeService.cancelOrder(orderId);
            return ResponseEntity.ok(new ApiResponse(true, "訂單已取消", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    @GetMapping("/orders/{accountId}")
    public ResponseEntity<?> getOrders(@PathVariable Long accountId, 
                                     @RequestParam(required = false) String status) {
        try {
            List<Order> orders;
            if (status != null) {
                orders = tradeService.getOrdersByStatus(accountId, status);
            } else {
                orders = tradeService.getAllOrders(accountId);
            }
            return ResponseEntity.ok(new ApiResponse(true, "成功", orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    @GetMapping("/positions/{accountId}")
    public ResponseEntity<?> getPositions(@PathVariable Long accountId) {
        try {
            List<Position> positions = tradeService.getPositions(accountId);
            
            // 批量獲取當前價格 (使用現有服務)
            List<String> symbols = positions.stream()
                                          .map(Position::getSymbol)
                                          .distinct()
                                          .collect(Collectors.toList());
                                          
            Map<String, BigDecimal> currentPrices = marketTwseStockService.getCurrentPrices(symbols);
            
            List<PositionResponse> response = positions.stream().map(position -> {
                PositionResponse dto = new PositionResponse();
                dto.setSymbol(position.getSymbol());
                dto.setQuantity(position.getQuantity());
                dto.setAverageCost(position.getAverageCost());
                
                // 處理價格獲取失敗情況
                BigDecimal currentPrice = currentPrices.get(position.getSymbol());
                if (currentPrice == null) {
                    throw new RuntimeException("無法獲取 " + position.getSymbol() + " 的當前價格");
                }
                
                dto.setCurrentPrice(currentPrice);
                dto.setUnrealizedProfit(
                    currentPrice.subtract(position.getAverageCost())
                              .multiply(new BigDecimal(position.getQuantity()))
                );
                return dto;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(new ApiResponse(true, "成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                   .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    @GetMapping("/realized-profit/{accountId}")
    public ResponseEntity<?> getRealizedProfit(@PathVariable Long accountId) {
        try {
            List<RealizedProfit> profits = tradeService.getRealizedProfits(accountId);
            System.out.println("Returning realized profits: " + profits);
            return ResponseEntity.ok(new ApiResponse(true, "成功", profits));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}
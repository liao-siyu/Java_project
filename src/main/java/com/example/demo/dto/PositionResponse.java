package com.example.demo.dto;

import java.math.BigDecimal;

public class PositionResponse {

	private String symbol;
    private int quantity;
    private BigDecimal averageCost;
    private BigDecimal currentPrice; // 從股票API獲取
    private BigDecimal unrealizedProfit;
    
 // 計算未實現損益
//    public BigDecimal calculateProfit() {
//        return currentPrice.subtract(averageCost)
//                           .multiply(new BigDecimal(quantity));
//    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(BigDecimal averageCost) {
        this.averageCost = averageCost;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getUnrealizedProfit() {
        return unrealizedProfit;
    }

    public void setUnrealizedProfit(BigDecimal unrealizedProfit) {
        this.unrealizedProfit = unrealizedProfit;
    }
}

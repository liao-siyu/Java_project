package com.example.demo.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MarketTwseStockDTO {

    private String stockId;           // 股票代碼
    private String date;               // 日期
    private String time;               // 時間（FinMind 無提供，可留空）
    private BigDecimal open;               // 開盤價
    private BigDecimal high;               // 最高價
    private BigDecimal low;                // 最低價
    private BigDecimal close;              // 收盤價
    private BigDecimal change;             // 漲跌價差（需額外計算）
    private BigDecimal percent;            // 漲跌百分比（需額外計算）
    
    @JsonProperty("trading_Volume")
    private Long trading_Volume;     // 成交量
    private boolean isClosed;          // 是否收盤
    private BigDecimal previousClose; // 前一日收盤價
    private String stockName;
    private String symbol;
    

	public String getStockName() {
	    return stockName;
	}

	public void setStockName(String stockName) {
	    this.stockName = stockName;
	}

    public BigDecimal getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(BigDecimal previousClose) {
        this.previousClose = previousClose;
    }


    public String getStock_id() {
        return stockId;
    }

    public void setStock_id(String stockId) {
        this.stockId = stockId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getChange() {
        return change;
    }

    public void setChange(BigDecimal change) {
        this.change = change;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    public Long getTrading_Volume() {
        return trading_Volume;
    }

    public void setTrading_Volume(Long trading_Volume) {
    	this.trading_Volume = trading_Volume;
    }

    public boolean isIsClosed() {
        return isClosed;
    }

    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}

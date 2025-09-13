package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class MarketHistoricalStockDTO {

	@JsonProperty("stock_id")
	private String stockId;

	@JsonProperty("date")
	private String date;

	@JsonProperty("open")
	private BigDecimal open;

	@JsonProperty("max")
	private BigDecimal max;

	@JsonProperty("min")
	private BigDecimal min;

	@JsonProperty("close")
	private BigDecimal close;

	@JsonProperty("diff")
	private BigDecimal diff;

	@JsonProperty("percent")
	private BigDecimal percent;

	@JsonProperty("trading_Volume")
	private Long tradingVolume;
	private BigDecimal previousClose; // 前一日收盤價
	private String stockName;

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

	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public BigDecimal getOpen() {
		return open;
	}

	public void setOpen(BigDecimal open) {
		this.open = open;
	}

	public BigDecimal getMax() {
		return max;
	}

	public void setMax(BigDecimal max) {
		this.max = max;
	}

	public BigDecimal getMin() {
		return min;
	}

	public void setMin(BigDecimal min) {
		this.min = min;
	}

	public BigDecimal getClose() {
		return close;
	}

	public void setClose(BigDecimal close) {
		this.close = close;
	}

	public BigDecimal getChange() {
		return diff;
	}

	public void setChange(BigDecimal change) {
		this.diff = change;
	}

	public BigDecimal getPercent() {
		return percent;
	}

	public void setPercent(BigDecimal percent) {
		this.percent = percent;
	}

	public Long getTradingVolume() {
		return tradingVolume;
	}

	public void setTradingVolume(Long tradingVolume) {
		this.tradingVolume = tradingVolume;
	}
}

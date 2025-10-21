package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "positions")
public class Position {		// 代表使用者在某個股票的持倉狀況

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "positions_id")  // 明確指定欄位名稱
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "average_cost", precision = 38, scale = 2)
    private BigDecimal averageCost;
//
//    @Column(name = "frozen_quantity", nullable = false)
//    private int frozenQuantity;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

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

//	public int getFrozenQuantity() {
//		return frozenQuantity;
//	}
//
//	public void setFrozenQuantity(int frozenQuantity) {
//		this.frozenQuantity = frozenQuantity;
//	}

}

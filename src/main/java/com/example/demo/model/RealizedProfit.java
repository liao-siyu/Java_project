package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "realized_profits")
public class RealizedProfit {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "cost_basis", precision = 38, scale = 2)
    private BigDecimal costBasis;

    @Column(name = "proceeds", precision = 38, scale = 2)
    private BigDecimal proceeds;

    @Column(name = "profit", precision = 38, scale = 2)
    private BigDecimal profit;

    @Column(name = "transaction_date", columnDefinition = "datetime(6)")
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate;

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

	public BigDecimal getCostBasis() {
		return costBasis;
	}

	public void setCostBasis(BigDecimal costBasis) {
		this.costBasis = costBasis;
	}

	public BigDecimal getProceeds() {
		return proceeds;
	}

	public void setProceeds(BigDecimal proceeds) {
		this.proceeds = proceeds;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}
	
	public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

}

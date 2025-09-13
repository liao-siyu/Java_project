package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id")
    private Long id;

    @Column(name = "account_id")
    private Long accountId;
    
    private String symbol;
    
    @Column(name = "order_type")
    private String orderType; // "buy" 或 "sell"
    
    private int quantity;
    
    private BigDecimal price;
    
    private String status; // "pending", "filled", "cancelled"

    @Column(name = "execution_price")
    private BigDecimal executionPrice;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "filled_at")
    private Date filledAt;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
    
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

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getExecutionPrice() {
		return executionPrice;
	}

	public void setExecutionPrice(BigDecimal executionPrice) {
		this.executionPrice = executionPrice;
	}
	
	public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getFilledAt() {
        return filledAt;
    }

    public void setFilledAt(Date filledAt) {
        this.filledAt = filledAt;
    }

}

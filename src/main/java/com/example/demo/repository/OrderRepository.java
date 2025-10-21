package com.example.demo.repository;

import com.example.demo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	//由 Spring Data JPA 自動生成 SQL 查詢方法
	// SELECT * FROM orders WHERE account_id = ?
	List<Order> findByAccountId(Long accountId);
	
	// SELECT * FROM orders WHERE account_id = ? AND status = ?
    List<Order> findByAccountIdAndStatus(Long accountId, String status);
    
    // SELECT * FROM orders WHERE status = ?
    List<Order> findByStatus(String status);
}
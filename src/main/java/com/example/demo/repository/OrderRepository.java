package com.example.demo.repository;

import com.example.demo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByAccountId(Long accountId);
    List<Order> findByAccountIdAndStatus(Long accountId, String status);
    List<Order> findByStatus(String status);
}
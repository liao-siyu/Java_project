package com.example.demo.repository;

import com.example.demo.model.RealizedProfit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RealizedProfitRepository extends JpaRepository<RealizedProfit, Long> {
	
	// 根據 accountId 查找所有 RealizedProfit 記錄
	//SQL: SELECT * FROM realized_profits WHERE account_id = ?
    List<RealizedProfit> findByAccountId(Long accountId);	
}
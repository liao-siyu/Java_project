package com.example.demo.repository;

import com.example.demo.model.RealizedProfit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RealizedProfitRepository extends JpaRepository<RealizedProfit, Long> {
    List<RealizedProfit> findByAccountId(Long accountId);
    List<RealizedProfit> findByAccountIdAndSymbol(Long accountId, String symbol);
}
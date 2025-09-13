package com.example.demo.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.AccountOverview;

@Repository
public interface AccountOverviewRepository extends JpaRepository<AccountOverview, Long> {
    
	@Query("SELECT a FROM AccountOverview a WHERE a.userId = :userId AND a.isActive = true")
    Optional<AccountOverview> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT a.balance FROM AccountOverview a WHERE a.userId = :userId AND a.isActive = true")
    Optional<BigDecimal> findBalanceByUserId(@Param("userId") Long userId);
}
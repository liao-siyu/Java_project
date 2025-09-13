package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Watchlist;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
	
	boolean existsByUserIdAndSymbol(Long userId, String symbol);
	
	Optional<Watchlist> findByUserIdAndSymbol(Long userId, String symbol);
    List<Watchlist> findByUserId(Long userId);
    @Transactional
    @Modifying
    @Query("DELETE FROM Watchlist w WHERE w.userId = :userId AND w.symbol = :symbol")
    void deleteByUserIdAndSymbol(@Param("userId") Long userId, 
                               @Param("symbol") String symbol);
}

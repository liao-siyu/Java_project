package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Watchlist;
import com.example.demo.repository.WatchlistRepository;

@Service
@Transactional
public class WatchlistService {

	@Autowired
	private WatchlistRepository watchlistRepository;

	public List<Watchlist> getWatchlistByUserId(Long userId) {
		return watchlistRepository.findByUserId(userId);
	}

	public Watchlist addWatchlist(Long userId, String symbol) {

		if (userId == null)
			throw new IllegalArgumentException("使用者ID不能為空");
		if (symbol == null || symbol.trim().isEmpty())
			throw new IllegalArgumentException("股票代碼不能為空");

		// 防止重複加入
		if (watchlistRepository.findByUserIdAndSymbol(userId, symbol).isPresent()) {
			throw new RuntimeException("該股票已加入觀察列表");
		}

		Watchlist w = new Watchlist();
		w.setUserId(userId);
		w.setSymbol(symbol);
		return watchlistRepository.save(w);
	}
	
	public boolean existsByUserIdAndSymbol(Long userId, String symbol) {
        return watchlistRepository.existsByUserIdAndSymbol(userId, symbol);
    }

	public void removeWatchlist(Long userId, String symbol) {
		watchlistRepository.deleteByUserIdAndSymbol(userId, symbol);
	}
}

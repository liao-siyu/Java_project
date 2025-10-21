package com.example.demo.service;

import java.util.Date;
import java.util.HashMap;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.MarketTwseStockDTO;
import com.example.demo.model.Order;
import com.example.demo.model.Account;
import com.example.demo.model.Position;
import com.example.demo.model.RealizedProfit;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.PositionRepository;
import com.example.demo.repository.RealizedProfitRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TradeService {
	private static final Logger logger = LoggerFactory.getLogger(TradeService.class);

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PositionRepository positionRepository;

	@Autowired
	private MarketTwseStockService marketService;

	@Autowired
	private RealizedProfitRepository realizedProfitRepository; // 当前代码中已使用但未声明

	// 新增：最大重試次數
	private static final int MAX_RETRY = 3;

	// 查詢帳戶的指定狀態訂單
	public List<Order> getOrdersByStatus(Long accountId, String status) {
		return orderRepository.findByAccountIdAndStatus(accountId, status);
	}

	// 查詢帳戶的全部訂單
	public List<Order> getAllOrders(Long accountId) {
		return orderRepository.findByAccountId(accountId);
	}

	public List<Position> getPositions(Long accountId) {
		return positionRepository.findByAccountId(accountId);
	}

	public List<RealizedProfit> getRealizedProfits(Long accountId) {
		return realizedProfitRepository.findByAccountId(accountId);
	}

	// @Transactional 註解表示此方法內的所有資料庫操作將在同一個交易中執行
	@Transactional		
	public Order placeOrder(Long accountId, String symbol, String orderType, int quantity, BigDecimal price) {
		// 1. 驗證帳戶
		Account account = accountRepository.findById(accountId) // findById 是 JpaRepository 內建的方法
				.orElseThrow(() -> new IllegalArgumentException("帳戶不存在"));

		// 2. 驗證股票代碼
		if (!marketService.isValidStock(symbol)) {
			throw new IllegalArgumentException("無效的股票代碼");
		}

		// 3. 根據訂單類型進行不同驗證
		if (orderType.equals("buy")) {
			// 買單驗證：檢查帳戶餘額是否足夠
			BigDecimal totalCost = price.multiply(new BigDecimal(quantity));
			if (account.getBalance().compareTo(totalCost) < 0) {
				throw new IllegalArgumentException("帳戶餘額不足");
			}

			// 凍結資金
			account.setBalance(account.getBalance().subtract(totalCost));
			accountRepository.save(account); // save() 是 JpaRepository 內建的方法
		} else if (orderType.equals("sell")) {
			// 賣單驗證：檢查持有數量是否足夠
			Position position = positionRepository.findByAccountIdAndSymbol(accountId, symbol)
					.orElseThrow(() -> new IllegalArgumentException("沒有該股票的持倉"));

			if (position.getQuantity() < quantity) {
				throw new IllegalArgumentException("持有數量不足");
			}

		} else {
			throw new IllegalArgumentException("無效的訂單類型");
		}

		// 4. 創建訂單
		Order order = new Order();
		order.setAccountId(accountId);
		order.setSymbol(symbol);
		order.setOrderType(orderType);
		order.setQuantity(quantity);
		order.setPrice(price.setScale(2, RoundingMode.HALF_UP));
		order.setStatus("pending");
		order.setCreatedAt(new Date());

		return orderRepository.save(order);
	}

	@Transactional
	public void cancelOrder(Long orderId) {
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("訂單不存在"));

		if (!order.getStatus().equals("pending")) {
			throw new IllegalArgumentException("只有待處理的訂單可以取消");
		}

		// 根據訂單類型解凍資金或股票
		if (order.getOrderType().equals("buy")) {
			// 解凍資金
			Account account = accountRepository.findById(order.getAccountId())
					.orElseThrow(() -> new IllegalArgumentException("帳戶不存在"));

			BigDecimal totalCost = order.getPrice().multiply(new BigDecimal(order.getQuantity()));
			account.setBalance(account.getBalance().add(totalCost));
			accountRepository.save(account);
		} else {
			// 解凍股票
			Position position = positionRepository.findByAccountIdAndSymbol(order.getAccountId(), order.getSymbol())
					.orElseThrow(() -> new IllegalArgumentException("持倉不存在"));

			positionRepository.save(position);
		}

		// 更新訂單狀態
		order.setStatus("cancelled");
		orderRepository.save(order);
	}

	// 排程處理「待處理」訂單
	@Scheduled(fixedRate = 50000) // 每50秒執行一次
	public void processPendingOrders() {
		logger.info("開始處理待處理訂單...");

		// 只在開盤時間處理
		if (!marketService.isMarketOpen()) {
			logger.info("當前非交易時段，跳過處理");
			return;
		}

		try {
			// 讀取 pending 訂單清單
			List<Order> pendingOrders = orderRepository.findByStatus("pending");

			// 若沒有任何 pending 訂單，直接跳過
			if (pendingOrders.isEmpty()) {
				logger.info("目前無委託中訂單，跳過報價與撮合處理");
				return;
			}

			// Step 1: 分組所有股票代碼，因每檔只抓一次價
			Map<String, List<Order>> ordersBySymbol = pendingOrders.stream()
					.collect(Collectors.groupingBy(Order::getSymbol));

			// Step 2: 緩存股價
			Map<String, MarketTwseStockDTO> stockPriceCache = new HashMap<>();

			for (String symbol : ordersBySymbol.keySet()) {
				try {
					// 每支股票只抓一次報價
					MarketTwseStockDTO dto = marketService.getRealtimeStock(symbol);
					stockPriceCache.put(symbol, dto);
				} catch (Exception e) {
					logger.warn("無法取得股票 {} 報價，跳過其訂單: {}", symbol, e.getMessage());
				}
			}

			// Step 3: 執行訂單處理
			for (Order order : pendingOrders) {
				MarketTwseStockDTO dto = stockPriceCache.get(order.getSymbol());
				if (dto == null)
					continue; // 查不到價格就跳過

				try {
					processSingleOrderWithPrice(order, dto.getClose().setScale(2, RoundingMode.HALF_UP));
				} catch (Exception e) {
					logger.warn("處理訂單 {} 發生錯誤: {}", order.getId(), e.getMessage());
				}
			}

		} catch (Exception e) {
			logger.error("排程任務發生未預期錯誤", e);
		}

	}

	// 使用已知價格處理單一訂單
	@Transactional
	protected void processSingleOrderWithPrice(Order order, BigDecimal currentPrice) throws Exception {
		Order freshOrder = orderRepository.findById(order.getId()).orElseThrow(() -> new Exception("訂單不存在"));

		if (!"pending".equals(freshOrder.getStatus())) {
			throw new Exception("訂單狀態已變更為: " + freshOrder.getStatus());
		}

		BigDecimal orderPrice = freshOrder.getPrice().setScale(2, RoundingMode.HALF_UP);

		boolean shouldExecute = false;
		if ("buy".equals(freshOrder.getOrderType())) {
			shouldExecute = currentPrice.compareTo(orderPrice) <= 0;
		} else if ("sell".equals(freshOrder.getOrderType())) {
			shouldExecute = currentPrice.compareTo(orderPrice) >= 0;
		}

		if (shouldExecute) {
			executeOrder(freshOrder, currentPrice);
		}
	}

	// 帶重試機制的單一訂單處理
	private void processSingleOrderWithRetry(Order order, int retryCount) {
		for (int i = 0; i < retryCount; i++) {
			try {
				processSingleOrder(order);
				return; // 成功則退出重試
			} catch (Exception e) {
				logger.warn("訂單 {} 處理失敗 (重試 {}/{}): {}", order.getId(), i + 1, retryCount, e.getMessage());
				if (i == retryCount - 1) {
					logger.error("訂單 {} 達到最大重試次數", order.getId());
				}
			}
		}
	}

	// 使用即時價格處理單一訂單
	@Transactional
	protected void processSingleOrder(Order order) throws Exception {
		// 重新從DB加載最新狀態
		Order freshOrder = orderRepository.findById(order.getId()).orElseThrow(() -> new Exception("訂單不存在"));

		if (!"pending".equals(freshOrder.getStatus())) {
			throw new Exception("訂單狀態已變更為: " + freshOrder.getStatus());
		}

		MarketTwseStockDTO marketData = marketService.getRealtimeStock(freshOrder.getSymbol());
		BigDecimal currentPrice = marketData.getClose().setScale(2, RoundingMode.HALF_UP);
		BigDecimal orderPrice = freshOrder.getPrice().setScale(2, RoundingMode.HALF_UP);

		logger.info("檢查訂單 {}: 委託價={}, 市價={}", freshOrder.getId(), orderPrice, currentPrice);

		boolean shouldExecute = false;
		if ("buy".equals(freshOrder.getOrderType())) {
			shouldExecute = currentPrice.compareTo(orderPrice) <= 0;
		} else if ("sell".equals(freshOrder.getOrderType())) {
			shouldExecute = currentPrice.compareTo(orderPrice) >= 0;
		}

		if (shouldExecute) {
			executeOrder(freshOrder, currentPrice);
		}
	}

	// 執行訂單
	@Transactional
	protected void executeOrder(Order order, BigDecimal executionPrice) {
		// 再次驗證狀態
		if (!"pending".equals(order.getStatus())) {
			throw new IllegalStateException("訂單狀態不正確: " + order.getStatus());
		}

		// 更新訂單狀態
		order.setStatus("filled");
		order.setFilledAt(new Date());
		order.setExecutionPrice(executionPrice);
		orderRepository.save(order);

		// 處理資金和持倉
		if ("buy".equals(order.getOrderType())) {
			processBuyOrder(order, executionPrice);
		} else {
			processSellOrder(order, executionPrice);
		}

		logger.info("訂單 {} 已成功執行", order.getId());
	}

	// 處理買單
	private void processBuyOrder(Order order, BigDecimal executionPrice) {
		Position position = positionRepository.findByAccountIdAndSymbol(order.getAccountId(), order.getSymbol())
				.orElseGet(() -> {
					Position newPosition = new Position();
					newPosition.setAccountId(order.getAccountId());
					newPosition.setSymbol(order.getSymbol());
					newPosition.setQuantity(0);
					newPosition.setAverageCost(BigDecimal.ZERO);
					return newPosition;
				});

		// 計算新平均成本
		BigDecimal totalCost = position.getAverageCost().multiply(new BigDecimal(position.getQuantity()))
				.add(executionPrice.multiply(new BigDecimal(order.getQuantity())));

		int newQuantity = position.getQuantity() + order.getQuantity();
		position.setAverageCost(totalCost.divide(new BigDecimal(newQuantity), 2, RoundingMode.HALF_UP));
		position.setQuantity(newQuantity);
		positionRepository.save(position);
	}

	// 處理賣單
	private void processSellOrder(Order order, BigDecimal executionPrice) {
		Position position = positionRepository.findByAccountIdAndSymbol(order.getAccountId(), order.getSymbol())
				.orElseThrow(() -> new IllegalStateException("持倉不存在"));

		// 解凍並減少股票
		position.setQuantity(position.getQuantity() - order.getQuantity());
		positionRepository.save(position);

		// 增加資金
		Account account = accountRepository.findById(order.getAccountId())
				.orElseThrow(() -> new IllegalStateException("帳戶不存在"));

		BigDecimal proceeds = executionPrice.multiply(new BigDecimal(order.getQuantity()));
		account.setBalance(account.getBalance().add(proceeds));
		accountRepository.save(account);

		// 記錄損益
		recordRealizedProfit(order, position, executionPrice);
	}

	// 記錄已實現損益
	private void recordRealizedProfit(Order order, Position position, BigDecimal executionPrice) {
		BigDecimal costBasis = position.getAverageCost().multiply(new BigDecimal(order.getQuantity()));
		BigDecimal profit = executionPrice.multiply(new BigDecimal(order.getQuantity())).subtract(costBasis);

		RealizedProfit record = new RealizedProfit();
		record.setAccountId(order.getAccountId());
		record.setSymbol(order.getSymbol());
		record.setQuantity(order.getQuantity());
		record.setCostBasis(costBasis);
		record.setProceeds(executionPrice.multiply(new BigDecimal(order.getQuantity())));
		record.setProfit(profit);
		record.setTransactionDate(new Date());
		realizedProfitRepository.save(record);
	}

	// 判斷台股市場是否開盤
	private boolean isMarketOpenNow() {
		ZoneId taipeiZone = ZoneId.of("Asia/Taipei");
		ZonedDateTime now = ZonedDateTime.now(taipeiZone);

		// 週一至週五
		if (now.getDayOfWeek().getValue() > 5) {
			return false;
		}

		LocalTime nowTime = now.toLocalTime();
		return !nowTime.isBefore(LocalTime.of(9, 0)) && !nowTime.isAfter(LocalTime.of(13, 30));
	}
}
#  StockMatch - 股票模擬交易系統

這是一個以 Spring Boot + MySQL + Redis 建構的股票模擬交易系統，
使用者可以體驗真實市場的下單、查詢持倉、觀察列表與損益分析。

---

##  專案簡介

**StockMatch** 是一個提供使用者模擬投資體驗的網頁系統，  
整合即時台股行情與歷史資料，並結合帳戶管理、下單、觀察清單與損益分析功能。  
此專案以 **金融交易系統** 為概念，完整實作前後端互動與資料庫整合流程。

---

##  功能介紹

| 模組 | 功能說明 |
|------|----------|
|  帳戶概覽 | 顯示餘額、資產、持倉與損益統計 |
|  損益分析 | 區分已實現與未實現報酬率 |
|  模擬交易 | 即時下單、取消訂單、成交更新持倉 |
|  觀察清單 | 收藏並追蹤個股動態 |
|  股市行情 | 透過 FinMind / TWSE API 取得股票資訊 |
|  定時任務 | 自動更新即時股價與處理掛單 |

---

##  技術架構

```text
前端 (Frontend)
│
├── HTML / Tailwind CSS / JavaScript
│
後端 (Backend)
│
├── Spring Boot 3.5.3
│   ├── Controller 層：接收與回傳 API
│   ├── Service 層：業務邏輯、呼叫 Redis / Repository
│   └── Repository 層：JPA 存取 MySQL
│
資料儲存 (Database / Cache)
│
├── MySQL 8.0：主要交易資料
└── Redis：股價快取、提升查詢效能
```

---

##  使用技術

| 類別        | 技術                            |
| ---------- | ------------------------------ |
| Backend    | Java 17, Spring Boot 3.5.3     |
| Frontend   | HTML, Tailwind CSS, JavaScript |
| Database   | MySQL 8                        |
| Cache      | Redis                          |
| Build Tool | Maven                          |
| Deploy     | Tomcat 9 (WAR)                 |

---

##  系統畫面預覽

此區展示 **StockMatch 股票模擬系統** 的主要操作介面，  
包含帳戶管理、損益分析、模擬交易、觀察清單與股市行情等模組。

---

| 模組 | 畫面 1 | 畫面 2 |
|------|--------|--------|
|  **帳戶概覽**<br>顯示餘額、持倉、報酬率統計。<br>(提供重置帳戶功能） | <img width="420" alt="account_overview" src="https://github.com/user-attachments/assets/fd0db469-2481-4215-8d39-e1fec1212e70" />| <img width="420" alt="reset_account" src="https://github.com/user-attachments/assets/8deddac5-5bb5-40d5-9f73-8eb162d527b0" /> |
|  **損益分析**<br>區分已實現與未實現損益。 | <img width="420" alt="account_overview_realizedprofit" src="https://github.com/user-attachments/assets/350045ec-1c7b-4dc7-8ba4-73c744f31a93" /> | <img width="420" alt="account_overview_unrealizedprofit" src="https://github.com/user-attachments/assets/62c08b91-2a76-4c78-8014-146f27bd4ed5" /> |
|  **模擬交易**<br>進行下單、撤單、查詢成交狀態。 |<img width="420" alt="trade_service" src="https://github.com/user-attachments/assets/f7ab577a-4581-4c9e-984b-10a181053e0c" />| &nbsp; |
|  **觀察清單**<br>追蹤感興趣的股票即時漲跌。 | <img width="420" alt="watchlist" src="https://github.com/user-attachments/assets/b4eb705e-e57c-4b70-a7b5-82b759b87236" /> | &nbsp; |
|  **股市行情**<br>瀏覽即時與歷史股價走勢。 | <img width="420" alt="realtime_stock_prices" src="https://github.com/user-attachments/assets/37fd0184-b1a2-43c6-bd0e-0c7a0e8f7350" /> | <img width="420" alt="historical_stock_prices" src="https://github.com/user-attachments/assets/1e80c97b-cb7f-48c4-83fc-9050374db265" />
|

---

**作者**  
廖偲妤 Siyu Liao  
職訓局｜Java 工程師培訓  
前審計員，現轉職 Java 開發  

---

本專案僅供學習與展示用途，禁止未經授權之商業使用。


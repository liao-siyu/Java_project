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
|  歷史行情 | 透過 FinMind / TWSE API 取得股價走勢 |
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

**作者**  
廖偲妤 Siyu Liao  
職訓局｜Java 工程師培訓  
前審計員，現轉職 Java 開發  

---

本專案僅供學習與展示用途，禁止未經授權之商業使用。


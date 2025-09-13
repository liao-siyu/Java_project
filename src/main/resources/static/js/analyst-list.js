// js/analyst-list.js

// ✅ 成功提示
function showToast(message) {
  const toast = document.getElementById("toast");
  toast.textContent = message;
  toast.classList.remove("hidden");
  setTimeout(() => toast.classList.add("hidden"), 2000);
}

// ❌ 錯誤提示
function showError(message) {
  const error = document.getElementById("error-message");
  error.textContent = message;
  error.classList.remove("hidden");
  setTimeout(() => error.classList.add("hidden"), 2000);
}

// ✅ 卡片渲染函式
function renderAnalystCard(analyst) {
  const specialties = (analyst.specialties || []).join("、");
  return `
    <div class="bg-white rounded-2xl shadow p-6 hover:shadow-lg transition">
      <div class="text-center">
        <img src="${analyst.profileImgPath || '/images/default.jpg'}" alt="${analyst.name}" class="mx-auto w-36 h-36 rounded-full object-cover mb-4 border" />
        <h3 class="text-2xl font-bold text-blue-700">${analyst.name}</h3>
        <p class="text-lg text-gray-700 mb-2">${analyst.title || ''}</p>
        <p class="text-gray-700 text-sm line-clamp-2 mb-2">${analyst.bio || ''}</p>
        <p class="text text-gray-700 mb-4">${specialties || '(無)'}</p>
        <a href="analyst-plan-list.html?analystId=${analyst.id}" class="inline-block bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 text-sm">查看方案</a>
      </div>
    </div>
  `;
}

// ✅ 取得資料
async function loadAnalysts() {
  const container = document.getElementById("analystList");
  container.innerHTML = "載入中...";

  try {
    const res = await fetch("/api/analyst/list");
    if (!res.ok) throw new Error("無法取得資料");

    const data = await res.json();

    console.log("🔍 回傳分析師資料：", data); // debug 檢查 analystId

    if (!data.length) {
      container.innerHTML = "<p class='text-gray-600'>目前尚無可顯示的分析師。</p>";
      return;
    }

    const html = data.map(renderAnalystCard).join("");
    container.innerHTML = html;
  } catch (err) {
    console.error("❌ 分析師資料載入失敗：", err);
    container.innerHTML = "<p class='text-red-500'>❌ 載入失敗</p>";
    showError("❌ 無法載入分析師資料");
  }
}

// ✅ 自動載入
window.addEventListener("DOMContentLoaded", loadAnalysts);

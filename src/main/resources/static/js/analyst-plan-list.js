


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
  setTimeout(() => error.classList.add("hidden"), 3000);
}

// ✅ 從網址抓 analystId
function getAnalystIdFromURL() {
  const params = new URLSearchParams(window.location.search);
  return params.get("analystId");
}

// ✅ 渲染單一方案卡片
function renderPlanCard(plan) {
  return `
    <div class="border rounded-lg p-6 shadow bg-white">
      <h2 class="text-xl font-semibold text-blue-700 mb-2">${plan.name}</h2>
      <p class="text-gray-700 mb-2">${plan.description}</p>
      <p class="text-gray-900 font-bold text-lg mb-2">NT$ ${plan.price}</p>
      <p class="text-sm ${plan.status ? 'text-green-600' : 'text-gray-500'}">
        ${plan.status ? '✅ 已上架' : '⛔ 尚未啟用'}
      <button class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded">訂閱</button>
      </p>
    </div>
  `;
}

// ✅ 主流程
document.addEventListener("DOMContentLoaded", async () => {
  const container = document.getElementById("planContainer");
  const analystId = getAnalystIdFromURL();

console.log("🎯 URL 帶入的 analystId =", analystId); // 👈 才能安全印出來

  if (!analystId) {
    showError("❌ 無法取得分析師 ID");
    return;
  }

  try {
    console.log("📦 呼叫 API：/api/plans/analyst/" + analystId);
    const res = await fetch(`/api/plans/analyst/${analystId}`);
    if (!res.ok) {
  const text = await res.text();
  console.error("❌ API 回傳錯誤：", text);
  throw new Error("無法取得方案資料");
}

    const plans = await res.json();

    if (!plans.length) {
      container.innerHTML = `<p class="text-gray-600">此分析師尚未上架任何方案。</p>`;
      return;
    }

    const html = plans
      .filter(p => p.status) // ✅ 只顯示已上架方案
      .map(plan => renderPlanCard(plan))
      .join("");

    container.innerHTML = html || `<p class="text-gray-600">目前沒有可顯示的上架方案。</p>`;
  } catch (err) {
    console.error("❌ 載入失敗：", err);
    showError("❌ 無法載入方案，請稍後再試！");
  }
});

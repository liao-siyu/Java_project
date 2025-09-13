// analyst-dashboard.js

// ✅ 成功提示：Toast
function showToast(message) {
  const toast = document.getElementById("toast");
  toast.textContent = message;
  toast.classList.remove("hidden");
  setTimeout(() => toast.classList.add("hidden"), 2000);
}

// ❌ 錯誤提示：#error-message 區塊
function showError(message) {
  const error = document.getElementById("error-message");
  error.textContent = message;
  error.classList.remove("hidden");
  setTimeout(() => error.classList.add("hidden"), 2000);
}

// ✅ 全域變數：用於儲存 analystId
let analystId = null;

// === 載入方案列表 ===
async function loadMyPlans(analystId) {
  const container = document.getElementById("myPlans");
  container.innerHTML = "載入中...";

  try {
    const res = await fetch(`/api/plans/analyst/${analystId}?t=${Date.now()}`);
    const plans = await res.json();

    if (!plans.length) {
      container.innerHTML = "<p class='text-gray-600'>尚未建立任何方案。</p>";
      return;
    }

    const html = plans.reverse().map(plan => renderPlanCard(plan)).join("");

    container.innerHTML = html;
  } catch (err) {
    console.error("❌ 無法載入方案清單：", err);
    container.innerHTML = "❌ 無法取得方案資訊";
  }
}

// 取得分析師的方案列表，填入下拉選單
async function populatePlanOptions(analystId) {
  const select = document.getElementById("reportPlan");
  select.innerHTML = `<option value="">選擇要發布的方案</option>`; // reset

  try {
    const res = await fetch(`/api/plans/analyst/${analystId}`);
    const plans = await res.json();

    plans.forEach(plan => {
      const option = document.createElement("option");
      option.value = plan.id;
      option.textContent = plan.name;
      select.appendChild(option);
    });
  } catch (err) {
    console.error("❌ 無法載入方案：", err);
    showError("❌ 無法載入可發布方案，請稍後再試");
  }
}


// === 渲染方案卡片 ===
function renderPlanCard(plan) {
  return `
    <div class="border rounded-lg p-4 shadow bg-white" data-name="${plan.name}" data-description="${plan.description}" data-price="${plan.price}">
      <div class="flex justify-between items-center">
        <h4 class="text-2xl font-semibold text-blue-700">${plan.name}</h4>
        <label class="inline-flex items-center space-x-2">
          <span>${plan.status ? "已上架" : "未啟用"}</span>
          <input type="checkbox" data-id="${plan.id}" class="toggle-status" ${plan.status ? "checked" : ""}>
        </label>
      </div>
      <p class="text-gray-600 my-2">${plan.description}</p>
      <p class="text-gray-800 font-bold mb-2">NT$ ${plan.price}</p>
      <div class="flex space-x-2">
        <button class="edit-btn bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded" data-id="${plan.id}">修改</button>
      </div>
    </div>
  `;
}

// ✅ 與 loadMyPlans() 同層級，全域函式

async function loadReports(analystId) {
  const container = document.getElementById("reportList");
  container.innerHTML = "載入中...";

  try {
    const res = await fetch(`/api/reports/analyst/${analystId}`);
    const reports = await res.json();

    if (!reports.length) {
      container.innerHTML = "<p class='text-gray-600'>目前尚無任何報告</p>";
      return;
    }

    const html = reports.reverse().map(report => `
      <div class="border rounded p-4 bg-gray-50" data-id="${report.id}">
        <h4 class="text-2xl font-bold text-blue-700">${report.title}</h4>
        <p class="text-xs text-gray-600 mb-2">${report.reportDate}</p>
        <p class="text-lg text-gray-800 mb-2">${report.content}</p>
        <p class="text-xs text-gray-600 mb-2">${report.planName}</p>
        <button class="edit-report-btn bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded">修改</button>
      </div>
    `).join("");

    container.innerHTML = html;
  } catch (err) {
    console.error("❌ 無法載入報告：", err);
    container.innerHTML = "<p class='text-red-500'>❌ 無法載入報告</p>";
  }
}

// === 專長領域載入 ===
async function loadSpecialties() {
  try {
    // 1. 取得全部可選專長
    const res = await fetch("/api/specialties");
    const specialties = await res.json();

    // 2. 取得分析師已選的專長名稱
    const user = JSON.parse(localStorage.getItem("user"));
    const profileRes = await fetch(`/api/analyst/profile?userId=${user.userId}`);
    const profileData = await profileRes.json();
    const selectedNames = profileData.specialties || [];

    // 3. 動態產生 checkbox 並勾選
    const group = document.getElementById("specialtyCheckboxGroup");
    group.innerHTML = "";

    specialties.forEach(s => {
      const label = document.createElement("label");
      label.classList = "flex items-center space-x-2";

      const input = document.createElement("input");
      input.type = "checkbox";
      input.name = "specialty";
      input.value = s.id;

      if (selectedNames.includes(s.name)) {
        input.checked = true;
      }

      label.appendChild(input);
      label.appendChild(document.createTextNode(" " + s.name));
      group.appendChild(label);
    });

  } catch (err) {
    console.error("❌ 無法載入專長：", err);
    showError("❌ 載入專長資料失敗");
  }
}




// === 載入分析師個人資料 ===
async function loadAnalystProfile() {
  const user = JSON.parse(localStorage.getItem("user"));
  if (!user || user.userRole !== "analyst") {
    showError("請先登入分析師帳號");
    return;
  }

  await loadSpecialties();

  try {
    const res = await fetch("/api/analyst/profile?userId=" + user.userId);
    const data = await res.json();

    document.getElementById("title").value = data.title || "";
    document.getElementById("bio").value = data.bio || "";
    if (data.profileImgPath) document.getElementById("profilePreview").src = data.profileImgPath;
    if (data.certificateImgPath) document.getElementById("certificatePreview").src = data.certificateImgPath;

    // ✅ 勾選專長 checkbox
    const selectedNames = data.specialties || [];
    document.querySelectorAll("input[name='specialty']").forEach(input => {
      const label = input.parentElement.textContent.trim();
      if (selectedNames.includes(label)) {
        input.checked = true;
      }
    });

  } catch (err) {
    console.error("❌ 載入分析師資料失敗", err);
    showError("❌ 無法載入帳號資料");
  }
}
window.loadAnalystProfile = loadAnalystProfile;

// === 圖片預覽 ===
document.getElementById("profileImg").addEventListener("change", (e) => {
  const file = e.target.files[0];
  if (file) document.getElementById("profilePreview").src = URL.createObjectURL(file);
});

document.getElementById("certificateImg").addEventListener("change", (e) => {
  const file = e.target.files[0];
  if (file) document.getElementById("certificatePreview").src = URL.createObjectURL(file);
});

// === 帳號設定表單送出 ===
document.getElementById("profileForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const user = JSON.parse(localStorage.getItem("user"));

  const formData = new FormData();
  formData.append("userId", user.userId);
  formData.append("password", document.getElementById("newPassword").value);
  formData.append("title", document.getElementById("title").value);
  formData.append("bio", document.getElementById("bio").value);

  document.querySelectorAll("input[name='specialty']:checked").forEach(cb => {
    formData.append("specialtyIds", cb.value);
  });

  const profileImg = document.getElementById("profileImg").files[0];
  const certImg = document.getElementById("certificateImg").files[0];
  if (profileImg) formData.append("profileImg", profileImg);
  if (certImg) formData.append("certificateImg", certImg);

  try {
    const res = await fetch("/api/analyst/profile", {
      method: "PUT",
      body: formData
    });

    if (!res.ok) throw new Error("更新失敗");
    const msg = await res.text();
    showToast("✅ " + msg);
  } catch (err) {
    console.error("❌ 更新失敗：", err);
    showError("❌ 更新資料失敗");
  }
});

// 查詢分析師資料
async function loadAnalystInfo() {
  const user = JSON.parse(localStorage.getItem("user"));
  if (!user || user.userRole !== "analyst") {
    showError("請先登入分析師帳號");
    return;
  }

  try {
    const res = await fetch(`/api/analyst/profile?userId=${user.userId}`);
    const data = await res.json();

    const infoBox = document.getElementById("analystInfoBox");
    infoBox.innerHTML = `
      <div class="space-y-3 text-gray-700">
        <p><strong>職稱：</strong> ${data.title || "(未設定)"}</p>
        <p><strong>簡介：</strong> ${data.bio || "(未設定)"}</p>
        <p><strong>專長：</strong> ${(data.specialties || []).join("、") || "(無設定)"}</p>
        <div class="flex space-x-6 items-start">
          <div>
            <p class="font-semibold mb-1">個人頭像：</p>
            <img src="${data.profileImgPath}" class="w-24 h-24 object-cover rounded-full border" alt="頭像">
          </div>
          <div>
            <p class="font-semibold mb-1">證照圖片：</p>
            <img src="${data.certificateImgPath}" class="w-24 h-24 object-cover rounded-full border" alt="證照">
          </div>
        </div>
      </div>
    `;
  } catch (err) {
    console.error("❌ 載入分析師資料失敗", err);
    showError("❌ 無法載入分析師資料");
  }
}

// ✅ tab 切換到 info 就載入
if (document.querySelector("#analystInfoBox")) {
  loadAnalystInfo();
}

// ✅ 修改報告按鈕互動

document.addEventListener("click", (e) => {
  if (e.target.classList.contains("edit-report-btn")) {
    const reportCard = e.target.closest("[data-id]");
    const reportId = reportCard.dataset.id;
    const title = reportCard.querySelector("h4").textContent;
    const content = reportCard.querySelector("p.text-lg").textContent;

    const existingForm = reportCard.querySelector(".edit-report-form");
    if (existingForm) {
      existingForm.remove();
      return;
    }

    const formHtml = `
      <form class="edit-report-form mt-2 space-y-2 bg-gray-50 p-3 rounded">
        <input type="text" name="title" class="w-full border px-2 py-1 rounded" value="${title}" required>
        <textarea name="content" class="w-full border px-2 py-1 rounded" rows="3" required>${content}</textarea>
        <button type="submit" class="bg-green-500 text-white px-4 py-1 rounded hover:bg-green-600">儲存</button>
      </form>
    `;

    reportCard.insertAdjacentHTML("beforeend", formHtml);
  }
});

// ✅ 送出修改報告

document.addEventListener("submit", async (e) => {
  if (e.target.classList.contains("edit-report-form")) {
    e.preventDefault();
    const form = e.target;
    const reportId = form.closest("[data-id]").dataset.id;

    const payload = {
      title: form.title.value.trim(),
      content: form.content.value.trim()
    };

    try {
      const res = await fetch(`/api/reports/${reportId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });

      if (!res.ok) throw new Error("修改失敗");
      showToast("✅ 修改成功！");
      await loadReports(analystId);
    } catch (err) {
      console.error("❌ 修改報告失敗：", err);
      showError("❌ 修改失敗：" + err.message);
    }
  }
});



// === 主流程開始 ===
document.addEventListener("DOMContentLoaded", async () => {
  const user = JSON.parse(localStorage.getItem("user"));
  if (!user || user.userRole !== "analyst") {
    showError("❌ 請以分析師身份登入");
    window.location.href = "index.html";
    return;
  }

  try {
    const res = await fetch(`/api/analyst/user/${user.id}`);
    if (!res.ok) throw new Error("無法取得分析師資訊");
    const data = await res.json();
    // 登入成功取得 analystId
    analystId = data.analystId;
    document.getElementById("analystId").value = analystId;

    // ✅ 正確呼叫載入初始方案
    loadMyPlans(analystId);

    document.getElementById("reportForm").addEventListener("submit", async (e) => {
      e.preventDefault();

      const title = document.getElementById("reportTitle").value.trim();
      const content = document.getElementById("reportContent").value.trim();
      const planId = parseInt(document.getElementById("reportPlan").value);
      const analystId = parseInt(document.getElementById("analystId").value);

      // ✅ 欄位檢查
      if (!title || !content || !planId || isNaN(planId)) {
        showError("❌ 所有欄位皆為必填！");
        return;
      }

      const payload = {
        title,
        content,
        reportDate: new Date().toISOString().split("T")[0],
        analystId,
        planId
      };

      try {
        const res = await fetch("/api/reports", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload)
        });

        console.log("📡 Response status:", res.status);

        // ✅ 檢查是否成功
        if (res.status !== 201) {
          const msg = await res.text();
          throw new Error("新增失敗：" + msg);
        }

        showToast("✅ 發布成功！");
        e.target.reset();

        // ✅ 切換頁籤並載入報告

        const main = document.querySelector("main");
        if (main?.__x?.$data) {
          main.__x.$data.tab = "viewReports";
        } else {
          console.warn("Alpine tab 無法切換，main.__x 為 undefined");
        }


        await loadReports(analystId);

      } catch (err) {
        console.error("❌ 發布報告失敗：", err);
        showError("❌ 發布失敗：" + err.message);
      }
    });



    // 如果一進來就停在報告頁，也立刻載入
    const currentTab = document.querySelector("main")?.__x?.$data?.tab;
    if (currentTab === "viewReports") {
      loadReports(analystId);
    }

  } catch (err) {
    console.error(err);
    showError("❌ 登入錯誤，請重新登入");
    window.location.href = "index.html";
  }
});

// === toggle 切換狀態 ===
document.addEventListener("change", async (e) => {
  if (e.target.classList.contains("toggle-status")) {
    const planId = e.target.dataset.id;

    try {
      const res = await fetch(`/api/plans/${planId}/status`, {
        method: "PATCH",
      });

      if (!res.ok) throw new Error("狀態更新失敗");

      const label = e.target.closest("label").querySelector("span");
      const newStatus = e.target.checked;
      label.textContent = newStatus ? "已上架" : "未啟用";

      showToast(`✅ 已成功${newStatus ? "上架" : "停用"}方案`);
    } catch (err) {
      console.error("❌ 狀態切換錯誤：", err);
      showError("❌ 切換狀態失敗，請稍後再試！");
      e.target.checked = !e.target.checked;
    }
  }
});

// ✅ === 點修改 → 展開表單 ===
document.addEventListener("click", (e) => {
  if (e.target.classList.contains("edit-btn")) {
    const planCard = e.target.closest(".border");
    const planId = e.target.dataset.id;

    const existingForm = planCard.querySelector(".edit-form");

    if (existingForm) {
      // 如果已經有表單 → 移除（收起）
      existingForm.remove();
      return;
    }

    // 沒有表單 → 新增表單
    const name = planCard.dataset.name;
    const description = planCard.dataset.description;
    const price = planCard.dataset.price;

    const formHtml = `
      <form class="edit-form mt-4 space-y-2 bg-gray-50 p-3 rounded">
        <input type="text" name="name" class="w-full border px-2 py-1 rounded" value="${name}" required>
        <textarea name="description" class="w-full border px-2 py-1 rounded" rows="2" required>${description}</textarea>
        <input type="number" name="price" class="w-full border px-2 py-1 rounded" value="${price}" required>
        <button type="submit" class="bg-green-500 text-white px-3 py-1 rounded hover:bg-green-600">儲存</button>
      </form>
    `;
    planCard.insertAdjacentHTML("beforeend", formHtml);
  }
});

// === ✅ 表單送出 → 更新資料 ===
document.addEventListener("submit", async (e) => {
  if (e.target.classList.contains("edit-form")) {
    e.preventDefault();
    const form = e.target;
    const planId = form.closest(".border").querySelector(".edit-btn").dataset.id;

    const payload = {
      name: form.name.value,
      description: form.description.value,
      price: parseInt(form.price.value),
      analystId: parseInt(document.getElementById("analystId").value)
    };

    try {
      const res = await fetch(`/api/plans/${planId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });

      if (!res.ok) throw new Error("更新失敗");

      showToast("✅ 方案已更新！");
      form.remove(); // 收起表單
      await loadMyPlans(analystId); // ✅ 正確帶入全域變數
    } catch (err) {
      console.error("❌ 更新失敗：", err);
      showError("❌ 更新方案失敗");
    }
  }
});

// === ✅ 新增方案功能 ===
document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("createPlanForm");

  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const name = document.getElementById("planName").value.trim();
    const description = document.getElementById("planDescription").value.trim();
    const price = parseInt(document.getElementById("planPrice").value);
    const analystId = parseInt(document.getElementById("analystId").value);

    if (!name || !description || !price || !analystId) {
      showError("❌ 請完整填寫所有欄位");
      return;
    }

    const payload = { name, description, price, analystId };

    try {
      const res = await fetch("/api/plans", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });

      if (!res.ok) throw new Error("新增失敗");

      showToast("✅ 新增成功！");
      form.reset();

      // 切換頁籤為「我的方案」並重載
      const alpineRoot = document.querySelector('[x-data]');
      if (alpineRoot?.__x?.$data) {
        alpineRoot.__x.$data.tab = 'plans';
      }

      await loadMyPlans(analystId);
    } catch (err) {
      console.error("❌ 新增失敗：", err);
      showError("❌ 新增方案失敗");
    }
  });
});

if (document.querySelector("#profileForm")) {
  loadAnalystProfile();
}











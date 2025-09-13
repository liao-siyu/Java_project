
// ✅ Toast 提示用法
function showToast(message) {
  const toast = document.getElementById("toast");
  toast.textContent = message;
  toast.classList.remove("hidden", "opacity-0");

  // 2 秒後自動隱藏
  setTimeout(() => {
    toast.classList.add("opacity-0");
    setTimeout(() => toast.classList.add("hidden"), 500);
  }, 2000);
}

// ❌ 錯誤提示用法（紅字內嵌 alert-box）
function showError(message, formType) {
  const target = formType === "login"
    ? document.getElementById("loginError")
    : document.getElementById("registerError");
  target.textContent = message;
  target.classList.remove("hidden");
}

// 🔄 清除所有錯誤提示（切換表單時用）
function clearErrors() {
  document.getElementById("loginError").classList.add("hidden");
  document.getElementById("registerError").classList.add("hidden");
}

// ⛔ 阻擋登入者訪問 auth.html
const existingUser = JSON.parse(localStorage.getItem("user"));
if (existingUser) {
  const redirectTo = existingUser.userRole === "analyst" ? "analyst-dashboard.html" : "user-dashboard.html";
  window.location.href = redirectTo;
}



// ✅ auth.js 登入/註冊整合：Toast + AlertBox + Navbar + Role跳轉

// === DOMContentLoaded ===
document.addEventListener("DOMContentLoaded", () => {
  const loginTab = document.getElementById("loginTab");
  const registerTab = document.getElementById("registerTab");
  const loginForm = document.getElementById("loginForm");
  const registerForm = document.getElementById("registerForm");
  const forgotForm = document.getElementById("forgotPasswordForm");
  const forgotBtn = document.getElementById("forgotPasswordBtn");
  const backToLoginBtn = document.getElementById("backToLoginBtn");

  // 切換 tab
  loginTab.addEventListener("click", () => {
    loginTab.classList.add("border-blue-500");
    loginTab.classList.remove("border-transparent")
    registerTab.classList.remove("border-blue-500");
    registerTab.classList.add("border-transparent");
    loginForm.classList.remove("hidden");
    registerForm.classList.add("hidden");
    forgotForm.classList.add("hidden");
    clearErrors();
  });

  registerTab.addEventListener("click", () => {
    registerTab.classList.add("border-blue-500");
    registerTab.classList.remove("border-transparent");
    loginTab.classList.remove("border-blue-500");
    loginTab.classList.add("border-transparent");
    registerForm.classList.remove("hidden");
    loginForm.classList.add("hidden");
    forgotForm.classList.add("hidden");
    clearErrors();

    // 預設選擇「一般使用者」
    const userRadio = document.querySelector('input[name="role"][value="user"]');
    if (userRadio) {
      userRadio.checked = true;  // Set it as the default checked radio
      userRadio.dispatchEvent(new Event("change")); // 加上這行！
      analystForm.classList.add('hidden');  // Hide the analyst form
    }
  });

  forgotBtn.addEventListener("click", () => {
    loginForm.classList.add("hidden");
    forgotForm.classList.remove("hidden");
  });

  backToLoginBtn.addEventListener("click", () => {
    forgotForm.classList.add("hidden");
    loginForm.classList.remove("hidden");
  });

  // 角色切換表單
  const roleRadios = document.querySelectorAll('input[name="role"]');
  const analystForm = document.getElementById('analystForm');

  roleRadios.forEach((radio) => {
    radio.addEventListener('change', () => {
      if (radio.value === 'analyst' && radio.checked) {
        analystForm.classList.remove('hidden');
      } else if (radio.value === 'user' && radio.checked) {
        analystForm.classList.add('hidden');
      }
    });
  });

  // 🔄 動態載入 specialties checkbox
  async function loadSpecialties() {
    const container = document.getElementById("specialtyContainer");
    container.innerHTML = ""; // 清空預設項目

    try {
      const res = await fetch("/api/specialties");
      if (!res.ok) throw new Error("無法取得領域清單");

      const data = await res.json();
      data.forEach((spec) => {
        const label = document.createElement("label");
        label.className = "inline-flex items-center";
        label.innerHTML = `
          <input type="checkbox" name="specialties" value="${spec.id}" class="form-checkbox h-5 w-5 text-blue-600" />
          <span class="ml-2">${spec.name}</span>
        `;
        container.appendChild(label);
      });
    } catch (err) {
      console.error("❌ 領域載入失敗：", err);
    }
  }
  loadSpecialties(); // 初始載入

  // 綁定圖片預覽功能
  function setupImagePreview(inputId, previewId) {
  const input = document.getElementById(inputId);
  const preview = document.getElementById(previewId);

  input.addEventListener("change", () => {
    const file = input.files[0];
    if (file) {
      preview.src = URL.createObjectURL(file);
      preview.classList.remove("hidden");
    } else {
      preview.src = "";
      preview.classList.add("hidden");
    }
  });
}

setupImagePreview("profileInput", "profilePreview");
setupImagePreview("certInput", "certPreview");



  // ✅ 登入處理
  loginForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    clearErrors();

    const email = loginForm.querySelector("input[type='email']").value;
    const password = loginForm.querySelector("input[type='password']").value;

    try {
      const res = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      if (!res.ok) throw new Error(await res.text());

      const data = await res.json();

      // console.log("✅ 登入回傳：", data); // 加上 log 方便你除錯

      // 🔧 補齊通用欄位（for 前端一致性）
      data.role = data.userRole; // 統一 key 名
      data.id = data.userId;     // 統一 key 名 

      localStorage.setItem("user", JSON.stringify(data));

      // showToast("✅ 登入成功，歡迎回來！");
      updateNavbar();

      // ➤ 延遲導向角色頁面
      // setTimeout(() => {
        const role = data.userRole
        // || data.role; // 自動判斷欄位
        if (role === "user") {
          window.location.href = "user-dashboard.html";
        } else if (role === "analyst") {
          window.location.href = "analyst-dashboard.html";
        }
      // }, 1000);
    } catch (err) {
      showError(err.message || "帳號或密碼錯誤", "login");
    }
  });

  // ✅ 註冊處理 - 根據角色送出不同格式
registerForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  clearErrors();
  

  const name = registerForm.querySelector("input[type='text']").value;
  const email = registerForm.querySelector("input[type='email']").value;
  const password = registerForm.querySelector("input[type='password']").value;
  
 const roleInput = registerForm.querySelector("input[name='role']:checked");
  if (!roleInput) {
    showError("請選擇角色", "register");
    return;
  }
  const role = roleInput.value;
  console.log("👉 role:", role);


  try {
    let res;

    if (role === "user") {
      // 一般使用者：傳 JSON
      const payload = { name, email, password, role };

      console.log("📦 Sending JSON:", payload);

      res = await fetch("/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      console.log("🧪 檢查 Content-Type header：application/json");

    } else if (role === "analyst") {
      // 分析師：傳 multipart/form-data (含檔案)
      const formData = new FormData();

      formData.append("name", name);
      formData.append("email", email);
      formData.append("password", password);
      formData.append("role", role);

      // 額外欄位
      const title = document.getElementById("titleInput").value;
      const bio = document.getElementById("bioInput").value;
      const profileFile = document.getElementById("profileInput").files[0];
      const certFile = document.getElementById("certInput").files[0];

      formData.append("title", title);
      formData.append("bio", bio);
      if (profileFile) formData.append("profileImg", profileFile);
      if (certFile) formData.append("certificateImg", certFile);

      // specialties 多選
      const selectedSpecs = Array.from(document.querySelectorAll('input[name="specialties"]:checked'))
        .map(cb => cb.value);
      selectedSpecs.forEach(id => formData.append("specialties", id));

      console.log("📦 Sending FormData for analyst");

      res = await fetch("/api/auth/register/analyst", {
        method: "POST",
        body: formData, // 不用設 headers，瀏覽器會自動帶 multipart/form-data
      });
    } else {
      throw new Error("未知的角色類型");
    }

    if (!res.ok) {
      const errorText = await res.text();
      throw new Error(errorText);
    }

    showToast("✅ 註冊成功，請重新登入！");
    if (loginTab) loginTab.click();

  } catch (err) {
    console.error("❌ 註冊失敗:", err);
    showError(err.message, "register");
  }
});


  // ✅ navbar 更新（登入後改為會員中心）
  function updateNavbar() {
    const user = JSON.parse(localStorage.getItem("user"));
    if (user) {
      const nav = document.querySelector("nav");
      if (nav) {
        const memberLink = document.createElement("a");
        memberLink.href = user.userRole === "analyst" ? "analyst-dashboard.html" : "user-dashboard.html";
        memberLink.className = "text-blue-700 font-semibold";
        memberLink.textContent = "會員中心";

        // 移除原本的登入/註冊文字或 span
        nav.querySelectorAll("span, a").forEach(el => {
          if (el.textContent.includes("登入") || el.textContent.includes("註冊")) {
            el.remove();
          }
        });

        nav.appendChild(memberLink);
      }
    }
  }


});
// === DOMContentLoaded ===

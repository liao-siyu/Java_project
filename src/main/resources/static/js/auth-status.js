// js/auth-status.js
document.addEventListener("DOMContentLoaded", () => {
  const user = JSON.parse(localStorage.getItem("user"));
  if (user) {
    const links = document.querySelectorAll("nav a, nav span, .auth-link, .block");
    links.forEach(link => {
      if (link.textContent.includes("登入") || link.textContent.includes("註冊")) {
        const label = user.userRole === "analyst" ? "分析師中心" : "會員中心";
        link.textContent = label;
        link.href = user.userRole === "analyst" ? "analyst-dashboard.html" : "user-dashboard.html";
      }
    });
  }
});

// // 👋 導覽列顯示使用者名稱 & 登出按鈕
// document.addEventListener("DOMContentLoaded", () => {
//   const user = JSON.parse(localStorage.getItem("user"));
//   if (user) {
//     const nav = document.querySelector("nav");
//     if (!nav) return;

//     // 建立歡迎文字
//     const welcomeSpan = document.createElement("span");
//     welcomeSpan.className = "text-gray-700 ml-4";
//     welcomeSpan.textContent = `Hi, ${user.name}`;

//     // 建立登出按鈕
//     const logoutBtn = document.createElement("button");
//     logoutBtn.className = "text-red-500 hover:underline ml-2";
//     logoutBtn.textContent = "登出";
//     logoutBtn.onclick = () => {
//       localStorage.removeItem("user");
//       window.location.href = "index.html";
//     };

//     // 加進去 navbar
//     nav.appendChild(welcomeSpan);
//     nav.appendChild(logoutBtn);
//   }
// });


document.addEventListener("DOMContentLoaded", async () => {
  const form = document.getElementById("planForm");
  const result = document.getElementById("result");
  const analystInput = document.getElementById("analystId");

  // ✅ 從 localStorage 取出使用者資訊
  const user = JSON.parse(localStorage.getItem("user"));
  if (!user || user.userRole !== "analyst") {
    alert("只有分析師可以上架方案！");
    window.location.href = "index.html";
    return;
  }

  // ✅ 從後端查詢對應的 analystId
  try {
    const res = await fetch(`http://localhost:8080/api/analysts/user/${user.id}`);
    if (!res.ok) throw new Error("查詢分析師 ID 失敗");

    const data = await res.json();
    analystInput.value = data.analystId;
    analystInput.type = "hidden"; // ✅ 設為隱藏欄位
  } catch (err) {
    alert("❌ 無法取得分析師資訊，請重新登入");
    console.error(err);
    return;
  }

  // ✅ 提交表單事件處理
  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const payload = {
      analystId: parseInt(analystInput.value),
      name: document.getElementById("name").value,
      description: document.getElementById("description").value,
      price: parseInt(document.getElementById("price").value)
    };

    try {
      const res = await fetch("http://localhost:8080/api/plans", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
      });

      if (!res.ok) throw new Error("上架失敗");

      const data = await res.json();
      console.log("成功回傳：", data);

      result.classList.remove("hidden");
      form.reset();
    } catch (err) {
      alert("❌ 發生錯誤，請確認輸入是否正確");
      console.error(err);
    }
  });
});

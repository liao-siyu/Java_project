document.addEventListener("DOMContentLoaded", loadRecommendedAnalysts);

async function loadRecommendedAnalysts() {
  try {
    const res = await fetch("/api/analyst/recommend");
    const data = await res.json();
    console.log(data); 

    const container = document.querySelector("#analysts .grid");
    container.innerHTML = "";

    data.forEach(a => {
      const card = `
        <a href="analyst-plan-list.html?analystId=${a.analystId}" class="block bg-white p-4 rounded-2xl shadow text-center hover:shadow-lg transition">
          <img src="${a.profileImg || '/images/default-profile.jpg'}" alt="分析師" class="mx-auto rounded-full w-32 h-32 mb-4">
          <h3 class="font-semibold text-lg">${a.name}</h3>
          <p class="text-sm text-gray-600">${a.title || ""}</p>
          <p class="text-sm text-gray-600">專長：${a.specialties.join("、")}</p>
        </a>`;
      container.insertAdjacentHTML("beforeend", card);
    });

  } catch (err) {
    console.error("❌ 載入推薦分析師失敗", err);
    const container = document.querySelector("#analysts .grid");
    container.innerHTML = "<p class='text-red-500'>❌ 無法載入分析師資料</p>";
  }
}

document.addEventListener("DOMContentLoaded", function() {
    const params = new URLSearchParams(window.location.search);

    // 读取新参数
    const keyword = params.get("keyword") || "";
    const areaKeyword = params.get("areaKeyword") || ""; // 城市关键字
    const checkInDate = params.get("checkInDate") || "";
    const searchType = params.get("searchType") || "";

    const titleEl = document.getElementById("pageTitle");
    const inputEl = document.getElementById("keywordInput");

    // 设置标题逻辑
    if (areaKeyword) {
        titleEl.textContent = `Properties in "${areaKeyword}"`;
        if (checkInDate) {
            titleEl.textContent += ` (Available from ${checkInDate})`;
        }
    } else if (keyword) {
        inputEl.value = keyword;
        titleEl.textContent = `Results for "${keyword}"`;
    }

    // 调用搜索
    fetchRooms({ keyword, areaKeyword, checkInDate, searchType });

    // 绑定本页面的搜索框 (仅做 Keyword 搜索)
    document.getElementById("searchPageBtn").addEventListener("click", () => {
        const newKw = inputEl.value.trim();
        window.location.href = `search.jsp?keyword=${encodeURIComponent(newKw)}`;
    });

    // Logout 逻辑
    const logoutLink = document.getElementById("logoutLink");
    if(logoutLink) {
        logoutLink.addEventListener("click", async (e) => {
            e.preventDefault();
            await fetch("logout", { method: "POST" });
            window.location.reload();
        });
    }
});

async function fetchRooms(params) {
    const container = document.getElementById("resultsContainer");
    try {
        // 构建 API URL
        const url = new URL("api/rooms", window.location.href);
        // 将所有参数附加到 URL
        const apiParams = new URLSearchParams();
        if(params.keyword) apiParams.append("keyword", params.keyword);
        if(params.areaKeyword) apiParams.append("areaKeyword", params.areaKeyword);
        if(params.checkInDate) apiParams.append("checkInDate", params.checkInDate);
        if(params.searchType) apiParams.append("searchType", params.searchType);

        // Fetch
        const response = await fetch("api/rooms?" + apiParams.toString());
        const rooms = await response.json();

        container.innerHTML = "";
        if (rooms.length === 0) {
            container.innerHTML = "<p>No properties found matching your criteria.</p>";
            return;
        }

        rooms.forEach(room => {
            const card = document.createElement("a");
            card.className = "property-card";
            card.href = `property-detail.jsp?id=${room.id}`;
            const img = room.coverImage || "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=500&q=60";

            card.innerHTML = `
                    <div class="card-image"><img src="${img}"></div>
                    <div class="card-info">
                        <div class="card-title">${room.title}</div>
                        <div class="card-meta">${room.areaName || ''} | ${room.roomTypeName || ''}</div>
                        <div class="card-price">¥${room.price}/Mo</div>
                    </div>
                `;
            container.appendChild(card);
        });

    } catch (e) {
        console.error(e);
        container.innerHTML = "Error loading data.";
    }
}
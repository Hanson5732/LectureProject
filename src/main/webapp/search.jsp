<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Search Properties - Estate Project</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="static/css/style.css">
</head>
<body>

<header class="top-bar">
    <nav class="nav-container">
        <div class="nav-left">
            <a href="index.jsp" class="logo">EstateProject</a>
            <a href="search.jsp" class="nav-item">All Properties</a>
        </div>
        <div class="nav-right">
            <% String username = (String) session.getAttribute("username"); %>
            <div class="user-info">
                <% if (username != null) { %>
                <span>Hello, <strong><%= username %></strong></span>
                <a id="logoutLink" href="#" class="nav-item">Logout</a>
                <% } else { %>
                <a href="login.jsp" class="nav-item">Login</a>
                <% } %>
            </div>
        </div>
    </nav>
</header>

<main class="section-container">

    <div style="margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center;">
        <h2 id="pageTitle" class="section-title">Property List</h2>
        <div>
            <input type="text" id="keywordInput" placeholder="Keyword..." style="padding: 8px; border: 1px solid #ddd; border-radius: 4px;">
            <button id="searchPageBtn" class="btn-secondary" style="padding: 8px 15px; border-radius: 4px; border:none; cursor:pointer;">Search</button>
        </div>
    </div>

    <div id="resultsContainer" class="property-grid">
        Loading...
    </div>

</main>

<script src="static/js/apiUtil.js"></script>
<script>
    // 简单的内联脚本处理搜索页逻辑
    document.addEventListener("DOMContentLoaded", function() {

        // 解析 URL 参数
        const params = new URLSearchParams(window.location.search);
        const keyword = params.get("keyword") || "";
        const areaId = params.get("areaId") || "";

        const titleEl = document.getElementById("pageTitle");
        const inputEl = document.getElementById("keywordInput");

        // 回填搜索框
        if (keyword) {
            inputEl.value = keyword;
            titleEl.textContent = `Search results for "${keyword}"`;
        } else if (areaId) {
            titleEl.textContent = "Properties in Selected Area";
        }

        // 加载数据
        fetchRooms(keyword, areaId);

        // 绑定搜索按钮
        document.getElementById("searchPageBtn").addEventListener("click", () => {
            const newKw = inputEl.value.trim();
            window.location.href = `search.jsp?keyword=${encodeURIComponent(newKw)}`;
        });

        // 绑定登出
        const logoutLink = document.getElementById("logoutLink");
        if(logoutLink) {
            logoutLink.addEventListener("click", async (e) => {
                e.preventDefault();
                await fetch("logout", { method: "POST" });
                window.location.reload();
            });
        }
    });

    async function fetchRooms(keyword, areaId) {
        const container = document.getElementById("resultsContainer");
        try {
            // 构建 URL
            let url = "api/rooms?";
            if (keyword) url += `keyword=${encodeURIComponent(keyword)}&`;
            if (areaId) url += `areaId=${areaId}`;

            const res = await fetch(url);
            const rooms = await res.json();

            container.innerHTML = "";
            if (rooms.length === 0) {
                container.innerHTML = "<p>No properties found matching your criteria.</p>";
                return;
            }

            // 复用 index.js 里定义的 createRoomCard 逻辑?
            // 为了简单，这里重写一遍卡片生成逻辑，或者你可以把 createRoomCard 提取到公共 JS
            rooms.forEach(room => {
                const card = document.createElement("a");
                card.className = "property-card";
                card.href = `property-detail.jsp?id=${room.id}`; // 稍后实现详情页

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
</script>

</body>
</html>
document.addEventListener("DOMContentLoaded", function() {

    // --- 1. 登出逻辑 ---
    const logoutLink = document.getElementById("logoutLink");
    if (logoutLink) {
        logoutLink.addEventListener("click", function(e) {
            e.preventDefault();
            logout();
        });
    }

    // --- 2. 加载地区列表 ---
    loadAreas();

    // --- 3. 加载推荐房源 ---
    loadRecommendRooms();

    // --- 4. 绑定搜索功能 ---
    const searchBtn = document.getElementById("searchBtn");
    const searchInput = document.getElementById("searchInput");

    if (searchBtn) {
        searchBtn.addEventListener("click", function() {
            handleSearch();
        });
    }

    // 允许回车搜索
    if (searchInput) {
        searchInput.addEventListener("keypress", function(e) {
            if (e.key === "Enter") {
                handleSearch();
            }
        });
    }

    function handleSearch() {
        const keyword = searchInput.value.trim();
        // 跳转到搜索页 (search.jsp)，带上 keyword 参数
        window.location.href = `search.jsp?keyword=${encodeURIComponent(keyword)}`;
    }
});

/**
 * 加载地区数据并渲染
 */
async function loadAreas() {
    const container = document.getElementById("areaLinksContainer");
    if (!container) return;

    try {
        // 调用后端 API
        const response = await fetch("api/areas");
        if (!response.ok) throw new Error("Failed to load areas");

        const areas = await response.json();

        // 清空容器
        container.innerHTML = "";

        if (areas.length === 0) {
            container.innerHTML = "<span>No areas found.</span>";
            return;
        }

        // 渲染链接
        areas.forEach(area => {
            const link = document.createElement("a");
            link.href = `search.jsp?areaId=${area.id}`;
            link.textContent = area.name;
            link.style.marginRight = "10px";
            container.appendChild(link);
        });

    } catch (error) {
        console.error("Area load error:", error);
        container.innerHTML = "<span style='color:red'>Error loading areas</span>";
    }
}

/**
 * 加载推荐房源
 */
async function loadRecommendRooms() {
    const container = document.getElementById("recommendContainer");
    if (!container) return;

    try {
        // 调用后端 API
        const response = await fetch("api/rooms");
        if (!response.ok) throw new Error("Failed to load rooms");

        const rooms = await response.json();

        container.innerHTML = "";

        if (rooms.length === 0) {
            container.innerHTML = "<p>No properties available at the moment.</p>";
            return;
        }

        // 仅显示前 4 个作为推荐
        const recommendRooms = rooms.slice(0, 4);

        recommendRooms.forEach(room => {
            const card = createRoomCard(room);
            container.appendChild(card);
        });

    } catch (error) {
        console.error("Room load error:", error);
        container.innerHTML = "<p style='color:red'>Error loading recommendations.</p>";
    }
}

/**
 * 创建房源卡片的 HTML 元素
 */
function createRoomCard(room) {
    // 默认图片处理
    const imageUrl = room.coverImage || "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=500&q=60";

    const a = document.createElement("a");
    a.className = "property-card";
    a.href = `property-detail.jsp?id=${room.id}`; // 跳转详情页

    a.innerHTML = `
        <div class="card-image">
            <img src="${imageUrl}" alt="${room.title}">
        </div>
        <div class="card-info">
            <div class="card-title" title="${room.title}">${room.title}</div>
            <div class="card-meta">
                ${room.areaName || 'Unknown Area'} | ${room.buildingName || ''} | ${room.roomTypeName || ''}
            </div>
            <div class="card-meta">
                ${room.size ? room.size + ' m²' : ''}
            </div>
            <div class="card-price">¥${room.price}/Month</div>
        </div>
    `;
    return a;
}

async function logout() {
    await fetch("logout", { method: "POST" });
    window.location.href = "login.jsp";
}

function setSearchType(type, btn) {
    // 设置隐藏域
    document.getElementById('searchType').value = type;

    // 切换样式
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
}
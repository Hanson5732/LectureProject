<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Property Detail - Estate Project</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="static/css/style.css">
    <style>
        /* 保持你原有的样式 */
        .details-container { width: 1200px; margin: 20px auto; display: flex; justify-content: space-between; }
        .property-main { width: 750px; }
        .breadcrumb { font-size: 14px; color: #999; margin-bottom: 20px; }
        .breadcrumb a { color: #333; text-decoration: none; }
        .property-main h1 { font-size: 28px; font-weight: 500; color: #333; margin: 0; }
        .stats-bar { display: flex; align-items: flex-end; background: #f7f7f7; padding: 15px 20px; margin-top: 20px; border-radius: 4px; }
        .stats-bar .price { font-size: 30px; font-weight: bold; color: #e84a5f; }
        .stats-bar .stat { font-size: 20px; color: #333; margin-left: 40px; }
        .image-carousel { margin-top: 25px; width: 100%; height: 480px; background: #eee; }
        .image-carousel img { width: 100%; height: 100%; object-fit: cover; }
        .description-section { margin-top: 30px; line-height: 1.6; color: #444; }

        /* 预订按钮样式 */
        .sidebar { width: 400px; padding: 20px; background: #fff; border: 1px solid #eee; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
        .book-btn { width: 100%; padding: 15px; background-color: #e84a5f; color: white; border: none; font-size: 18px; font-weight: bold; cursor: pointer; border-radius: 4px; margin-top: 20px; }
        .book-btn:hover { background-color: #d73c50; }
    </style>
</head>
<body>

<header class="top-bar" style="position: relative; background-color: #426354;">
    <nav class="nav-container">
        <div class="nav-left">
            <a href="index.jsp" class="logo">
                <i class="fa-solid fa-building"></i>
                <span class="logo-text">Rent</span>
                <span class="logo-sub">LIVING</span>
            </a>
        </div>
        <div class="nav-right">
            <% String username = (String) session.getAttribute("username"); %>
            <div class="user-info">
                <% if (username != null) { %>
                <span>Hi, <strong><%= username %></strong></span>
                <a href="#" onclick="logout()" class="nav-item">Logout</a>
                <% } else { %>
                <a href="login.jsp" class="nav-item">Login</a>
                <% } %>
            </div>
        </div>
    </nav>
</header>

<main class="details-container">
    <div class="property-main">
        <nav class="breadcrumb">
            <a href="index.jsp">Home</a> &gt;
            <span id="crumbArea">Area</span> &gt;
            <span id="crumbTitle">Loading...</span>
        </nav>

        <h1 id="propTitle">Loading property details...</h1>

        <div class="stats-bar">
            <div class="price" id="propPrice">--<span>/Month</span></div>
            <div class="stat" id="propType">--</div>
            <div class="stat" id="propSize">--<span>m²</span></div>
        </div>

        <div class="image-carousel">
            <img id="propImage" src="" alt="Property Image">
        </div>

        <div class="description-section">
            <h3>Description</h3>
            <p id="propDesc">Loading description...</p>
        </div>
    </div>

    <aside class="sidebar">
        <h3>Interested in this property?</h3>
        <p>Book a viewing or rent it now.</p>
        <button class="book-btn" id="bookBtn">Book Now</button>
    </aside>
</main>

<script src="static/js/apiUtil.js"></script>
<script>
    document.addEventListener("DOMContentLoaded", function() {
        const params = new URLSearchParams(window.location.search);
        const roomId = params.get("id");

        if (!roomId) {
            alert("No property selected.");
            window.location.href = "index.jsp";
            return;
        }

        loadRoomDetail(roomId);

        // 绑定预订按钮事件
        document.getElementById("bookBtn").addEventListener("click", function() {
            // 这里我们先简单判断是否登录
            <% if (session.getAttribute("username") == null) { %>
            alert("Please login to book this property.");
            window.location.href = "login.jsp";
            <% } else { %>
            // 下一步我们将实现这个功能
            alert("Booking function coming soon!");
            <% } %>
        });
    });

    async function loadRoomDetail(id) {
        try {
            const res = await fetch("api/room-detail?id=" + id);
            if (!res.ok) throw new Error("Failed to load data");

            const room = await res.json();

            // 填充数据
            document.getElementById("crumbArea").textContent = room.areaName || "Area";
            document.getElementById("crumbTitle").textContent = room.title;
            document.getElementById("propTitle").textContent = room.title;
            document.getElementById("propPrice").innerHTML = `¥${room.price}<span>/Month</span>`;
            document.getElementById("propType").textContent = room.roomTypeName || "Apartment";
            document.getElementById("propSize").innerHTML = `${room.size}<span>m²</span>`;
            document.getElementById("propDesc").textContent = room.description || "No description provided.";

            if (room.coverImage) {
                document.getElementById("propImage").src = room.coverImage;
            } else {
                document.getElementById("propImage").src = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=800&q=80";
            }

        } catch (e) {
            console.error(e);
            document.getElementById("propTitle").textContent = "Property not found or error loading data.";
        }
    }

    async function logout() {
        await fetch("logout", { method: "POST" });
        window.location.href = "login.jsp";
    }
</script>

</body>
</html>
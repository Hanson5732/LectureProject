<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Property Detail - Estate Project</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="static/css/style.css">

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
    <link rel="stylesheet" href="https://npmcdn.com/flatpickr/dist/themes/material_green.css">
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
                <div class="nav-divider"></div>
                <a href="register.jsp" class="nav-item">Sign up</a>
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

        <div style="margin-top: 15px;">
            <label style="display:block; font-size:12px; margin-bottom:5px;">Check-in Date:</label>
            <input type="text" id="startDate" placeholder="Select Date" style="width:100%; padding:8px; margin-bottom:10px; border:1px solid #ccc; border-radius:4px; background-color: white;">

            <label style="display:block; font-size:12px; margin-bottom:5px;">Duration:</label>
            <select id="duration" style="width:100%; padding:8px; margin-bottom:10px; border:1px solid #ccc; border-radius:4px;">
                <option value="1">1 Month</option>
                <option value="2">2 Months</option>
                <option value="3">3 Months</option>
                <option value="6">6 Months</option>
                <option value="12">1 Year</option>
            </select>

            <label style="display:block; font-size:12px; margin-bottom:5px;">Guests (Max 4):</label>
            <input type="number" id="guests" min="1" max="4" value="1" style="width:100%; padding:8px; margin-bottom:10px; border:1px solid #ccc; border-radius:4px;">
        </div>

        <button class="book-btn" id="bookBtn">Book Now</button>
    </aside>
</main>

<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
<script src="static/js/apiUtil.js"></script>
<script src="static/js/room-detail.js"></script>

</body>
</html>
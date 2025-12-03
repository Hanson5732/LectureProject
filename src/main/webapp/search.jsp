<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Search Properties - Estate Project</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="static/css/style.css">
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

<main class="section-container">

    <div class="breadcrumb" style="margin-bottom: 20px; font-size: 14px; color: #777;">
        <a href="index.jsp">Home</a> &gt; <span>Search Result</span>
    </div>

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
<script src="static/js/search.js"></script>

</body>
</html>
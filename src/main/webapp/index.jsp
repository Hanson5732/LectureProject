<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <title>Real Estate - Homepage</title>
  <!-- Import Font Awesome Icons -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
  <!-- Import Homepage Styles -->
  <link rel="stylesheet" href="static/css/style.css">
</head>
<body>

<!-- Top Navigation Bar (based on image_89f1bf.png) -->
<header class="top-bar">
  <nav class="nav-container">
    <div class="nav-left">
      <!-- 1. Logo (Brand removed) -->
      <a href="index.jsp" class="logo">EstateProject</a>

      <!-- 2. City Selector -->
      <a href="#" class="city-selector">
        @ Beijing <i class="fa-solid fa-caret-down"></i>
      </a>

      <!-- 3. Navigation Links -->
      <a href="#" class="nav-item">New Homes</a>
      <a href="#" class="nav-item">Second-Hand</a>
      <a href="#" class="nav-item">Rentals</a>
      <a href="#" class="nav-item">Commercial</a>
    </div>

    <!-- 4. User Info Area (Login/Sign Up) -->
    <div class="nav-right">
      <%
        // 检查 session 中是否存在 "fullName" 属性
        String username = (String) session.getAttribute("username");
      %>
      <div id="userInfo" class="user-info">
        <% if (username != null) { %>

          <span>Hello, <strong><%= username %></strong></span>
          <a id="logoutLink" href="#" class="nav-item">Logout</a>

        <% } else { %>

          <a href="login.jsp" class="nav-item">Login</a>
          <a href="register.jsp" class="nav-item">Sign Up</a>

        <% } %>
      </div>
    </div>
  </nav>
</header>

<!-- Main Hero Section (with background image) -->
<main class="hero-section">

  <!-- Search Block -->
  <div class="search-block">

    <!-- 1. Search Type Tabs (based on image_89f1bf.png) -->
    <div class="search-tabs">
      <!-- (Ad slogan removed) -->
      <button class="tab-btn active" data-search-type="buy">Buy</button>
      <button class="tab-btn" data-search-type="sell">Sell</button>
      <button class="tab-btn" data-search-type="rent">Rent</button>
      <button class="tab-btn" data-search-type="commercial">Commercial</button>
      <button class="tab-btn" data-search-type="factory">Factory</button>
    </div>

    <!-- 2. White Content Area -->
    <div class="search-content">

      <!-- 2a. Left Search Area -->
      <div class="search-main">
        <!-- Search Bar -->
        <div class="search-bar">
          <input type="text" placeholder="Enter community, address...">
          <div class="search-buttons">
            <button class="btn-secondary">Second-Hand</button>
            <button class="btn-primary">New Homes</button>
          </div>
        </div>

        <!-- Quick Links (based on image_89f1bf.png) -->
        <div class="quick-links-container">
          <!-- Second-Hand Quick Links -->
          <div class="quick-links-col">
            <h4>Second-Hand</h4>
            <div class="links-row">
              <a href="#">Area 1</a> <a href="#">Area 2</a> <a href="#">Area 3</a>
              <a href="#">Area 4</a> <a href="#">Area 5</a> <a href="#">Area 6</a>
              <a href="#">Area 7</a> <a href="#">Area 8</a> <a href="#">Area 9</a>
            </div>
            <div class="links-row">
              <a href="#">Area 10</a> <a href="#">Area 11</a> <a href="#">Area 12</a>
              <a href="#">Area 13</a> <a href="#">Area 14</a> <a href="#">Area 15</a>
              <a href="#">Area 16</a> <a href="#">Area 17</a>
            </div>
            <div class="links-row">
              <a href="#">Below 1.5M</a> <a href="#">1.5M-2.5M</a> <a href="#">2.5M-3.0M</a> <a href="#">3.0M-3.5M</a>
            </div>
          </div>

          <!-- New Homes Quick Links -->
          <div class="quick-links-col">
            <h4>New Homes</h4>
            <div class="links-row">
              <a href="#">Below 10k</a> <a href="#">10k-20k</a> <a href="#">20k-30k</a>
              <a href="#">30k-40k</a> <a href="#">40k-60k</a> <a href="#">60k-80k</a>
              <a href="#">80k-100k</a>
            </div>
            <div class="links-row">
              <a href="#">100k-130k</a> <a href="#">130k-150k</a> <a href="#">Above 150k</a>
            </div>
          </div>
        </div>
      </div>

      <!-- 2b. QR Code Area (Removed as requested) -->

    </div>
  </div>

</main>

<!--
  [Important] Load JS utilities and logic
  1. apiUtil.js must be loaded before index.js
  2. index.js handles the login state check
-->
<script src="static/js/index.js"></script>

</body>
</html>


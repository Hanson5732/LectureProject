<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <title>Weave Living Style - Estate Project</title>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" />
  <link rel="stylesheet" href="static/css/style.css" />
</head>
<body>

<header class="top-bar">
  <nav class="nav-container">

    <div class="nav-left">
      <a href="index.jsp" class="logo">
        <i class="fa-solid fa-building"></i>
        <span class="logo-text">Rent</span>
        <span class="logo-sub">LIVING</span>
      </a>

      <div class="nav-links">
        <a href="#" class="nav-item">Cities <i class="fa-solid fa-angle-down"></i></a>
        <a href="#" class="nav-item">Living Options <i class="fa-solid fa-angle-down"></i></a>
      </div>
    </div>

    <div class="nav-right">
      <%
        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");
      %>
      <div id="userInfo" class="user-info">

        <a href="#" class="btn-white-pill">Schedule Viewing</a>

        <% if (username != null) { %>
        <span class="nav-item">Hi, <%= username %></span>
        <% if ("ADMIN".equals(role)) { %>
        <a href="admin/index.jsp" class="nav-item">Dashboard</a>
        <% } else { %>
        <a href="my-orders.jsp" class="nav-item">My Orders</a>
        <% } %>
        <a id="logoutLink" href="#" class="nav-item">Logout</a>
        <% } else { %>
        <a href="login.jsp" class="nav-item">Login</a>
        <div class="nav-divider"></div>
        <a href="register.jsp" class="nav-item">Sign up</a>
        <% } %>

      </div>
    </div>
  </nav>
</header>

<main class="hero-section">
  <div class="search-container">
    <div class="search-tabs">
      <button type="button" class="tab-btn active" onclick="setSearchType('long', this)">Long Stay (1+ Month)</button>
      <button type="button" class="tab-btn" onclick="setSearchType('short', this)">Short Stay (Under 1 Month)</button>
    </div>

    <form action="search.jsp" method="get" class="search-bar-rounded">

      <div class="search-input-group">
        <label>Where ?</label>
        <input type="text" name="areaKeyword" placeholder="City or Area..." />
      </div>

      <div class="search-input-group">
        <label>When ?</label>
        <input type="text" name="checkInDate" placeholder="Check-in Date" onfocus="(this.type='date')" />
      </div>

      <div class="search-input-group">
        <label>Who ?</label>
        <select name="guests">
          <option value="1">1 guest</option>
          <option value="2">2 guests</option>
          <option value="3">3 guests</option>
          <option value="4">4 guests</option>
        </select>
      </div>

      <button type="submit" class="btn-search">
        Find your home <i class="fa-solid fa-magnifying-glass" style="margin-left:8px;"></i>
      </button>
    </form>

  </div>
</main>

<section class="section-container">
  <h2 class="section-title">Recommended for You</h2>
  <div id="recommendContainer" class="property-grid">
    Loading recommendations...
  </div>
</section>

<script src="static/js/apiUtil.js"></script> <script src="static/js/index.js"></script>

</body>
</html>
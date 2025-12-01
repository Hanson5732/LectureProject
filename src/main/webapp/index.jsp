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
        <span class="logo-text">weave</span>
        <span class="logo-sub">LIVING</span>
      </a>

      <div class="nav-links">
<%--        <a href="#" class="nav-item">Why Weave</a>--%>
        <a href="#" class="nav-item">Cities <i class="fa-solid fa-angle-down"></i></a>
        <a href="#" class="nav-item">Living Options <i class="fa-solid fa-angle-down"></i></a>
<%--        <a href="#" class="nav-item">Offers</a>--%>
      </div>
    </div>

    <div class="nav-right">
      <%
        String username = (String) session.getAttribute("username");
      %>
      <div id="userInfo" class="user-info">

        <a href="#" class="btn-white-pill">Schedule Viewing</a>

        <% if (username != null) { %>
        <span class="nav-item">Hi, <%= username %></span>
        <a id="logoutLink" href="#" class="nav-item">Logout</a>
        <% } else { %>
        <a href="login.jsp" class="nav-item">Login</a>
        <% } %>

        <div class="nav-divider"></div>
      </div>
    </div>
  </nav>
</header>

<main class="hero-section">
  <div class="search-block">
    <div class="search-content">
      <div class="search-main">
        <div class="search-bar">
          <input type="text" id="searchInput" placeholder="Enter community, address...">
          <div class="search-buttons">
            <button class="btn-secondary" id="searchBtn">Search</button>
          </div>
        </div>

        <div class="quick-links-container">
          <div class="quick-links-col">
            <h4>Areas</h4>
            <div class="links-row" id="areaLinksContainer">
              Loading areas...
            </div>
          </div>
        </div>
      </div>
    </div>
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
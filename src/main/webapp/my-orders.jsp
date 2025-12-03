<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>My Orders - Estate Project</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="static/css/style.css">
    <style>
        .order-card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
            margin-bottom: 20px;
            padding: 20px;
            display: flex;
            align-items: center;
        }
        .order-img {
            width: 120px;
            height: 90px;
            border-radius: 4px;
            object-fit: cover;
            margin-right: 20px;
            background: #eee;
        }
        .order-info { flex-grow: 1; }
        .order-title { font-size: 18px; font-weight: bold; margin-bottom: 5px; color: #333; }
        .order-meta { font-size: 14px; color: #777; margin-bottom: 3px; }
        .order-status {
            font-weight: bold;
            padding: 5px 10px;
            border-radius: 4px;
            font-size: 12px;
            text-transform: uppercase;
        }
        .status-UNPAID { background: #ffeeba; color: #856404; }
        .status-PAID { background: #d4edda; color: #155724; }
        .status-CONFIRMED { background: #cce5ff; color: #004085; }
        .status-REFUND_REQUESTED { background: #f8d7da; color: #721c24; }

        .order-actions button {
            padding: 8px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: bold;
            margin-left: 10px;
        }
        .btn-pay { background: #e84a5f; color: white; }
        .btn-confirm { background: #1db99a; color: white; }
        .btn-refund { background: #6c757d; color: white; }
    </style>
</head>
<body>

<header class="top-bar" style="position: relative; background-color: #426354;">
    <nav class="nav-container">
        <div class="nav-left">
            <a href="index.jsp" class="logo">EstateProject</a>
        </div>
        <div class="nav-right">
            <% String username = (String) session.getAttribute("username"); %>
            <div class="user-info">
                <% if (username != null) { %>
                <span>Hi, <%= username %></span>
                <a href="#" onclick="logout()" class="nav-item">Logout</a>
                <% } else { %>
                <script>window.location.href="login.jsp";</script>
                <% } %>
            </div>
        </div>
    </nav>
</header>

<main class="section-container">
    <h2 class="section-title">My Orders</h2>
    <div id="ordersContainer">Loading...</div>
</main>

<script src="static/js/apiUtil.js"></script>
<script src="static/js/my-orders.js"></script>

</body>
</html>
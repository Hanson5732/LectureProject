<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Detail - Estate Project</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">

    <link rel="stylesheet" href="static/css/style.css">

    <style>
        /* 页面主体内容容器 */
        .details-container {
            width: 1200px;
            margin: 20px auto;
            display: flex;
            justify-content: space-between;
            font-family: 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
        }

        /* ---------------- */
        /* 左侧主要内容 */
        /* ---------------- */
        .property-main {
            width: 750px; /* 匹配截图中左侧的宽度 */
        }

        /* 面包屑导航 */
        .breadcrumb {
            font-size: 14px;
            color: #999;
            margin-bottom: 20px;
        }
        .breadcrumb a {
            color: #333;
            text-decoration: none;
        }
        .breadcrumb a:hover {
            text-decoration: underline;
        }

        /* 房源标题 */
        .property-main h1 {
            font-size: 28px;
            font-weight: 500;
            color: #333;
            margin: 0;
        }

        /* 标签 */
        .tags {
            margin-top: 10px;
        }
        .tags .tag {
            display: inline-block;
            padding: 3px 8px;
            margin-right: 5px;
            font-size: 12px;
            border-radius: 3px;
            background-color: #f2f2f2;
            color: #777;
        }
        .tags .tag-green {
            background-color: #e4f4ee;
            color: #1db99a;
        }

        /* 价格和统计栏 */
        .stats-bar {
            display: flex;
            align-items: flex-end;
            background: #f7f7f7;
            padding: 15px 20px;
            margin-top: 20px;
            border-radius: 4px;
        }
        .stats-bar .price {
            font-size: 30px;
            font-weight: bold;
            color: #e84a5f;
        }
        .stats-bar .price span {
            font-size: 14px;
            font-weight: normal;
            margin-left: 2px;
        }
        .stats-bar .stat {
            font-size: 20px;
            color: #333;
            margin-left: 40px;
        }
        .stats-bar .stat span {
            font-size: 14px;
        }

        /* 图片轮播区 */
        .image-carousel {
            margin-top: 25px;
            position: relative;
            width: 100%;
            height: 480px; /* 匹配图片高度 */
            background: #eee;
        }
        .image-carousel img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        .image-carousel .arrow {
            position: absolute;
            top: 50%;
            transform: translateY(-50%);
            width: 50px;
            height: 50px;
            background: rgba(0, 0, 0, 0.3);
            color: white;
            font-size: 30px;
            border-radius: 50%;
            cursor: pointer;
            display: flex;
            justify-content: center;
            align-items: center;
            text-decoration: none;
        }
        .image-carousel .arrow-left { left: 15px; }
        .image-carousel .arrow-right { right: 15px; }

        /* ---------------- */
        /* 右侧边栏 (留空) */
        /* ---------------- */
        .sidebar {
            width: 400px;
            height: 400px; /* 仅为占位，显示布局 */
            background: #f7f7f7;
            border: 1px dashed #ccc;
            /* (按要求留空) */
        }

    </style>
</head>
<body>

<header class="top-bar">
    <nav class="nav-container">
        <div class="nav-left">
            <a href="index.jsp" class="logo">EstateProject</a>
            <a href="#" class="city-selector">
                @ Beijing <i class="fa-solid fa-caret-down"></i>
            </a>
            <a href="#" class="nav-item">New Homes</a>
            <a href="#" class="nav-item">Second-Hand</a>
            <a href="#" class="nav-item">Rentals</a>
            <a href="#" class="nav-item">Commercial</a>
        </div>

        <div class="nav-right">
            <%
                // 检查 session 中是否存在 "fullName" 属性
                String userFullName = (String) session.getAttribute("fullName");
            %>

            <div id="userInfo" class="user-info">

                <% if (userFullName != null) { %>

                <span>Hello, <strong><%= userFullName %></strong>!</span>
                <a id="logoutLink" href="#" class="nav-item">Logout</a>

                <% } else { %>

                <a href="login.jsp" class="nav-item">Login</a>
                <a href="register.jsp" class="nav-item">Sign Up</a>

                <% } %>

            </div>
        </div>
    </nav>
</header>
<main class="details-container">

    <div class="property-main">

        <nav class="breadcrumb">
            <a href="index.jsp">北京安居客</a> &gt;
            <a href="#">北京租房</a> &gt;
            <a href="#">朝阳租房</a> &gt;
            <a href="#">北苑租房</a> &gt;
            <a href="#">龙泽公寓</a> &gt;
            龙泽公寓租房
        </nav>

        <h1>北苑 华贸城 龙湖 清河营17号线200米</h1>

        <div class="tags">
            <span class="tag">可短租</span>
            <span class="tag tag-green">两室一卫</span>
        </div>

        <div class="stats-bar">
            <div class="price">1800<span>元/月</span></div>
            <div class="stat">3<span>室</span>1<span>厅</span></div>
            <div class="stat">10.00<span>平方米</span></div>
        </div>

        <div class="image-carousel">
            <img src="775a1a99-a1fb-482e-807d-ac2a469cbb76.jpg" alt="室内图片">

            <a href="#" class="arrow arrow-left">&lt;</a>
            <a href="#" class="arrow arrow-right">&gt;</a>
        </div>

    </div>

    <aside class="sidebar">
    </aside>

</main>
<script src="static/js/index.js"></script>

</body>
</html>
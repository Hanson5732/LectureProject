<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="../static/css/style.css"> <style>
    .admin-container { display: flex; height: 100vh; }
    .sidebar { width: 250px; background: #333; color: white; padding-top: 20px; }
    .sidebar a { display: block; padding: 15px 20px; color: #ccc; text-decoration: none; }
    .sidebar a:hover { background: #444; color: white; }
    .sidebar a.active { background: #426354; color: white; }
    .main-content { flex: 1; padding: 30px; overflow-y: auto; }
    .stat-card { display: inline-block; width: 200px; padding: 20px; background: #f9f9f9; border-radius: 8px; margin-right: 20px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
</style>
</head>
<body>

<div class="admin-container">
    <div class="sidebar">
        <h3 style="text-align: center; margin-bottom: 30px;">Admin Panel</h3>
        <a href="index.jsp" class="active">Dashboard</a>
        <a href="rooms.jsp">Manage Rooms</a> <a href="orders.jsp">Manage Orders</a>
        <a href="#" onclick="logout()">Logout</a>
    </div>

    <div class="main-content">
        <h1>Welcome, Admin</h1>
        <p>System Overview</p>

        <div style="margin-top: 30px;">
            <div class="stat-card">
                <h3>Total Orders</h3>
                <p style="font-size: 24px; font-weight: bold;">--</p>
            </div>
            <div class="stat-card">
                <h3>Active Rooms</h3>
                <p style="font-size: 24px; font-weight: bold;">--</p>
            </div>
        </div>
    </div>
</div>

<script>
    async function logout() {
        await fetch("../logout", { method: "POST" }); // 注意路径是 ../logout
        window.location.href = "../login.jsp";
    }
</script>

</body>
</html>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Manage Rooms - Admin</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="../static/css/style.css">
    <style>
        /* 复用后台布局样式 */
        .admin-container { display: flex; height: 100vh; }
        .sidebar { width: 250px; background: #333; color: white; padding-top: 20px; flex-shrink: 0; }
        .sidebar a { display: block; padding: 15px 20px; color: #ccc; text-decoration: none; }
        .sidebar a:hover { background: #444; color: white; }
        .sidebar a.active { background: #426354; color: white; }
        .main-content { flex: 1; padding: 30px; overflow-y: auto; background: #f4f4f4; }

        /* 表格样式 */
        .admin-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
        .btn-add { background: #1db99a; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; font-weight: bold; }
        .data-table { width: 100%; border-collapse: collapse; background: white; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .data-table th, .data-table td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #eee; }
        .data-table th { background: #eee; color: #555; }
        .btn-edit { color: #3498db; cursor: pointer; margin-right: 10px; }
        .btn-delete { color: #e74c3c; cursor: pointer; }

        /* 模态框 (Modal) 样式 */
        .modal { display: none; position: fixed; z-index: 1000; left: 0; top: 0; width: 100%; height: 100%; background-color: rgba(0,0,0,0.5); }
        .modal-content { background-color: white; margin: 5% auto; padding: 25px; border-radius: 8px; width: 500px; position: relative; }
        .close-btn { position: absolute; right: 20px; top: 15px; font-size: 24px; cursor: pointer; color: #999; }

        /* 表单样式 */
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; font-size: 14px; }
        .form-group input, .form-group select, .form-group textarea { width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        .btn-submit { width: 100%; background: #426354; color: white; border: none; padding: 12px; border-radius: 4px; font-size: 16px; cursor: pointer; margin-top: 10px; }
    </style>
</head>
<body>

<div class="admin-container">
    <div class="sidebar">
        <h3 style="text-align: center; margin-bottom: 30px;">Admin Panel</h3>
        <a href="index.jsp">Dashboard</a>
        <a href="rooms.jsp" class="active">Manage Rooms</a>
        <a href="orders.jsp">Manage Orders</a>
        <a href="#" onclick="logout()">Logout</a>
    </div>

    <div class="main-content">
        <div class="admin-header">
            <h2>Room Management</h2>
            <button class="btn-add" onclick="openModal()">+ Add Room</button>
        </div>

        <table class="data-table">
            <thead>
            <tr>
                <th>ID</th>
                <th>Image</th>
                <th width="30%">Title</th>
                <th>Area</th>
                <th>Price</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody id="tableBody">
            <tr><td colspan="6" style="text-align:center;">Loading...</td></tr>
            </tbody>
        </table>
    </div>
</div>

<div id="roomModal" class="modal">
    <div class="modal-content">
        <span class="close-btn" onclick="closeModal()">&times;</span>
        <h3 id="modalTitle">Add New Room</h3>

        <form id="roomForm">
            <input type="hidden" id="roomId" name="id">

            <div class="form-group">
                <label>Title</label>
                <input type="text" id="title" name="title" required>
            </div>

            <div class="form-group" style="display: flex; gap: 10px;">
                <div style="flex: 1;">
                    <label>Price (¥/Month)</label>
                    <input type="number" id="price" name="price" required>
                </div>
                <div style="flex: 1;">
                    <label>Size (m²)</label>
                    <input type="number" id="size" name="size">
                </div>
            </div>

            <div class="form-group">
                <label>Area</label>
                <select id="areaId" name="areaId" required>
                    <option value="">Loading...</option>
                </select>
            </div>

            <div class="form-group" style="display: flex; gap: 10px;">
                <div style="flex: 1;">
                    <label>Building</label>
                    <select id="buildingId" name="buildingId" required>
                        <option value="">Select Area first</option>
                    </select>
                </div>
                <div style="flex: 1;">
                    <label>Room Type</label>
                    <select id="roomTypeId" name="roomTypeId" required>
                        <option value="">Loading...</option>
                    </select>
                </div>
            </div>

            <div class="form-group">
                <label>Image URL</label>
                <input type="text" id="coverImage" name="coverImage" placeholder="https://...">
            </div>

            <div class="form-group">
                <label>Description</label>
                <textarea id="description" name="description" rows="3"></textarea>
            </div>

            <button type="submit" class="btn-submit">Save Room</button>
        </form>
    </div>
</div>

<script src="../static/js/admin-rooms.js"></script>
<script>
    async function logout() {
        await fetch("../logout", { method: "POST" });
        window.location.href = "../login.jsp";
    }
</script>

</body>
</html>
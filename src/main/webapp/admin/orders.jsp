<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Manage Orders - Admin</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="../static/css/style.css">
    <style>
        /* 复用后台布局 */
        .admin-container { display: flex; height: 100vh; }
        .sidebar { width: 250px; background: #333; color: white; padding-top: 20px; flex-shrink: 0; }
        .sidebar a { display: block; padding: 15px 20px; color: #ccc; text-decoration: none; }
        .sidebar a:hover { background: #444; color: white; }
        .sidebar a.active { background: #426354; color: white; }
        .main-content { flex: 1; padding: 30px; overflow-y: auto; background: #f4f4f4; }

        .admin-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
        .data-table { width: 100%; border-collapse: collapse; background: white; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .data-table th, .data-table td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #eee; font-size: 14px; }
        .data-table th { background: #eee; color: #555; }

        .status-badge { padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: bold; }
        .st-UNPAID { background: #ffeeba; color: #856404; }
        .st-PAID { background: #d4edda; color: #155724; }
        .st-CONFIRMED { background: #cce5ff; color: #004085; }
        .st-REFUND_REQUESTED { background: #f8d7da; color: #721c24; }

        .btn-edit { color: #3498db; cursor: pointer; margin-right: 10px; }
        .btn-delete { color: #e74c3c; cursor: pointer; }

        /* Modal */
        .modal { display: none; position: fixed; z-index: 1000; left: 0; top: 0; width: 100%; height: 100%; background-color: rgba(0,0,0,0.5); }
        .modal-content { background-color: white; margin: 10% auto; padding: 25px; border-radius: 8px; width: 400px; position: relative; }
        .close-btn { position: absolute; right: 20px; top: 15px; font-size: 24px; cursor: pointer; color: #999; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
        .form-group input, .form-group select { width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        .btn-submit { width: 100%; background: #426354; color: white; border: none; padding: 10px; border-radius: 4px; cursor: pointer; }
    </style>
</head>
<body>

<div class="admin-container">
    <div class="sidebar">
        <h3 style="text-align: center; margin-bottom: 30px;">Admin Panel</h3>
        <a href="index.jsp">Dashboard</a>
        <a href="rooms.jsp">Manage Rooms</a>
        <a href="orders.jsp" class="active">Manage Orders</a>
        <a href="#" onclick="logout()">Logout</a>
    </div>

    <div class="main-content">
        <div class="admin-header">
            <h2>Order Management</h2>
        </div>

        <table class="data-table">
            <thead>
            <tr>
                <th>ID</th>
                <th>User</th>
                <th width="25%">Room</th>
                <th>Date Range</th>
                <th>Amount</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody id="tableBody">
            <tr><td colspan="7" style="text-align:center;">Loading...</td></tr>
            </tbody>
        </table>
    </div>
</div>

<div id="orderModal" class="modal">
    <div class="modal-content">
        <span class="close-btn" onclick="closeModal()">&times;</span>
        <h3>Edit Order</h3>
        <form id="orderForm">
            <input type="hidden" id="orderId" name="id">

            <div class="form-group">
                <label>Status</label>
                <select id="status" name="status">
                    <option value="UNPAID">UNPAID</option>
                    <option value="PAID">PAID</option>
                    <option value="CONFIRMED">CONFIRMED</option>
                    <option value="REFUND_REQUESTED">REFUND_REQUESTED</option>
                    <option value="REFUND_COMPLETED">REFUND_COMPLETED</option>
                </select>
            </div>

            <div class="form-group">
                <label>Amount (¥)</label>
                <input type="number" id="totalAmount" name="totalAmount" step="0.01">
            </div>

            <div class="form-group">
                <label>Start Date</label>
                <input type="date" id="startDate" name="startDate">
            </div>

            <div class="form-group">
                <label>End Date</label>
                <input type="date" id="endDate" name="endDate">
            </div>

            <button type="submit" class="btn-submit">Update Order</button>
        </form>
    </div>
</div>

<script src="../static/js/admin-orders.js"></script>
<script>
    async function logout() {
        await fetch("../logout", { method: "POST" });
        window.location.href = "../login.jsp";
    }
</script>

</body>
</html>
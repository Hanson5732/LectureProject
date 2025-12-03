document.addEventListener("DOMContentLoaded", () => {
    loadOrders();
    document.getElementById("orderForm").addEventListener("submit", handleUpdate);
});

// 1. 加载列表
async function loadOrders() {
    const tbody = document.getElementById("tableBody");
    try {
        const res = await fetch("../api/admin/orders");
        if (res.status === 401 || res.status === 403) {
            window.location.href = "../login.jsp";
            return;
        }
        const orders = await res.json();

        tbody.innerHTML = "";
        if (orders.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;">No orders found.</td></tr>';
            return;
        }

        orders.forEach(o => {
            const row = `
                <tr>
                    <td>${o.id}</td>
                    <td>${o.username}<br><small style="color:#888">${o.fullName || ''}</small></td>
                    <td>${o.roomTitle}</td>
                    <td>${o.startDate}<br>to ${o.endDate}</td>
                    <td>¥${o.totalAmount}</td>
                    <td><span class="status-badge st-${o.status}">${o.status}</span></td>
                    <td>
                        <i class="fa-solid fa-pen-to-square btn-edit" onclick='openEditModal(${JSON.stringify(o)})'></i>
                        <i class="fa-solid fa-trash btn-delete" onclick="deleteOrder(${o.id})"></i>
                    </td>
                </tr>
            `;
            tbody.innerHTML += row;
        });

    } catch (e) {
        console.error(e);
        tbody.innerHTML = '<tr><td colspan="7" style="text-align:center; color:red;">Error loading orders</td></tr>';
    }
}

// 2. 更新订单
async function handleUpdate(e) {
    e.preventDefault();
    const formData = new FormData(e.target);
    const data = Object.fromEntries(formData.entries());

    try {
        const res = await fetch("../api/admin/orders", {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });

        if (res.ok) {
            alert("Updated successfully!");
            closeModal();
            loadOrders();
        } else {
            // 显示具体的错误信息 (比如 "Admin can only change...")
            const errData = await res.json();
            alert("Update failed: " + (errData.error || res.statusText));
        }
    } catch (e) {
        alert("Error updating");
    }
}

// 3. 删除订单
async function deleteOrder(id) {
    if (!confirm("Are you sure you want to delete this order? This cannot be undone.")) return;

    try {
        const res = await fetch(`../api/admin/orders?id=${id}`, {
            method: "DELETE"
        });

        if (res.ok) {
            loadOrders();
        } else {
            alert("Delete failed");
        }
    } catch (e) {
        alert("Error deleting");
    }
}

// Modal Logic
const modal = document.getElementById("orderModal");

function openEditModal(order) {
    document.getElementById("orderId").value = order.id;
    document.getElementById("totalAmount").value = order.totalAmount;
    document.getElementById("startDate").value = order.startDate;
    document.getElementById("endDate").value = order.endDate;

    // [关键修改] 动态生成状态下拉框
    const statusSelect = document.getElementById("status");
    statusSelect.innerHTML = ""; // 清空原有选项

    // 1. 无论如何，先把当前状态加进去并选中
    const currentOpt = document.createElement("option");
    currentOpt.value = order.status;
    currentOpt.text = order.status;
    currentOpt.selected = true;
    statusSelect.add(currentOpt);

    // 2. 如果当前是 REFUND_REQUESTED，允许变成 REFUND_COMPLETED
    if (order.status === 'REFUND_REQUESTED') {
        const nextOpt = document.createElement("option");
        nextOpt.value = 'REFUND_COMPLETED';
        nextOpt.text = 'REFUND_COMPLETED';
        statusSelect.add(nextOpt);

        statusSelect.disabled = false; // 启用下拉框
    } else {
        // 其他状态不允许修改
        statusSelect.disabled = true; // 禁用下拉框
    }

    modal.style.display = "block";
}

function closeModal() {
    modal.style.display = "none";
}

window.onclick = function(e) {
    if (e.target == modal) closeModal();
}
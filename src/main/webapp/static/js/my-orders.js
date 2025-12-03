document.addEventListener("DOMContentLoaded", () => loadOrders());

async function loadOrders() {
    const container = document.getElementById("ordersContainer");
    try {
        const res = await fetch("api/orders/my");
        if (res.status === 401) {
            window.location.href = "login.jsp";
            return;
        }
        const orders = await res.json();

        container.innerHTML = "";
        if (orders.length === 0) {
            container.innerHTML = "<p>No orders found.</p>";
            return;
        }

        orders.forEach(order => {
            const div = document.createElement("div");
            div.className = "order-card";

            // 图片
            const imgUrl = order.roomImage || 'https://via.placeholder.com/150';

            // 按钮逻辑
            let actionsHtml = "";
            if (order.status === "UNPAID") {
                actionsHtml = `<button class="btn-pay" onclick="updateOrder(${order.id}, 'pay')">Pay Now</button>`;
            } else if (order.status === "PAID") {
                actionsHtml = `
                    <button class="btn-confirm" onclick="updateOrder(${order.id}, 'confirm')">Check-in</button>
                    <button class="btn-refund" onclick="updateOrder(${order.id}, 'refund')">Refund</button>
                `;
            } else {
                // 已完成或已退款的状态，可以不显示按钮，或者显示状态文本
                actionsHtml = `<span style="color:#999; font-size:12px;">${order.status}</span>`;
            }

            // 这里的 ${} 现在安全了，因为在 .js 文件中 JSP 引擎不会解析它
            div.innerHTML = `
                <img src="${imgUrl}" class="order-img">
                <div class="order-info">
                    <div class="order-title">${order.roomTitle || 'Unknown Room'}</div>
                    <div class="order-meta">Order ID: #${order.id} | Amount: ¥${order.totalAmount}</div>
                    <div class="order-meta">Date: ${order.startDate} to ${order.endDate}</div>
                    <div style="margin-top:8px;">
                        <span class="order-status status-${order.status}">${order.status}</span>
                    </div>
                </div>
                <div class="order-actions">
                    ${actionsHtml}
                </div>
            `;
            container.appendChild(div);
        });

    } catch (e) {
        console.error(e);
        container.innerHTML = "Error loading orders.";
    }
}

async function updateOrder(orderId, action) {
    if (!confirm("Are you sure you want to " + action + " this order?")) return;

    try {
        const res = await fetch("api/orders/action", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ orderId: orderId, action: action })
        });

        if (res.ok) {
            alert("Operation successful!");
            loadOrders(); // 刷新列表
        } else {
            alert("Operation failed");
        }
    } catch (e) {
        alert("Error: " + e.message);
    }
}

async function logout() {
    await fetch("logout", { method: "POST" });
    window.location.href = "login.jsp";
}
document.addEventListener("DOMContentLoaded", () => {
    loadStats();
});

async function loadStats() {
    try {
        const res = await fetch("../api/admin/stats"); // 注意相对路径
        if (res.status === 401) {
            window.location.href = "../login.jsp";
            return;
        }

        if (!res.ok) throw new Error("Failed to load stats");

        const data = await res.json();

        // 更新页面上的数字
        // 带有动画效果的数字跳动 (可选优化)
        animateValue("totalOrdersDisplay", 0, data.totalOrders, 1000);
        animateValue("activeRoomsDisplay", 0, data.activeRooms, 1000);

    } catch (e) {
        console.error(e);
        document.getElementById("totalOrdersDisplay").innerText = "Err";
        document.getElementById("activeRoomsDisplay").innerText = "Err";
    }
}

// 简单的数字滚动动画函数
function animateValue(id, start, end, duration) {
    const obj = document.getElementById(id);
    if (!obj) return;

    let startTimestamp = null;
    const step = (timestamp) => {
        if (!startTimestamp) startTimestamp = timestamp;
        const progress = Math.min((timestamp - startTimestamp) / duration, 1);
        obj.innerHTML = Math.floor(progress * (end - start) + start);
        if (progress < 1) {
            window.requestAnimationFrame(step);
        }
    };
    window.requestAnimationFrame(step);
}
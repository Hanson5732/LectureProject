document.addEventListener("DOMContentLoaded", function() {
    const params = new URLSearchParams(window.location.search);
    const roomId = params.get("id");

    if (!roomId) {
        alert("No property selected.");
        window.location.href = "index.jsp";
        return;
    }

    // 1. 加载详情
    loadRoomDetail(roomId);

    // 2. 初始化日历 (先获取占用情况)
    initCalendar(roomId);

    // 3. 绑定按钮
    const bookBtn = document.getElementById("bookBtn");
    if (bookBtn) {
        bookBtn.addEventListener("click", () => handleBooking(roomId)); // 传参 roomId
    }
});

// [新增] 初始化日历函数
async function initCalendar(roomId) {
    try {
        // 获取已被占用的日期
        const res = await fetch(`api/room-availability?roomId=${roomId}`);
        const bookedDates = await res.json(); // 格式: [{from: '...', to: '...'}, ...]

        // 初始化 Flatpickr
        flatpickr("#startDate", {
            minDate: "today",      // 禁止选今天之前的
            disable: bookedDates,  // [关键] 禁用已预订的日期段
            dateFormat: "Y-m-d",   // 格式
            // 可选：高亮显示样式
            locale: {
                firstDayOfWeek: 1 // 周一作为一周开始
            }
        });

    } catch (e) {
        console.error("Failed to load availability", e);
        // 如果出错，至少初始化一个基础日历
        flatpickr("#startDate", { minDate: "today" });
    }
}

async function handleBooking(roomId) {
    const startDate = document.getElementById("startDate").value;
    const duration = document.getElementById("duration").value;
    const guests = document.getElementById("guests").value;

    // 前端验证
    if (!startDate) {
        alert("Please select a check-in date.");
        return;
    }
    if (guests < 1 || guests > 4) {
        alert("Guests must be between 1 and 4.");
        return;
    }

    try {
        const response = await fetch("api/orders", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                roomId: roomId,
                startDate: startDate,
                duration: parseInt(duration),
                guests: parseInt(guests)
            })
        });

        const contentType = response.headers.get("content-type");

        if (response.ok) {
            const data = await response.json();
            alert(`Order created!\nTotal Amount: ¥${data.totalAmount}\nValid until: ${data.endDate}`);
            window.location.href = "my-orders.jsp";
        } else if (response.status === 401) {
            alert("Please login to book.");
            window.location.href = "login.jsp";
        } else if (response.status === 409) {
            const errData = await response.json();
            alert("Booking Failed: " + errData.message);
        } else {
            if (contentType && contentType.indexOf("application/json") !== -1) {
                const errData = await response.json();
                alert("Booking failed: " + (errData.message || "Unknown error"));
            } else {
                console.error("Server Error (HTML response received)");
                alert("Booking failed: Internal Server Error. Please contact admin.");
            }
        }
    } catch (error) {
        console.error("Booking error:", error);
        alert("An error occurred during booking. Please try again.");
    }
}

async function loadRoomDetail(id) {
    try {
        const res = await fetch("api/room-detail?id=" + id);
        if (!res.ok) throw new Error("Failed to load data");

        const room = await res.json();

        document.getElementById("crumbArea").textContent = room.areaName || "Area";
        document.getElementById("crumbTitle").textContent = room.title;
        document.getElementById("propTitle").textContent = room.title;
        document.getElementById("propPrice").innerHTML = `¥${room.price}<span>/Month</span>`;
        document.getElementById("propType").textContent = room.roomTypeName || "Apartment";
        document.getElementById("propSize").innerHTML = `${room.size}<span>m²</span>`;
        document.getElementById("propDesc").textContent = room.description || "No description provided.";

        if (room.coverImage) {
            document.getElementById("propImage").src = room.coverImage;
        } else {
            document.getElementById("propImage").src = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=800&q=80";
        }

    } catch (e) {
        console.error(e);
        document.getElementById("propTitle").textContent = "Property not found.";
    }
}

async function logout() {
    await fetch("logout", { method: "POST" });
    window.location.href = "login.jsp";
}
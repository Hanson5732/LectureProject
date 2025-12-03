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

    // 2. 初始化日历 (Easepick)
    initEasepick(roomId);

    // 3. 绑定按钮
    const bookBtn = document.getElementById("bookBtn");
    if (bookBtn) {
        bookBtn.addEventListener("click", () => handleBooking(roomId));
    }
});

// [新增] 初始化 Easepick 日历
async function initEasepick(roomId) {
    try {
        // 1. 获取占用数据
        const res = await fetch(`api/room-availability?roomId=${roomId}`);
        const bookedData = await res.json(); // [{from: '2023-01-01', to: '2023-01-05'}, ...]

        // 2. 转换数据格式为 Easepick 所需的格式: [['start', 'end'], ['start', 'end']]
        const bookedDates = bookedData.map(item => [item.from, item.to]);

        // 3. 创建日历实例
        const picker = new easepick.create({
            element: document.getElementById('startDate'),
            css: [
                'https://cdn.jsdelivr.net/npm/@easepick/bundle@1.2.1/dist/index.css',
            ],
            zIndex: 1000, // 防止被其他层遮挡
            plugins: ['LockPlugin'], // 启用锁定插件
            LockPlugin: {
                minDate: new Date(), // 禁止选今天之前的
                booked: bookedDates  // 传入已预订的日期数组
            },
            setup(picker) {
                // 可选：当用户选择日期时触发事件
                picker.on('select', (e) => {
                    const { date } = e.detail;
                    // console.log("Selected date:", date);
                });
            }
        });

    } catch (e) {
        console.error("Calendar init failed", e);
        // 如果初始化失败，回退到普通输入框，不至于让页面挂掉
    }
}

async function handleBooking(roomId) {
    const startDate = document.getElementById("startDate").value;
    const duration = document.getElementById("duration").value;
    const guests = document.getElementById("guests").value;

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
            headers: { "Content-Type": "application/json" },
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
                console.error("Server Error HTML");
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
        document.getElementById("propDesc").textContent = room.description || "No description.";

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
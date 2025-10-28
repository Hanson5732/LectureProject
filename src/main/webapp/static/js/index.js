/**
 * 页面加载完成后立即执行
 */
document.addEventListener("DOMContentLoaded", function() {

    // 检查 Access Token 是否存在
    const token = localStorage.getItem("accessToken");

    if (token) {
        // 1. 如果有 Token，我们尝试获取用户信息
        console.log("Token found. Attempting to fetch profile.");
        fetchProfile();
    } else {
        // 2. 如果没有 Token，什么也不做
        // 页面将显示 /index.jsp 中默认的 "Login" / "Sign Up" 链接
        console.log("No token found. Displaying public homepage.");
    }
});

/**
 * 异步函数，用于调用 /api/profile
 * (此函数与之前相同)
 */
async function fetchProfile() {
    try {
        // 使用 apiFetch，它会自动处理 Token 和刷新
        const response = await apiFetch("api/profile", {
            method: "GET"
        });

        if (response.ok) {
            // [成功] 获取到用户信息
            const user = await response.json();
            updateUserInfo(user);
        } else {
            // [失败] 可能是 401 (即使刷新也失败了) 或 500
            // apiFetch 内部的 refreshToken() 会处理重定向
            // 我们只需要记录错误，并允许登出
            console.error("Failed to fetch profile:", response.status);

            // 如果 token 确定无效 (例如 401)，apiUtil 会自动跳转
            // 如果是其他错误 (例如 500)，我们也登出以清理状态
            if (response.status === 500) {
                window.logout(); // 调用 apiUtil.js 中的登出函数
            }
        }

    } catch (error) {
        // [网络错误]
        console.error("Network error fetching profile:", error);
        // 不再强制登出，可能只是暂时网络问题
        // alert("Network error trying to fetch profile.");
    }
}

/**
 * 更新导航栏中的用户信息
 * (此函数与之前相同)
 * @param {object} user - 从 /api/profile 返回的用户对象
 */
function updateUserInfo(user) {
    const userInfoDiv = document.getElementById("userInfo");

    if (userInfoDiv) {
        // [cite: hanson5732/lectureproject/LectureProject-0efab5c5dd076978425e1382cdb23f0b336a53bf/src/main/java/com/estate/lectureproject/entity/User.java]
        userInfoDiv.innerHTML = `
            <span>Welcome, <strong>${user.fullName}</strong> (${user.role})</span>
            <a id="logoutLink" href="#">Logout</a>
        `;

        // 为新的 "Logout" 链接添加事件监听
        document.getElementById("logoutLink").addEventListener("click", function(e) {
            e.preventDefault();
            // 调用 apiUtil.js 中定义的全局 logout 函数
            window.logout();
        });
    }
}


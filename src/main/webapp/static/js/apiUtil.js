/**
 * 尝试刷新 Access Token
 * 这是一个独立的函数，在 apiFetch 内部被调用
 * 它会调用 /refresh-token Servlet
 */
async function refreshToken() {
    console.log("Access Token expired. Attempting to refresh...");

    try {
        // [重要] fetch /refresh-token 时，不需要 Authorization header
        // 浏览器会自动发送 HttpOnly Cookie (refreshToken)
        const response = await fetch("refresh-token", {
            method: "POST"
        });

        if (response.ok) {
            const data = await response.json();

            // [成功] 获得了新的 Access Token
            console.log("Token refreshed successfully.");
            localStorage.setItem("accessToken", data.accessToken);
            return true; // 表示刷新成功

        } else {
            // [失败] Refresh Token 也过期了, 或被吊销
            console.error("Failed to refresh token. User must log in again.");
            // 清除所有登录信息
            localStorage.removeItem("accessToken");
            localStorage.removeItem("userRole");

            // 重定向到登录页
            window.location.href = "login.jsp";
            return false; // 表示刷新失败
        }
    } catch (error) {
        console.error("Error during token refresh:", error);
        localStorage.removeItem("accessToken");
        localStorage.removeItem("userRole");
        window.location.href = "login.jsp";
        return false; // 表示刷新失败
    }
}

/**
 * 包装了原生 fetch 的 apiFetch 函数
 * 所有对受保护 API (/api/*) 的调用都应使用此函数
 *
 * @param {string} url - 要请求的 API 路径 (例如: "api/profile")
 * @param {object} options - 原生 fetch 的配置对象 (method, body, etc.)
 * @returns {Promise<Response>} - 返回原生的 fetch 响应
 */
async function apiFetch(url, options = {}) {

    // 1. 准备请求
    const requestOptions = {
        ...options,
        headers: {
            ...options.headers,
            // 默认添加 JSON content-type，如果需要可以覆盖
            "Content-Type": "application/json",
            "Accept": "application/json"
        }
    };

    // 2. 从 localStorage 获取 Access Token
    const token = localStorage.getItem("accessToken");

    // 3. 如果 Token 存在，添加到 Authorization 请求头
    if (token) {
        requestOptions.headers["Authorization"] = `Bearer ${token}`;
    }

    // 4. 执行第一次请求
    let response = await fetch(url, requestOptions);

    // 5. 检查是否为 "401 Unauthorized" (Token 过期)
    // 并且我们*不是*正在尝试刷新 token (避免死循环)
    if (response.status === 401 && !url.includes("refresh-token")) {

        // 6. 尝试刷新 Token
        const refreshSuccess = await refreshToken();

        if (refreshSuccess) {
            // 7. [重试] 刷新成功，使用 *新* 的 Token 重新发起请求
            console.log("Retrying original request with new token.");

            // 更新请求头中的 Token
            requestOptions.headers["Authorization"] = `Bearer ${localStorage.getItem("accessToken")}`;

            // 执行第二次 (重试) 请求
            response = await fetch(url, requestOptions);
        }
    }

    // 8. 返回最终的响应 (无论是第一次成功，还是重试后成功)
    return response;
}

/**
 * [可选] 登出函数
 * 它需要调用后端的 /logout 接口 (我们稍后创建) 来吊销 refresh token
 */
async function logout() {
    // 1. 清除前端的 Token
    localStorage.removeItem("accessToken");
    localStorage.removeItem("userRole");

    // 2. (理想情况下) 通知后端吊销 Refresh Token
    // 我们可以创建一个 /logout Servlet 来处理这个
    try {
        await fetch("logout", { method: "POST" });
    } catch (error) {
        console.error("Error during logout:", error);
    }

    // 3. 跳转到登录页
    window.location.href = "login.jsp";
}

// 导出函数 (如果你的项目使用模块，但在这里我们暂时挂载到 window)
// 这样在其他 <script> 标签中就可以直接使用 apiFetch()
window.apiFetch = apiFetch;
window.logout = logout;
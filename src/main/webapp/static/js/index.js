document.addEventListener("DOMContentLoaded", function() {

    // 1. 查找 "Logout" 链接
    const logoutLink = document.getElementById("logoutLink");

    if (logoutLink) {
        // 2. 如果找到了 (意味着用户已登录), 为它添加点击事件
        logoutLink.addEventListener("click", function(e) {
            e.preventDefault(); // 阻止链接的默认跳转行为
            logout(); // 调用登出函数
        });
    }

    /**
     * 登出函数
     * 调用后端的 /logout 接口来销毁 session
     */
    async function logout() {
        await fetch("logout", { method: "POST" });
    }
});
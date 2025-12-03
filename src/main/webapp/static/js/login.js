/**
 * 监听 DOM 内容加载完成事件
 */
document.addEventListener("DOMContentLoaded", function() {

    // 1. 获取表单和按钮元素
    const loginForm = document.getElementById("loginForm");

    if (loginForm) {
        // 2. 为表单添加 "submit" 事件监听
        loginForm.addEventListener("submit", function(event) {

            // 3. 阻止表单的默认提交行为
            event.preventDefault();

            // 4. 获取表单上的按钮和复选框
            const loginButton = loginForm.querySelector(".login-btn");
            const agreeCheckbox = document.getElementById("agree");

            // 5. 验证：检查协议是否勾选
            if (!agreeCheckbox.checked) {
                alert("Please read and agree to the User Service Agreement & Privacy Policy.");
                return;
            }

            // 6. 获取用户名和密码
            const username = document.getElementById("username").value;
            const password = document.getElementById("password").value;

            // 7. [重要] 准备要发送到 Servlet 的数据
            const formData = new URLSearchParams();
            formData.append("username", username);
            formData.append("password", password);

            // 8. 禁用按钮，防止重复提交
            loginButton.disabled = true;
            loginButton.textContent = "Logging in...";

            // 9. 使用 fetch API 发送 POST 请求到 /login Servlet
            fetch("login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: formData
            })
                .then(response => {
                    // 10. 检查响应状态
                    if (response.ok) {
                        return response.json();
                    } else {
                        return response.text().then(text => {
                            throw new Error(text || `Error ${response.status}`);
                        });
                    }
                })
                .then(data => {
                    if (data.role === 'ADMIN') {
                        // 管理员 -> 后台
                        window.location.href = "admin/index.jsp";
                    } else {
                        // 普通用户 -> 首页
                        window.location.href = "index.jsp";
                    }
                })
                .catch(error => {
                    // 12. [失败] 捕获所有错误
                    let friendlyMessage = "Login failed. Please check your username and password.";
                    if (error.message.includes("(")) {
                        friendlyMessage = error.message.substring(error.message.indexOf('(') + 1, error.message.lastIndexOf(')'));
                    } else if (error.message.includes("Failed to fetch")) {
                        friendlyMessage = "Network error or server is down.";
                    }

                    alert(friendlyMessage);

                    // 13. 恢复按钮
                    loginButton.disabled = false;
                    loginButton.textContent = "Login";
                });
        });
    }
});
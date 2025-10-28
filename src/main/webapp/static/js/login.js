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
            // 我们的 Java Servlet 使用 request.getParameter()
            // 这需要 "application/x-www-form-urlencoded" 格式的数据
            const formData = new URLSearchParams();
            formData.append("username", username);
            formData.append("password", password);

            // 8. 禁用按钮，防止重复提交
            loginButton.disabled = true;
            loginButton.textContent = "Logging in...";

            // 9. 使用 fetch API 发送 POST 请求到 /login Servlet
            fetch("login", { // "login" 会被解析为相对路径 -> /login
                method: "POST",
                headers: {
                    // 告诉服务器我们发送的是什么类型的数据
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: formData // 发送格式化后的数据
            })
                .then(response => {
                    // 10. 检查响应状态
                    if (response.ok) {
                        // 登录成功 (状态码 200-299)
                        // 解析后端返回的 JSON 数据
                        return response.json();
                    } else {
                        // 登录失败 (例如 401, 404, 500)
                        // Servlet 的 sendError 会返回文本，我们读取它
                        return response.text().then(text => {
                            // 将错误文本包装成一个 Error 对象抛出
                            throw new Error(text || `Error ${response.status}`);
                        });
                    }
                })
                .then(data => {
                    // 11. [成功] 登录成功，"data" 是解析后的 JSON
                    console.log("Login successful:", data);

                    // [关键] 将 Access Token 存储在 localStorage 中
                    // 这样其他页面或JS文件就可以获取它
                    localStorage.setItem("accessToken", data.accessToken);
                    localStorage.setItem("userRole", data.role); // 存储角色

                    // 提示用户并跳转到主页
                    alert("Login successful!");
                    window.location.href = "index.jsp"; // 登录后跳转到主页

                })
                .catch(error => {
                    // 12. [失败] 捕获所有错误 (网络错误或登录失败)
                    console.error("Login failed:", error);

                    // 从 Servlet 返回的 401 错误中提取消息
                    // "Error: Unauthorized (用户名或密码无效。)" -> "用户名或密码无效。"
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
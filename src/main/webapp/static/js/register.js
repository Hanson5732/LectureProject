/**
 * 监听 DOM 内容加载完成事件
 */
document.addEventListener("DOMContentLoaded", function() {

    // --- 获取表单和按钮 ---
    const registerForm = document.getElementById("registerForm");
    const registerButton = registerForm.querySelector(".login-btn");
    const agreeCheckbox = document.getElementById("agree");

    // --- 获取密码相关字段 ---
    const passwordInput = document.getElementById("password");
    const confirmPasswordInput = document.getElementById("confirmPassword");

    // --- 获取图标 ---
    const validationIcon = document.getElementById("validationIcon");
    const togglePasswordIcon = document.getElementById("togglePassword");
    const toggleConfirmPasswordIcon = document.getElementById("toggleConfirmPassword");

    /**
     * [新] 1. 切换密码可见性的函数
     */
    function togglePasswordVisibility(input, icon) {
        if (input.type === "password") {
            input.type = "text";
            icon.classList.remove("fa-eye-slash");
            icon.classList.add("fa-eye"); // 切换为“睁开的眼睛”
        } else {
            input.type = "password";
            icon.classList.remove("fa-eye");
            icon.classList.add("fa-eye-slash"); // 切换为“关闭的眼睛”
        }
    }

    // 为两个眼睛图标添加点击事件
    togglePasswordIcon.addEventListener("click", function() {
        togglePasswordVisibility(passwordInput, this);
    });

    toggleConfirmPasswordIcon.addEventListener("click", function() {
        togglePasswordVisibility(confirmPasswordInput, this);
    });

    /**
     * [新] 2. 实时验证密码是否匹配的函数
     */
    function validatePasswordMatch() {
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        // 如果确认密码框为空，则隐藏图标
        if (confirmPassword.length === 0) {
            validationIcon.style.display = "none";
            return;
        }

        // 密码匹配
        if (password === confirmPassword) {
            validationIcon.style.display = "inline-block";
            validationIcon.classList.remove("fa-times");
            validationIcon.classList.add("fa-check"); // 绿色勾
            validationIcon.style.color = "green";
        }
        // 密码不匹配
        else {
            validationIcon.style.display = "inline-block";
            validationIcon.classList.remove("fa-check");
            validationIcon.classList.add("fa-times"); // 红色叉
            validationIcon.style.color = "red";
        }
    }

    // 为两个密码输入框添加 'keyup' 事件，以便实时验证
    passwordInput.addEventListener("keyup", validatePasswordMatch);
    confirmPasswordInput.addEventListener("keyup", validatePasswordMatch);


    /**
     * 3. 处理表单提交
     */
    if (registerForm) {
        registerForm.addEventListener("submit", function(event) {

            // 阻止表单的默认提交行为
            event.preventDefault();

            // --- 客户端验证 ---
            if (!agreeCheckbox.checked) {
                alert("Please read and agree to the User Service Agreement & Privacy Policy.");
                return;
            }

            // 再次检查密码是否匹配
            if (passwordInput.value !== confirmPasswordInput.value) {
                alert("The password and the confirmation password do not match.");
                return;
            }

            // 准备要发送到 Servlet 的数据
            const formData = new FormData(registerForm);
            const urlEncodedData = new URLSearchParams(formData);

            // 禁用按钮
            registerButton.disabled = true;
            registerButton.textContent = "Processing...";

            // 使用 fetch API 发送 POST 请求到 /register Servlet
            fetch("register", { //
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: urlEncodedData
            })
                .then(response => {
                    if (response.status === 201) { // 201 Created
                        return response.json();
                    } else {
                        return response.text().then(text => {
                            throw new Error(text || `Error ${response.status}`);
                        });
                    }
                })
                .then(data => {
                    // 注册成功
                    alert(data.message || "Registration successful! Please login.");
                    window.location.href = "login.jsp";
                })
                .catch(error => {
                    // 注册失败

                    let friendlyMessage = "Registration failed. Please try again.";
                    if (error.message.includes("(")) {
                        friendlyMessage = error.message.substring(error.message.indexOf('(') + 1, error.message.lastIndexOf(')'));
                    } else if (error.message.includes("Failed to fetch")) {
                        friendlyMessage = "Network error or server is down.";
                    }

                    alert(friendlyMessage);

                    // 恢复按钮
                    registerButton.disabled = false;
                    registerButton.textContent = "Sign Up";
                });
        });
    }
});
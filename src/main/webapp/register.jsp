<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sign Up - Estate Project</title>
    <link rel="stylesheet" href="static/css/login.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">

    <style>
        .login-container {
            width: 450px;
        }
        .input-group {
            margin-bottom: 15px;
            position: relative;
        }
        .password-toggle {
            position: absolute;
            right: 15px;
            top: 50%;
            transform: translateY(-50%);
            color: #999;
            cursor: pointer;
            z-index: 2;
        }
        .password-toggle:hover {
            color: #333;
        }
        .validation-icon {
            position: absolute;
            right: 45px;
            top: 50%;
            transform: translateY(-50%);
            display: none;
            z-index: 2;
        }
        .validation-icon.fa-check {
            color: green;
        }
        .validation-icon.fa-times {
            color: red;
        }
        .role-selector {
            display: flex;
            justify-content: space-around;
            margin-bottom: 15px;
        }
        .role-selector label {
            font-size: 14px;
            color: #555;
            cursor: pointer;
        }
    </style>
</head>
<body>

<div class="login-container">
    <div class="form-wrapper">
        <h2>Create Account</h2>
        <div class="header-links">
            <a href="login.jsp">Login</a>
        </div>

        <!-- 注册表单 -->
        <form id="registerForm">

            <div class="input-group role-selector">
                <label>
                    <input type="radio" name="role" value="user" checked>
                    I am a User
                </label>
                <label>
                    <input type="radio" name="role" value="agent">
                    I am an Agent
                </label>
            </div>

            <div class="input-group">
                <i class="fa-solid fa-user icon"></i>
                <input type="text" id="username" name="username" placeholder="Username" required>
            </div>

            <div class="input-group">
                <i class="fa-solid fa-signature icon"></i>
                <input type="text" id="fullName" name="fullName" placeholder="Full Name" required>
            </div>

            <div class="input-group">
                <i class="fa-solid fa-id-card icon"></i>
                <input type="text" id="idCardNumber" name="idCardNumber" placeholder="ID Card Number" required>
            </div>

            <div class="input-group">
                <i class="fa-solid fa-phone icon"></i>
                <input type="text" id="phoneNumber" name="phoneNumber" placeholder="Phone Number" required>
            </div>

            <div class="input-group">
                <i class="fa-solid fa-lock icon"></i>
                <input type="password" id="password" name="password" placeholder="Password" required>
                <!-- [修正] 默认图标改为 fa-eye-slash -->
                <i class="fa-solid fa-eye-slash password-toggle" id="togglePassword"></i>
            </div>

            <div class="input-group">
                <i class="fa-solid fa-lock icon"></i>
                <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Confirm Password" required>
                <i class="fa-solid validation-icon" id="validationIcon"></i>
                <!-- [修正] 默认图标改为 fa-eye-slash -->
                <i class="fa-solid fa-eye-slash password-toggle" id="toggleConfirmPassword"></i>
            </div>

            <div class="agreement">
                <input type="checkbox" id="agree" name="agree" checked>
                <label for="agree">I have read and agree to the
                    <a href="#">User Service Agreement</a> &
                    <a href="#">Privacy Policy</a>
                </label>
            </div>

            <button type="submit" class="login-btn">Sign Up</button>
        </form>

    </div>
</div>

<script src="static/js/register.js"></script>
</body>
</html>

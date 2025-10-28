<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login - Estate Project</title>
    <link rel="stylesheet" href="static/css/login.css">

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>

<div class="login-container">

    <div class="form-wrapper">

        <h2>Account Login</h2>

        <div class="header-links">
            <a href="register.jsp">Sign Up</a>
        </div>

        <form id="loginForm">
            <div class="input-group">
                <i class="fa-solid fa-user icon"></i>
                <input type="text" id="username" name="username" placeholder="Enter Username/Email/Phone" required>
            </div>

            <div class="input-group">
                <i class="fa-solid fa-lock icon"></i>
                <input type="password" id="password" name="password" placeholder="Enter Password" required>
            </div>

            <div class="agreement">
                <input type="checkbox" id="agree" name="agree" checked>
                <label for="agree">I have read and agree to the
                    <a href="#">User Service Agreement</a> &
                    <a href="#">Privacy Policy</a>
                </label>
            </div>

            <button type="submit" class="login-btn">Login</button>
        </form>

        <div class="footer-links">
            <a href="#">Forgot Password?</a>
        </div>

    </div>
</div>

<script src="static/js/login.js"></script>

</body>
</html>
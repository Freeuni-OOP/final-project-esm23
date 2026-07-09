<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Register — Quiz Website</title>
    <link rel="stylesheet" href="css/main.css">
</head>
<body class="site-wrapper">

<nav class="navbar">
    <div class="container">
        <a class="navbar-brand" href="index.jsp">&#9670; Quiz Website</a>
        <span class="navbar-spacer"></span>
        <ul class="navbar-nav">
            <li><a href="login.jsp">Log In</a></li>
            <li><a href="register.jsp">Register</a></li>
        </ul>
    </div>
</nav>

<div class="main-content auth-wrapper">
    <div class="auth-card">
        <h1>Create Account</h1>

        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error">
            <span class="alert-icon">&#9888;</span>
            <%= request.getAttribute("error") %>
        </div>
        <% } %>

        <form method="post" action="RegisterServlet">
            <div class="form-group">
                <label class="form-label" for="username">Username</label>
                <input class="form-control" type="text" id="username" name="username" required autofocus
                       value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>">
            </div>
            <div class="form-group">
                <label class="form-label" for="password">Password</label>
                <input class="form-control" type="password" id="password" name="password" required>
            </div>
            <div class="form-actions">
                <button class="btn btn-primary w-100" type="submit">Create Account</button>
            </div>
        </form>

        <p class="auth-footer">Already have an account? <a href="login.jsp">Log in</a></p>
    </div>
</div>

<footer class="site-footer">
    <div class="container">&copy; 2025 Quiz Website</div>
</footer>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="js/main.js"></script>
</body>
</html>
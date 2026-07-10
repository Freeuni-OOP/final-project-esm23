<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head><title>Log In</title></head>
<body>
    <h1>Log In</h1>

    <% if (request.getAttribute("registered") != null) { %>
        <p style="color:green;">Registration successful. You can now log in.</p>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
        <p style="color:red;"><%= request.getAttribute("error") %></p>
    <% } %>

    <form method="post" action="LoginServlet">
        <label>Username: <input type="text" name="username" required></label><br/>
        <label>Password: <input type="password" name="password" required></label><br/>
        <button type="submit">Log In</button>
    </form>

    <p>Don't have an account? <a href="register.jsp">Register</a></p>
</body>
</html>

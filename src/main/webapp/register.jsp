<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head><title>Register</title></head>
<body>
<h1>Register</h1>
    <% if (request.getAttribute("error") != null) { %>
        <p style="color:red;"><%= request.getAttribute("error") %></p>
    <% } %>

    <form method="post" action="RegisterServlet">
        <label>Username: <input type="text" name="username" required
                        value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>"></label><br/>
        <label>Password: <input type="password" name="password" required></label><br/>
        <button type="submit">Register</button>
    </form>

    <p>Already have an account? <a href="login.jsp">Log in</a></p>
</body>
</html>
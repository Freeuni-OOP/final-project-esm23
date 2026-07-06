<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head><title>Log In</title></head>
<body>
    <h1>Log In</h1>

    <!-- TODO: placeholder login page - wire up to a LoginServlet that sets session attribute "user" -->
    <form method="post" action="LoginServlet">
        <label>Username: <input type="text" name="username" required></label><br/>
        <label>Password: <input type="password" name="password" required></label><br/>
        <button type="submit">Log In</button>
    </form>
</body>
</html>

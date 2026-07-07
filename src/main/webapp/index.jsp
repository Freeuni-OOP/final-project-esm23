<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head><title>Quiz Website</title></head>
<body>
    <h1>Welcome to the Quiz Website</h1>

    <% if (session.getAttribute("user") == null) { %>
        <p><a href="login.jsp">Log in</a> to get started.</p>
    <% } else { %>
        <p>You are logged in.</p>
        <ul>
            <li><a href="WEB-INF/jsp/createQuiz.jsp">Create a new quiz</a></li>
            <li><a href="TakeQuizServlet">Take a quiz</a></li>
        </ul>
         <li><a href="LogoutServlet">Log out</a></li>
    <% } %>

    <!-- TODO: placeholder landing page - flesh out with real quiz listing/navigation -->
</body>
</html>

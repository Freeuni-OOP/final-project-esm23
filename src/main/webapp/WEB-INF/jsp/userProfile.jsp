<%@ page import="com.quizwebsite.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User profileUser = (User) request.getAttribute("profileUser");
    Integer friendCount = (Integer) request.getAttribute("friendCount");
    Integer createdQuizCount = (Integer) request.getAttribute("createdQuizCount");
    Integer takenQuizCount = (Integer) request.getAttribute("takenQuizCount");
    Boolean isOwnProfile = (Boolean) request.getAttribute("isOwnProfile");
    User loggedInUser = (User) session.getAttribute("user");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= profileUser.getUsername() %> — Quiz Website</title>
    <link rel="stylesheet" href="css/main.css">
</head>
<body class="site-wrapper">

<nav class="navbar">
    <div class="container">
        <a class="navbar-brand" href="index.jsp">&#9670; Quiz Website</a>
        <span class="navbar-spacer"></span>
        <ul class="navbar-nav">
            <li><a href="index.jsp">Home</a></li>
            <li><a href="LogoutServlet">Log Out</a></li>
        </ul>
        <% if (loggedInUser != null) { %>
            <div class="navbar-user">Welcome, <strong><%= loggedInUser.getUsername() %></strong></div>
        <% } %>
    </div>
</nav>

<div class="main-content">
    <div class="container">

        <div class="card">
            <h1><%= profileUser.getUsername() %></h1>

            <h3 style="margin-top:1rem;">Statistics</h3>
            <p>Friends: <strong><%= friendCount %></strong></p>
            <p>Quizzes Created: <strong><%= createdQuizCount %></strong></p>
            <p>Quizzes Taken: <strong><%= takenQuizCount %></strong></p>

            <div class="card-actions" style="margin-top:1.5rem;">
                <% if (!isOwnProfile) { %>
                    <button class="btn btn-primary">Add Friend</button>
                <% } %>
                <a class="btn btn-outline" href="HomeServlet">Back to Home</a>
            </div>
        </div>

    </div>
</div>

<footer class="site-footer">
    <div class="container">&copy; 2025 Quiz Website</div>
</footer>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="js/main.js"></script>
</body>
</html>

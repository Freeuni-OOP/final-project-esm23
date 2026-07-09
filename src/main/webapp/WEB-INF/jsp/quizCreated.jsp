<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.quizwebsite.model.User" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    User user = (User) session.getAttribute("user");
    String quizId = request.getParameter("quizId");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quiz Created! — Quiz Website</title>
    <link rel="stylesheet" href="css/main.css">
</head>
<body class="site-wrapper">

<nav class="navbar">
    <div class="container">
        <a class="navbar-brand" href="index.jsp">&#9670; Quiz Website</a>
        <span class="navbar-spacer"></span>
        <ul class="navbar-nav">
            <li><a href="index.jsp">Home</a></li>
            <li><a href="CreateQuizServlet">+ Create Quiz</a></li>
            <li><a href="LogoutServlet">Log Out</a></li>
        </ul>
        <div class="navbar-user">Welcome, <strong><%= user.getUsername() %></strong></div>
    </div>
</nav>

<div class="main-content">
    <div class="container">

        <div class="card text-center" style="padding:3rem;max-width:500px;margin:0 auto;">
            <div style="font-size:3rem;margin-bottom:1rem;">&#127881;</div>
            <h1 style="font-size:1.75rem;margin-bottom:.5rem;">Quiz Created!</h1>
            <p class="text-muted mb-3">Your quiz has been saved successfully.</p>
            <div class="d-flex gap-1" style="justify-content:center;">
                <% if (quizId != null) { %>
                <a href="ReviewQuizServlet?quizId=<%= quizId %>" class="btn btn-outline">View Quiz</a>
                <a href="TakeQuizServlet?quizId=<%= quizId %>" class="btn btn-primary">Take It Now</a>
                <% } %>
                <a href="index.jsp" class="btn btn-secondary">Back to Home</a>
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

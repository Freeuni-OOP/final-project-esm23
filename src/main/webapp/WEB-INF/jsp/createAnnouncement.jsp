<%@ page import="java.util.List" %>
<%@ page import="com.quizwebsite.dao.AnnouncementDAO.Announcement" %>
<%@ page import="com.quizwebsite.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User user = (User) session.getAttribute("user");
    List<Announcement> announcements = (List<Announcement>) request.getAttribute("announcements");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Announcements — Quiz Website</title>
    <link rel="stylesheet" href="css/main.css">
</head>
<body class="site-wrapper">

<nav class="navbar">
    <div class="container">
        <a class="navbar-brand" href="index.jsp">&#9670; Quiz Website</a>
        <span class="navbar-spacer"></span>
        <ul class="navbar-nav">
            <li><a href="index.jsp">Home</a></li>
            <% if (user != null && user.isAdmin()) { %>
            <li><a href="create-announcement">+ Post Announcement</a></li>
            <% } %>
            <% if (user != null) { %><li><a href="LogoutServlet">Log Out</a></li><% } %>
        </ul>
        <% if (user != null) { %>
        <div class="navbar-user">Welcome, <strong><%= user.getUsername() %></strong>
            <% if (user.isAdmin()) { %><span class="admin-badge">Admin</span><% } %>
        </div>
        <% } %>
    </div>
</nav>

<div class="main-content">
    <div class="container">

        <h1 class="page-title">&#128226; Announcements</h1>

        <% if (announcements == null || announcements.isEmpty()) { %>
        <div class="empty-state"><p>No announcements yet.</p></div>
        <% } else { %>
        <% for (Announcement a : announcements) { %>
        <div class="announcement-card">
            <p><%= a.body() %></p>
            <small>Posted: <%= a.createdAt() %></small>
        </div>
        <% } %>
        <% } %>

        <div class="form-actions mt-2">
            <a href="index.jsp" class="btn btn-secondary">&#8592; Back to Home</a>
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

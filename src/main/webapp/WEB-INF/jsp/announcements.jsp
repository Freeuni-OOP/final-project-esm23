<%@ page import="java.util.List" %>
<%@ page import="com.quizwebsite.dao.AnnouncementDAO.Announcement" %>
<%@ page import="com.quizwebsite.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User user = (User) session.getAttribute("user");
    // get announcements from the servlet
    List<Announcement> announcements =
            (List<Announcement>) request.getAttribute("announcements");
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
            <% if (user != null) { %>
                <li><a href="LogoutServlet">Log Out</a></li>
            <% } else { %>
                <li><a href="login.jsp">Log In</a></li>
            <% } %>
        </ul>
        <% if (user != null) { %>
            <div class="navbar-user">Welcome, <strong><%= user.getUsername() %></strong></div>
        <% } %>
    </div>
</nav>

<div class="main-content">
    <div class="container">

        <h2 style="margin-bottom:1rem;">Announcements</h2>

        <% if (announcements == null || announcements.isEmpty()) { %>
            <div class="card"><p>No announcements yet.</p></div>
        <% } else {
               // display all announcements
               for (Announcement announcement : announcements) { %>
            <div class="announcement-card">
                <p><%= announcement.body() %></p>
                <small class="card-meta">Created at: <%= announcement.createdAt() %></small>
            </div>
        <%     }
           } %>

    </div>
</div>

<footer class="site-footer">
    <div class="container">&copy; 2025 Quiz Website</div>
</footer>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="js/main.js"></script>
</body>
</html>

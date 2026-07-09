<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.quizwebsite.model.Quiz" %>
<%@ page import="com.quizwebsite.model.User" %>
<%@ page import="com.quizwebsite.dao.AnnouncementDAO.Announcement" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User user = (User) session.getAttribute("user");

    List<Quiz> recentQuizzes  = (List<Quiz>) request.getAttribute("recentQuizzes");
    List<Quiz> popularQuizzes = (List<Quiz>) request.getAttribute("popularQuizzes");
    List<Announcement> announcements = (List<Announcement>) request.getAttribute("announcements");
    List<Quiz> myQuizzes      = (List<Quiz>) request.getAttribute("myQuizzes");
    Map<Long, String> friendNames    = (Map<Long, String>) request.getAttribute("friendNames");
    Integer pendingRequestCount      = (Integer) request.getAttribute("pendingRequestCount");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quiz Website</title>
    <link rel="stylesheet" href="css/main.css">
</head>
<body class="site-wrapper">

<%-- ==================== Navbar ==================== --%>
<nav class="navbar">
    <div class="container">
        <a class="navbar-brand" href="index.jsp">&#9670; Quiz Website</a>
        <span class="navbar-spacer"></span>
        <ul class="navbar-nav">
            <% if (user == null) { %>
            <li><a href="login.jsp">Log In</a></li>
            <li><a href="register.jsp">Register</a></li>
            <% } else { %>
            <li><a href="CreateQuizServlet">+ Create Quiz</a></li>
            <li><a href="announcements">Announcements</a></li>
            <% if (user.isAdmin()) { %>
            <li><a href="create-announcement">&#128226; Post</a></li>
            <% } %>
            <li><a href="LogoutServlet">Log Out</a></li>
            <% } %>
        </ul>
        <% if (user != null) { %>
        <div class="navbar-user">
            Welcome, <strong><%= user.getUsername() %></strong>
            <% if (user.isAdmin()) { %><span class="admin-badge">Admin</span><% } %>
            <% if (pendingRequestCount != null && pendingRequestCount > 0) { %>
            <span class="badge-pill" title="<%= pendingRequestCount %> pending friend request(s)"><%= pendingRequestCount %></span>
            <% } %>
        </div>
        <% } %>
    </div>
</nav>

<%-- ==================== Main content ==================== --%>
<div class="main-content">
    <div class="container">

        <% if (user == null) { %>
        <div class="card text-center mb-3" style="padding:2.5rem;">
            <h2 style="margin-bottom:.75rem;">Welcome to Quiz Website!</h2>
            <p class="text-muted mb-2">Create quizzes, challenge friends, and track your scores.</p>
            <div class="d-flex gap-1" style="justify-content:center;">
                <a href="login.jsp" class="btn btn-primary">Log In</a>
                <a href="register.jsp" class="btn btn-outline">Register</a>
            </div>
        </div>
        <% } %>

        <div class="home-grid">
            <%-- ---- Main column ---- --%>
            <div>

                <%-- Announcements --%>
                <h2 class="section-title">&#128226; Latest Announcements</h2>
                <% if (announcements == null || announcements.isEmpty()) { %>
                <div class="empty-state"><p>No announcements yet.</p></div>
                <% } else { %>
                <% for (Announcement a : announcements) { %>
                <div class="announcement-card">
                    <p><%= a.body() %></p>
                    <small><%= a.createdAt() %></small>
                </div>
                <% } %>
                <p class="text-small mt-1 mb-2"><a href="announcements">See all announcements &raquo;</a></p>
                <% } %>

                <hr class="divider">

                <%-- Recently Added Quizzes --%>
                <h2 class="section-title">&#128337; Recently Added</h2>
                <% if (recentQuizzes == null || recentQuizzes.isEmpty()) { %>
                <div class="empty-state">
                    <p>No quizzes have been created yet.</p>
                    <% if (user != null) { %>
                    <a href="CreateQuizServlet" class="btn btn-primary btn-sm">Create the first one!</a>
                    <% } %>
                </div>
                <% } else { %>
                <div class="quiz-grid">
                    <% for (Quiz quiz : recentQuizzes) { %>
                    <div class="card">
                        <div class="card-title"><%= quiz.getTitle() %></div>
                        <% if (quiz.getDescription() != null && !quiz.getDescription().isEmpty()) { %>
                        <p class="card-meta"><%= quiz.getDescription() %></p>
                        <% } %>
                        <div class="card-actions">
                            <a href="TakeQuizServlet?quizId=<%= quiz.getId() %>" class="btn btn-primary btn-sm">Take Quiz</a>
                            <% if (quiz.isPracticeEnabled()) { %>
                            <a href="TakeQuizServlet?quizId=<%= quiz.getId() %>&practice=true" class="btn btn-secondary btn-sm">Practice</a>
                            <% } %>
                        </div>
                    </div>
                    <% } %>
                </div>
                <% } %>

                <hr class="divider">

                <%-- Popular Quizzes --%>
                <h2 class="section-title">&#128293; Popular Quizzes</h2>
                <% if (popularQuizzes == null || popularQuizzes.isEmpty()) { %>
                <div class="empty-state"><p>No quizzes have been attempted yet.</p></div>
                <% } else { %>
                <div class="quiz-grid">
                    <% for (Quiz quiz : popularQuizzes) { %>
                    <div class="card">
                        <div class="card-title"><%= quiz.getTitle() %></div>
                        <div class="card-actions">
                            <a href="TakeQuizServlet?quizId=<%= quiz.getId() %>" class="btn btn-primary btn-sm">Take Quiz</a>
                        </div>
                    </div>
                    <% } %>
                </div>
                <% } %>

                <%-- User-specific: Your Quizzes --%>
                <% if (user != null) { %>
                <hr class="divider">
                <h2 class="section-title">&#128221; Your Quizzes</h2>
                <% if (myQuizzes == null || myQuizzes.isEmpty()) { %>
                <div class="empty-state">
                    <p>You haven't created any quizzes yet.</p>
                    <a href="CreateQuizServlet" class="btn btn-primary btn-sm">Create one now</a>
                </div>
                <% } else { %>
                <div class="quiz-grid">
                    <% for (Quiz quiz : myQuizzes) { %>
                    <div class="card">
                        <div class="card-title"><%= quiz.getTitle() %></div>
                        <div class="card-actions">
                            <a href="TakeQuizServlet?quizId=<%= quiz.getId() %>" class="btn btn-secondary btn-sm">Preview</a>
                            <a href="ReviewQuizServlet?quizId=<%= quiz.getId() %>" class="btn btn-outline btn-sm">Review</a>
                        </div>
                    </div>
                    <% } %>
                </div>
                <% } %>
                <% } %>

            </div>

            <%-- ---- Sidebar ---- --%>
            <% if (user != null) { %>
            <aside>
                <div class="sidebar-card">
                    <h3>&#128101; Friends</h3>
                    <% if (pendingRequestCount != null && pendingRequestCount > 0) { %>
                    <div class="alert alert-info" style="margin-bottom:.75rem;padding:.5rem .75rem;font-size:.82rem;">
                        <%= pendingRequestCount %> pending friend request<%= pendingRequestCount == 1 ? "" : "s" %>
                    </div>
                    <% } %>
                    <% if (friendNames == null || friendNames.isEmpty()) { %>
                    <p class="text-muted text-small">No friends added yet.</p>
                    <% } else { %>
                    <ul class="friends-list">
                        <% for (String friendName : friendNames.values()) { %>
                        <li><%= friendName %></li>
                        <% } %>
                    </ul>
                    <% } %>
                </div>
            </aside>
            <% } %>
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

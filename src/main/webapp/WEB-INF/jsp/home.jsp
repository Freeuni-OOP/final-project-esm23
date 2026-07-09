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
    List<Quiz> myQuizzes = (List<Quiz>) request.getAttribute("myQuizzes");
    Map<Long, String> friendNames = (Map<Long, String>) request.getAttribute("friendNames");
    Integer pendingRequestCount = (Integer) request.getAttribute("pendingRequestCount");


    String REMOVE_QUIZ_MSG = "Remove this quiz? This deletes all its questions and history.";
    String CLEAR_HISTORY_MSG = "Clear all attempt history for this quiz? The quiz itself is kept.";
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
                <li><a href="create-announcement">Post an announcement</a></li>
                <li><a href="AdminUsersServlet">Users</a></li>
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
                <% if (user != null && user.isAdmin()) { %>
                &nbsp;|&nbsp;
                <form method="post" action="AdminRemoveQuizServlet" style="display:inline;">
                    <input type="hidden" name="quizId" value="<%= quiz.getId() %>">
                    <button type="submit" onclick="return confirm('<%=REMOVE_QUIZ_MSG%>');">Remove quiz</button>
                </form>
                &nbsp;|&nbsp;
                <form method="post" action="AdminClearHistoryServlet" style="display:inline;">
                    <input type="hidden" name="quizId" value="<%= quiz.getId() %>">
                    <button type="submit" onclick="return confirm('<%=CLEAR_HISTORY_MSG%>');">Clear history</button>
                </form>
                <% } %>
            </div>
        <% } %>
    <% } %>

    <hr>

    <%-- ==================== Popular Quizzes ==================== --%>
    <h2>Popular Quizzes</h2>
    <% if (popularQuizzes == null || popularQuizzes.isEmpty()) { %>
        <p>No quizzes have been attempted yet.</p>
    <% } else { %>
        <% for (Quiz quiz : popularQuizzes) { %>
            <div style="margin-bottom:10px; padding:8px; border:1px solid #ccc;">
                <p><strong><%= quiz.getTitle() %></strong></p>
                <a href="TakeQuizServlet?quizId=<%= quiz.getId() %>">Take this quiz</a>
                <% if (user != null && user.isAdmin()) { %>
                &nbsp;|&nbsp;
                <form method="post" action="AdminRemoveQuizServlet" style="display:inline;">
                    <input type="hidden" name="quizId" value="<%= quiz.getId() %>">
                    <button type="submit" onclick="return confirm('<%=REMOVE_QUIZ_MSG%>');">Remove quiz</button>
                </form>
                &nbsp;|&nbsp;
                <form method="post" action="AdminClearHistoryServlet" style="display:inline;">
                    <input type="hidden" name="quizId" value="<%= quiz.getId() %>">
                    <button type="submit" onclick="return confirm('<%=CLEAR_HISTORY_MSG%>');">Clear history</button>
                </form>
                <% } %>
            </div>
        <% } %>
    <% } %>

    <%-- ==================== User-Specific Dashboard ==================== --%>
    <% if (user != null) { %>
        <hr>

        <%-- Your Quizzes --%>
        <h2>Your Quizzes</h2>
        <% if (myQuizzes == null || myQuizzes.isEmpty()) { %>
            <p>You haven't created any quizzes yet. <a href="CreateQuizServlet">Create one now</a>.</p>
        <% } else { %>
            <% for (Quiz quiz : myQuizzes) { %>
                <div style="margin-bottom:10px; padding:8px; border:1px solid #ccc;">
                    <p><strong><%= quiz.getTitle() %></strong></p>
                    <a href="TakeQuizServlet?quizId=<%= quiz.getId() %>">Preview / take</a>

                    <form method="post" action="AdminRemoveQuizServlet" style="display:inline;">
                        <input type="hidden" name="quizId" value="<%= quiz.getId() %>">
                        <button type="submit" onclick="return confirm('<%=REMOVE_QUIZ_MSG%>');">Remove quiz</button>
                    </form>
                    &nbsp;|&nbsp;
                    <form method="post" action="AdminClearHistoryServlet" style="display:inline;">
                        <input type="hidden" name="quizId" value="<%= quiz.getId() %>">
                        <button type="submit" onclick="return confirm('<%=CLEAR_HISTORY_MSG%>');">Clear history</button>
                    </form>
                </div>
            </aside>
            <% } %>
        </div>

<hr>

<footer class="site-footer">
    <div class="container">&copy; 2025 Quiz Website</div>
</footer>

    <%-- Admin stats panel--%>
    <% if (user != null && user.isAdmin()) { %>
        <a href="AdminStatsServlet">View site statistics</a>
        <br/>
    <% } %>

</body>
</html>

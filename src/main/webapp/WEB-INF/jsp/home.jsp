<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.quizwebsite.model.Quiz" %>
<%@ page import="com.quizwebsite.model.User" %>
<%@ page import="com.quizwebsite.dao.AnnouncementDAO.Announcement" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User user = (User) session.getAttribute("user");

    List<Quiz> recentQuizzes = (List<Quiz>) request.getAttribute("recentQuizzes");
    List<Quiz> popularQuizzes = (List<Quiz>) request.getAttribute("popularQuizzes");
    List<Announcement> announcements = (List<Announcement>) request.getAttribute("announcements");
    List<Quiz> myQuizzes = (List<Quiz>) request.getAttribute("myQuizzes");
    Map<Long, String> friendNames = (Map<Long, String>) request.getAttribute("friendNames");
    Integer pendingRequestCount = (Integer) request.getAttribute("pendingRequestCount");
%>

<!DOCTYPE html>
<html>
<head>
    <title>Quiz Website</title>
</head>
<body>

    <h1>Welcome to the Quiz Website</h1>

    <%-- ==================== Navigation / Header ==================== --%>
    <% if (user == null) { %>
        <p>
            <a href="login.jsp">Log in</a> or <a href="register.jsp">register</a> to create quizzes, track scores, and add friends.
        </p>
    <% } else { %>
        <p>Welcome back, <strong><%= user.getUsername() %></strong>!</p>
        <ul>
            <li><a href="CreateQuizServlet">Create a new quiz</a></li>
            <li><a href="announcements">View all announcements</a></li>
            <% if (user.isAdmin()) { %>
                <li><a href="create-announcement">Post an announcement</a></li>
            <% } %>
            <li><a href="LogoutServlet">Log out</a></li>
            <li><a href="FriendsServlet">Friends<% if (pendingRequestCount != null && pendingRequestCount > 0) { %> (<%= pendingRequestCount %> pending)<% } %></a></li>
        </ul>
    <% } %>

    <hr>

    <%-- ==================== Announcements Teaser ==================== --%>
    <h2>Latest Announcements</h2>
    <% if (announcements == null || announcements.isEmpty()) { %>
        <p>No announcements yet.</p>
    <% } else { %>
        <% for (Announcement announcement : announcements) { %>
            <div style="margin-bottom:10px; padding:8px; border:1px solid #ccc;">
                <p><%= announcement.body() %></p>
                <small>Posted: <%= announcement.createdAt() %></small>
            </div>
        <% } %>
        <p><a href="announcements">See all announcements &raquo;</a></p>
    <% } %>

    <hr>

    <%-- ==================== Recently Added Quizzes ==================== --%>
    <h2>Recently Added Quizzes</h2>
    <% if (recentQuizzes == null || recentQuizzes.isEmpty()) { %>
        <p>
            No quizzes have been created yet.
            <% if (user != null) { %>
                <a href="CreateQuizServlet">Create the first one!</a>
            <% } %>
        </p>
    <% } else { %>
        <% for (Quiz quiz : recentQuizzes) { %>
            <div style="margin-bottom:10px; padding:8px; border:1px solid #ccc;">
                <p><strong><%= quiz.getTitle() %></strong></p>
                <p><%= quiz.getDescription() == null ? "" : quiz.getDescription() %></p>
                <a href="TakeQuizServlet?quizId=<%= quiz.getId() %>">Take this quiz</a>
                <% if (quiz.isPracticeEnabled()) { %>
                    &nbsp;|&nbsp;
                    <a href="TakeQuizServlet?quizId=<%= quiz.getId() %>&practice=true">Practice mode</a>
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
                </div>
            <% } %>
        <% } %>

        <hr>

        <%-- Friends Feed --%>
        <h2>Friends</h2>
        <% if (pendingRequestCount != null && pendingRequestCount > 0) { %>
            <p>You have <%= pendingRequestCount %> pending friend request<%= pendingRequestCount == 1 ? "" : "s" %>.</p>
        <% } %>

        <% if (friendNames == null || friendNames.isEmpty()) { %>
            <p>You don't have any friends added yet.</p>
        <% } else { %>
            <ul>
                <% for (String friendName : friendNames.values()) { %>
                    <li><%= friendName %></li>
                <% } %>
            </ul>
        <% } %>
    <% } %>

</body>
</html>
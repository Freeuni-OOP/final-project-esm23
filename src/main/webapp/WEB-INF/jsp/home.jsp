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
    String REMOVE_USER_MSG = "Remove this user and all their data? This cannot be undone";
    String REMOVE_QUIZ_MSG = "Remove this quiz? This deletes all its questions and history.";
    String CLEAR_HISTORY_MSG = "Clear all attempt history for this quiz? The quiz itself is kept.";
    String PROMOTE_USER_MSG = "Grant admin rights to this user?";
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
                <li>
                    <form method="post" action="AdminRemoveUserServlet" style="display:inline;">
                        <input type="text" name="username" placeholder="username to remove" required>
                        <button type="submit" onclick="return confirm('<%=REMOVE_USER_MSG%>');">Remove user</button>
                    </form>
                </li>
                <li>
                    <form method="post" action="AdminPromoteUserServlet" style="display:inline;">
                        <input type="text" name="username" placeholder="username to promote" required>
                        <button type="submit" onclick="return confirm('<%=PROMOTE_USER_MSG%>');">Promote to Admin</button>
                    </form>
                </li>
            <% } %>
            <li><a href="LogoutServlet">Log out</a></li>
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

    <%-- Admin stats panel--%>
    <% if (user != null && user.isAdmin()) { %>
        <a href="AdminStatsServlet">View site statistics</a>
        <br/>
    <% } %>

</body>
</html>
<%@ page import="java.util.List" %>
<%@ page import="com.quizwebsite.dao.UserDAO.UserStats" %>
<%@ page contentType="text/html;charset=UTF-8"%>

<%
    List<UserStats> topCreators = (List<UserStats>) request.getAttribute("topCreators");
    List<UserStats> topTakers = (List<UserStats>) request.getAttribute("topTakers");
%>

<html>
<head>
    <title>Site Statistics</title>
</head>
<body>
<h1>Site Statistics</h1>

<ul>
    <li>Total users: <%= request.getAttribute("userCount") %></li>
    <li>Total quizzes: <%= request.getAttribute("quizCount") %></li>
    <li>Total quizzes taken: <%= request.getAttribute("attemptCount") %></li>
</ul>

<hr>

<h2>Most Active Users</h2>

<h3>Most Quizzes Added</h3>
<% if (topCreators == null || topCreators.isEmpty()) { %>
<p>No quizzes have been created yet.</p>
<% } else { %>
<ol>
    <% for (UserStats u : topCreators) { %>
    <li><a href="UserProfileServlet?id=<%= u.id() %>"><%= u.username() %></a>: <%= u.quizzesCreated() %> quizzes created</li>
    <% } %>
</ol>
<% } %>

<h3>Most Quizzes Taken</h3>
<% if (topTakers == null || topTakers.isEmpty()) { %>
<p>No quizzes have been taken yet.</p>
<% } else { %>
<ol>
    <% for (UserStats u : topTakers) { %>
    <li><a href="UserProfileServlet?id=<%= u.id() %>"><%= u.username() %></a>: <%= u.quizzesTaken() %> quizzes taken</li>
    <% } %>
</ol>
<% } %>

<p><a href="HomeServlet">Back to home</a></p>
</body>
</html>
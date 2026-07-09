<%@ page import="java.util.List" %>
<%@ page import="com.quizwebsite.dao.UserDAO.UserStats" %>
<%@ page import="com.quizwebsite.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<%-- Lets admins search users and promote them to admin --%>

<%
    String PROMOTE_USER_MSG = "Grant admin rights to this user?";
    String REMOVE_USER_MSG = "Remove this user and all their data? This cannot be undone";

    List<UserStats> users = (List<UserStats>) request.getAttribute("users");
    List<UserStats> admins = (List<UserStats>) request.getAttribute("admins");
    String searchQuery = (String) request.getAttribute("searchQuery");
    if (searchQuery == null) {
        searchQuery = "";
    }

    User currentUser = (User) session.getAttribute("user");
    String currentUsername = currentUser == null ? "" : currentUser.getUsername();

    // escape so a username/search term can't inject HTML
    String searchQueryEscaped = searchQuery
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
%>

<!DOCTYPE html>
<html>
<head>
    <title>Manage Users</title>
</head>
<body>

<h1>Manage Users</h1>
<p><a href="HomeServlet">Back to home</a> | <a href="AdminStatsServlet">Site statistics</a></p>

<hr>

<form method="get" action="AdminUsersServlet">
    <input type="text" name="q" placeholder="Search by username" value="<%= searchQueryEscaped %>">
    <button type="submit">Search</button>
</form>

<hr>

<h2>Users</h2>

<% if (users == null || users.isEmpty()) { %>
<p>No users found.</p>
<% } else { %>
<table border="1" cellpadding="6">
    <tr>
        <th>Username</th>
        <th>Member since</th>
        <th>Quizzes added</th>
        <th>Quizzes taken</th>
        <th>Role</th>
        <th>Actions</th>
    </tr>
    <% for (UserStats u : users) { %>
    <tr>
        <td><a href="UserProfileServlet?id=<%= u.id() %>"><%= u.username() %></a></td>
        <td><%= u.createdAt() %></td>
        <td><%= u.quizzesCreated() %></td>
        <td><%= u.quizzesTaken() %></td>
        <td><%= u.isAdmin() ? "Admin" : "User" %></td>
        <td>
            <% if (!u.isAdmin()) { %>
            <form method="post" action="AdminPromoteUserServlet" style="display:inline;">
                <input type="hidden" name="username" value="<%= u.username() %>">
                <button type="submit" onclick="return confirm('<%=PROMOTE_USER_MSG%>');">Promote to Admin</button>
            </form>
            <form method="post" action="AdminRemoveUserServlet" style="display:inline;">
                <input type="hidden" name="username" value="<%= u.username() %>">
                <button type="submit" onclick="return confirm('<%=REMOVE_USER_MSG%>');">Remove user</button>
            </form>
            <% } %>
        </td>
    </tr>
    <% } %>
</table>
<% } %>

<hr>

<h2>Admins</h2>

<% if (admins == null || admins.isEmpty()) { %>
<p>No admins found.</p>
<% } else { %>
<table border="1" cellpadding="6">
    <tr>
        <th>Username</th>
        <th>Member since</th>
        <th>Quizzes added</th>
        <th>Quizzes taken</th>
        <th>Role</th>
        <th>Actions</th>
    </tr>
    <% for (UserStats u : admins) { %>
    <tr>
        <td>
            <a href="UserProfileServlet?id=<%= u.id() %>"><%= u.username() %></a>
            <% if (u.username().equals(currentUsername)) { %> (you) <% } %>
        </td>
        <td><%= u.createdAt() %></td>
        <td><%= u.quizzesCreated() %></td>
        <td><%= u.quizzesTaken() %></td>
        <td>Admin</td>
        <td>
            <% if (!u.username().equals(currentUsername)) { %>
            <form method="post" action="AdminRemoveUserServlet" style="display:inline;">
                <input type="hidden" name="username" value="<%= u.username() %>">
                <button type="submit" onclick="return confirm('<%=REMOVE_USER_MSG%>');">Remove user</button>
            </form>
            <% } %>
        </td>
    </tr>
    <% } %>
</table>
<% } %>

</body>
</html>

<%@ page import="java.util.List" %>
<%@ page import="com.quizwebsite.dao.UserDAO.UserStats" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<%-- Lets admins search users and promote them to admin --%>

<%
    List<UserStats> users = (List<UserStats>) request.getAttribute("users");
    String searchQuery = (String) request.getAttribute("searchQuery");
    if (searchQuery == null) {
        searchQuery = "";
    }

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
                <button type="submit" onclick="return confirm('Grant admin rights to this user?');">Promote to Admin</button>
            </form>
            <% } %>
        </td>
    </tr>
    <% } %>
</table>
<% } %>

</body>
</html>

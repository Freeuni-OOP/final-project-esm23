<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.quizwebsite.servlet.FriendsServlet.FriendView" %>

<%!
    String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>

<%
    List<FriendView> friends =
            (List<FriendView>) request.getAttribute("friends");

    List<FriendView> pendingRequests =
            (List<FriendView>) request.getAttribute("pendingRequests");

    List<FriendView> searchResults =
            (List<FriendView>) request.getAttribute("searchResults");

    String error = (String) request.getAttribute("error");

    String contextPath = request.getContextPath();
    String currentQuery = request.getParameter("q");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Friends</title>
</head>
<body>

<h1>Friends</h1>

<p>
    <a href="<%= contextPath %>/HomeServlet">Home</a>
</p>

<% if (error != null && !error.trim().isEmpty()) { %>
    <p style="color: red;"><%= esc(error) %></p>
<% } %>

<hr>

<h2>Pending requests</h2>

<% if (pendingRequests == null || pendingRequests.isEmpty()) { %>
    <p>No pending friend requests.</p>
<% } else { %>
    <ul>
        <% for (FriendView requestUser : pendingRequests) { %>
            <li>
                <%= esc(requestUser.username()) %>

                <form method="post" action="<%= contextPath %>/RespondFriendRequestServlet" style="display: inline;">
                    <input type="hidden" name="requesterId" value="<%= requestUser.id() %>">
                    <input type="hidden" name="action" value="accept">
                    <button type="submit">Accept</button>
                </form>

                <form method="post" action="<%= contextPath %>/RespondFriendRequestServlet" style="display: inline;">
                    <input type="hidden" name="requesterId" value="<%= requestUser.id() %>">
                    <input type="hidden" name="action" value="reject">
                    <button type="submit">Reject</button>
                </form>
            </li>
        <% } %>
    </ul>
<% } %>

<hr>

<h2>Your friends</h2>

<% if (friends == null || friends.isEmpty()) { %>
    <p>You have no friends yet.</p>
<% } else { %>
    <ul>
        <% for (FriendView friend : friends) { %>
            <li>
                <%= esc(friend.username()) %>

                <form method="post" action="<%= contextPath %>/RespondFriendRequestServlet" style="display: inline;">
                    <input type="hidden" name="requesterId" value="<%= friend.id() %>">
                    <input type="hidden" name="action" value="remove">
                    <button type="submit">Remove</button>
                </form>
            </li>
        <% } %>
    </ul>
<% } %>

<hr>

<h2>Add a friend</h2>

<form method="post" action="<%= contextPath %>/SendFriendRequestServlet">
    <label for="username">Username:</label>
    <input type="text" id="username" name="username">
    <button type="submit">Send request</button>
</form>

<hr>

<h2>Search users</h2>

<form method="get" action="<%= contextPath %>/FriendsServlet">
    <label for="q">Search by username:</label>
    <input type="text" id="q" name="q" value="<%= esc(currentQuery) %>">
    <button type="submit">Search</button>
</form>

<% if (searchResults != null) { %>
    <h3>Search results</h3>

    <% if (searchResults.isEmpty()) { %>
        <p>No users found.</p>
    <% } else { %>
        <ul>
            <% for (FriendView result : searchResults) { %>
                <li>
                    <%= esc(result.username()) %>

                    <form method="post" action="<%= contextPath %>/SendFriendRequestServlet" style="display: inline;">
                        <input type="hidden" name="username" value="<%= esc(result.username()) %>">
                        <button type="submit">Add friend</button>
                    </form>
                </li>
            <% } %>
        </ul>
    <% } %>
<% } %>

</body>
</html>

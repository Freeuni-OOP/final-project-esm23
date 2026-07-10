<%@ page import="java.util.List" %>
<%@ page import="com.quizwebsite.servlet.FriendServlet.FriendView" %>

<%!
    @SuppressWarnings("unchecked")
    private List<FriendView> asFriendViewList(Object attr) {
        return (List<FriendView>) attr;
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>

<%
    List<FriendView> friends = asFriendViewList(request.getAttribute("friends"));
    List<FriendView> pendingRequests = asFriendViewList(request.getAttribute("pendingRequests"));
    List<FriendView> searchResults = asFriendViewList(request.getAttribute("searchResults"));

    String error = (String) request.getAttribute("error");
    String contextPath = request.getContextPath();
    String qParam = request.getParameter("q");
%>

<!DOCTYPE html>
<html>
<head>
    <title>Friends</title>
</head>
<body>

<h1>Friends</h1>

<p><a href="<%= contextPath %>/HomeServlet">Home</a></p>

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

        <form action="<%= contextPath %>/RespondFriendRequestServlet" method="post" style="display: inline;">
            <input type="hidden" name="requesterId" value="<%= requestUser.id() %>">
            <input type="hidden" name="action" value="accept">
            <button type="submit">Accept</button>
        </form>

        <form action="<%= contextPath %>/RespondFriendRequestServlet" method="post" style="display: inline;">
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

        <form action="<%= contextPath %>/RespondFriendRequestServlet" method="post" style="display: inline;">
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

<form action="<%= contextPath %>/SendFriendRequestServlet" method="post">
    <label for="username">Username:</label>
    <input type="text" id="username" name="username">
    <button type="submit">Send request</button>
</form>

<hr>

<h2>Search users</h2>

<form action="<%= contextPath %>/FriendsServlet" method="get">
    <label for="q">Search by username:</label>
    <input type="text" id="q" name="q" value="<%= esc(qParam != null ? qParam : "") %>">
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

        <form action="<%= contextPath %>/SendFriendRequestServlet" method="post" style="display: inline;">
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
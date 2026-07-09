<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.quizwebsite.servlet.FriendsServlet.FriendView" %>

<%!
    // Quick helper to escape HTML special characters and prevent XSS
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
    // Pull in the three lists of friends from request attributes
    List<FriendView> friends =
            (List<FriendView>) request.getAttribute("friends");

    List<FriendView> pendingRequests =
            (List<FriendView>) request.getAttribute("pendingRequests");

    List<FriendView> searchResults =
            (List<FriendView>) request.getAttribute("searchResults");

    // If an error occurred (from SendFriendRequestServlet), display it
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

<!-- Display any error messages from a failed friend request -->
<% if (error != null && !error.trim().isEmpty()) { %>
    <p style="color: red;"><%= esc(error) %></p>
<% } %>

<hr>

<!-- ==================== SECTION 1: Incoming Friend Requests ==================== -->
<h2>Pending requests</h2>

<% if (pendingRequests == null || pendingRequests.isEmpty()) { %>
    <p>No pending friend requests.</p>
<% } else { %>
    <ul>
        <% for (FriendView requestUser : pendingRequests) { %>
            <li>
                <%= esc(requestUser.username()) %>

                <!-- Accept button: POST to RespondFriendRequestServlet with action=accept -->
                <form method="post" action="<%= contextPath %>/RespondFriendRequestServlet" style="display: inline;">
                    <input type="hidden" name="requesterId" value="<%= requestUser.id() %>">
                    <input type="hidden" name="action" value="accept">
                    <button type="submit">Accept</button>
                </form>

                <!-- Reject button: POST to RespondFriendRequestServlet with action=reject -->
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

<!-- ==================== SECTION 2: Your Friends ==================== -->
<h2>Your friends</h2>

<% if (friends == null || friends.isEmpty()) { %>
    <p>You have no friends yet.</p>
<% } else { %>
    <ul>
        <% for (FriendView friend : friends) { %>
            <li>
                <%= esc(friend.username()) %>

                <!-- Remove friend button: POST to RespondFriendRequestServlet with action=remove -->
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

<!-- ==================== SECTION 3: Send a Friend Request ==================== -->
<h2>Add a friend</h2>

<!-- Simple form to enter a username and send a friend request -->
<form method="post" action="<%= contextPath %>/SendFriendRequestServlet">
    <label for="username">Username:</label>
    <input type="text" id="username" name="username">
    <button type="submit">Send request</button>
</form>

<hr>

<!-- ==================== SECTION 4: Search for Users ==================== -->
<h2>Search users</h2>

<!-- GET form to search by username (q parameter) -->
<form method="get" action="<%= contextPath %>/FriendsServlet">
    <label for="q">Search by username:</label>
    <input type="text" id="q" name="q" value="<%= esc(currentQuery) %>">
    <button type="submit">Search</button>
</form>

<!-- Display search results if a search was performed -->
<% if (searchResults != null) { %>
    <h3>Search results</h3>

    <% if (searchResults.isEmpty()) { %>
        <p>No users found.</p>
    <% } else { %>
        <ul>
            <% for (FriendView result : searchResults) { %>
                <li>
                    <%= esc(result.username()) %>

                    <!-- Quick-add button to send a friend request directly from search results -->
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

<%@ page import="java.util.List" %>
<%@ page import="com.quizwebsite.servlet.FriendRequestsServlet.PendingRequest" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<PendingRequest> pendingRequests = (List<PendingRequest>) request.getAttribute("pendingRequests");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Friend Requests</title>
</head>
<body>
<h1>Pending Friend Requests</h1>

<% if (pendingRequests == null || pendingRequests.isEmpty()) { %>
    <p>You have no pending friend requests.</p>
<% } else { %>
    <ul>
        <% for (PendingRequest r : pendingRequests) { %>
            <li style="margin-bottom:8px;">
                <a href="UserProfileServlet?id=<%= r.requesterId() %>"><%= r.requesterUsername() %></a>
                wants to be your friend.
                <form method="post" action="RespondFriendRequestServlet" style="display:inline;">
                    <input type="hidden" name="requesterId" value="<%= r.requesterId() %>">
                    <input type="hidden" name="action" value="accept">
                    <button type="submit">Accept</button>
                </form>
                <form method="post" action="RespondFriendRequestServlet" style="display:inline;">
                    <input type="hidden" name="requesterId" value="<%= r.requesterId() %>">
                    <input type="hidden" name="action" value="decline">
                    <button type="submit">Decline</button>
                </form>
            </li>
        <% } %>
    </ul>
<% } %>

<br>
<a href="FindFriendsServlet">Find Friends</a> &nbsp;|&nbsp;
<a href="HomeServlet">Back to Home</a>

</body>
</html>

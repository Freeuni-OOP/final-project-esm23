<%@ page import="com.quizwebsite.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User profileUser = (User) request.getAttribute("profileUser");
    Integer friendCount = (Integer) request.getAttribute("friendCount");
    Integer createdQuizCount = (Integer) request.getAttribute("createdQuizCount");
    Integer takenQuizCount = (Integer) request.getAttribute("takenQuizCount");
    Boolean isOwnProfile = (Boolean) request.getAttribute("isOwnProfile");
    String friendshipStatus = (String) request.getAttribute("friendshipStatus");
    String backUrl = "UserProfileServlet?id=" + (profileUser != null ? profileUser.getId() : "");
%>
<!DOCTYPE html>
<html>
<head>
    <title>User Profile</title>
</head>

<body>
<h1><%= profileUser.getUsername() %></h1>
<hr>
<h3>Statistics</h3>
<p>Friends: <%= friendCount %></p>
<p>Quizzes Created: <%= createdQuizCount %></p>
<p>Quizzes Taken: <%= takenQuizCount %></p>

<% if (!isOwnProfile && friendshipStatus != null) {
    if ("NONE".equals(friendshipStatus)) { %>
        <form method="post" action="SendFriendRequestServlet" style="display:inline;">
            <input type="hidden" name="friendId" value="<%= profileUser.getId() %>">
            <input type="hidden" name="redirect" value="<%= backUrl %>">
            <button type="submit">Add Friend</button>
        </form>
<%  } else if ("PENDING_SENT".equals(friendshipStatus)) { %>
        <button type="button" disabled>Friend Request Sent</button>
<%  } else if ("PENDING_RECEIVED".equals(friendshipStatus)) { %>
        <p><%= profileUser.getUsername() %> sent you a friend request.
            <a href="FriendRequestsServlet">Respond to it</a></p>
<%  } else if ("FRIENDS".equals(friendshipStatus)) { %>
        <form method="post" action="RemoveFriendServlet" style="display:inline;">
            <input type="hidden" name="friendId" value="<%= profileUser.getId() %>">
            <input type="hidden" name="redirect" value="<%= backUrl %>">
            <button type="submit" onclick="return confirm('Remove this friend?');">Remove Friend</button>
        </form>
<%  } %>
<% } %>
<% if (isOwnProfile) { %>
    <a href="FindFriendsServlet">Find Friends</a> &nbsp;|&nbsp;
    <a href="FriendRequestsServlet">View Friend Requests</a>
<% } %>
<br><br>
<a href="HomeServlet">Back to Home</a>

</body>
</html>
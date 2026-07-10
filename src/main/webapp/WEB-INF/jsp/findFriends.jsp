<%@ page import="java.util.List" %>
<%@ page import="com.quizwebsite.servlet.FindFriendsServlet.SearchResult" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String query = (String) request.getAttribute("query");
    List<SearchResult> results = (List<SearchResult>) request.getAttribute("results");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Find Friends</title>
</head>
<body>
<h1>Find Friends</h1>

<form method="get" action="FindFriendsServlet">
    <input type="text" name="q" placeholder="Search by username"
           value="<%= query == null ? "" : query %>">
    <button type="submit">Search</button>
</form>

<br>

<% if (query != null && !query.trim().isEmpty()) { %>
    <% if (results == null || results.isEmpty()) { %>
        <p>No users found matching "<%= query %>".</p>
    <% } else { %>
        <ul>
            <% for (SearchResult r : results) { %>
                <li style="margin-bottom:8px;">
                    <a href="UserProfileServlet?id=<%= r.userId() %>"><%= r.username() %></a>
                    &nbsp;
                    <% if ("NONE".equals(r.status())) { %>
                        <form method="post" action="SendFriendRequestServlet" style="display:inline;">
                            <input type="hidden" name="friendId" value="<%= r.userId() %>">
                            <input type="hidden" name="redirect" value="FindFriendsServlet?q=<%= query %>">
                            <button type="submit">Add Friend</button>
                        </form>
                    <% } else if ("PENDING_SENT".equals(r.status())) { %>
                        <em>Friend request sent</em>
                    <% } else if ("PENDING_RECEIVED".equals(r.status())) { %>
                        <em><a href="FriendRequestsServlet">Respond to their request</a></em>
                    <% } else if ("FRIENDS".equals(r.status())) { %>
                        <em>Already friends</em>
                    <% } %>
                </li>
            <% } %>
        </ul>
    <% } %>
<% } %>

<br>
<a href="HomeServlet">Back to Home</a>

</body>
</html>

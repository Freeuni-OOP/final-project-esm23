<%@ page import="java.util.List" %>
<%@ page import="com.quizwebsite.dao.AnnouncementDAO.Announcement" %>
<%@ page import="com.quizwebsite.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User user = (User) session.getAttribute("user");
%>

<html>
<head>
    <title>Announcements</title>
</head>
<body>
<h1>Announcements</h1>

<%
    // get announcements from the servlet
    List<Announcement> announcements =
            (List<Announcement>) request.getAttribute("announcements");

    if (announcements == null || announcements.isEmpty()) {
%>
        <p>No announcements yet.</p>
<%
    } else {
        // display all announcements
        for (Announcement announcement : announcements) {
%>
        <div>
            <p><%= announcement.body() %></p>
            <small>Created at: <%= announcement.createdAt() %></small>
            <% if (user != null && user.isAdmin()) { %>
            <form method="post" action="AdminDeleteAnnouncementServlet" style="display:inline;">
                <input type="hidden" name="id" value="<%= announcement.id() %>">
                <button type="submit" onclick="return confirm('Delete this announcement?');">Delete</button>
            </form>
            <% } %>
        </div>
        <hr>
<%
        }
    }
%>
</body>
</html>
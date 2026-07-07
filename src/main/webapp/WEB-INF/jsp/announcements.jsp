<%@ page import="java.util.List" %>
<%@ page import="com.quizwebsite.dao.AnnouncementDAO.Announcement" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

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
        </div>
        <hr>
<%
        }
    }
%>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Create Announcement</title>
</head>
<body>
<h1>Create Announcement</h1>

<%
    String error = (String) request.getAttribute("error");
    if (error != null) {
%>
<p style="color:red;"><%= error %></p>
<%
    }
%>

<form method="post" action="<%= request.getContextPath() %>/create-announcement">
    <label>Announcement:</label>
    <br><br>
    <textarea name="body" rows="5" cols="50"></textarea>
    <br><br>
    <button type="submit">Save</button>
</form>
<br>
<a href="<%= request.getContextPath() %>/announcements">
    Back to announcements
</a>

</body>
</html>
<%@ page import="com.quizwebsite.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User profileUser = (User) request.getAttribute("profileUser");
    Integer friendCount = (Integer) request.getAttribute("friendCount");
    Integer createdQuizCount = (Integer) request.getAttribute("createdQuizCount");
    Integer takenQuizCount = (Integer) request.getAttribute("takenQuizCount");
    Boolean isOwnProfile = (Boolean) request.getAttribute("isOwnProfile");
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

<% if (!isOwnProfile) { %>
    <button>Add Friend</button>
<% } %>
<br><br>
<a href="HomeServlet">Back to Home</a>

</body>
</html>
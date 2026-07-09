<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Site Statistics</title>
</head>
<body>
<h1>Site Statistics</h1>

<ul>
    <li>Total users: <%= request.getAttribute("userCount") %></li>
    <li>Total quizzes: <%= request.getAttribute("quizCount") %></li>
    <li>Total quizzes taken: <%= request.getAttribute("attemptCount") %></li>
</ul>

<p><a href="HomeServlet">Back to home</a></p>
</body>
</html>
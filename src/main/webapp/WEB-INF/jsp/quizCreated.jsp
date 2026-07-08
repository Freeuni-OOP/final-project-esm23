<%@ page contentType="text/html;charset=UTF-8" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    String quizId = request.getParameter("quizId");
%>
<!DOCTYPE html>
<html>
<head><title>Quiz Created!</title></head>
<body>
    <h1>Quiz Created Successfully!</h1>
    <p>Your quiz has been saved.</p>
    <% if (quizId != null) { %>
        <a href="ViewQuizServlet?quizId=<%= quizId %>">View Quiz</a> |
    <% } %>
    <a href="index.jsp">Go to Home</a>
</body>
</html>
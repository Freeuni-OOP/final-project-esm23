<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.quizwebsite.model.*" %>
<%@ page import="java.util.List" %>
<%
    Quiz quiz = (Quiz) request.getAttribute("quiz");
    List<Question> questions = (List<Question>) request.getAttribute("questions");

    //safety check to redirect if accessed outside servlet flow
    if (quiz == null) {
        response.sendRedirect("index.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head><title><%= quiz.getTitle() %></title></head>
<body>
<h1><%= quiz.getTitle() %></h1>
<p><%= quiz.getDescription() %></p>

<h3>Questions (<%= questions.size() %>)</h3>

<% for (Question q : questions) { %>
<div style="margin-bottom:15px;">
    <p><strong><%= q.getQuestionText() %></strong></p>
    <% if (q instanceof MultipleChoice mc) { %>
    <ul>
        <% for (QuestionOption opt : mc.getOptions()) { %>
        <li<%= opt.isCorrect() ? " style=\"color:green;font-weight:bold;\"" : "" %>>
            <%= opt.getOptionText() %><%= opt.isCorrect() ? " (correct)" : "" %>
        </li>
        <% } %>
    </ul>
    <% } else if (q.getType() == QuestionType.PICTURE_RESPONSE) {  %>
        <img src="<%= q.getImageUrl() %>" alt="question image">
        <input type="text" name="q<%=q.getId() %>">
    <% } else { %>
        <input type="text" name="q<%= q.getId() %>">
    <% } %>
</div>
<% } %>

<p><a href="index.jsp">Back to Home</a></p>
</body>
</html>

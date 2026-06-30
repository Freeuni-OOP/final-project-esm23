<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.quizwebsite.model.*" %>
<%@ page import="java.util.List" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    Quiz quiz = (Quiz) request.getAttribute("quiz");
    List<Question> questions = (List<Question>) request.getAttribute("questions");
%>
<!DOCTYPE html>
<html>
<head>
    <title><%= quiz.getTitle() %></title>
</head>
<body>
<h1><%= quiz.getTitle() %></h1>
<% if (quiz.getDescription() != null && !quiz.getDescription().isEmpty()) { %>
    <p><%= quiz.getDescription() %></p>
<% } %>
<% if (Boolean.TRUE.equals(session.getAttribute("takeQuiz_isPractice"))) { %>
    <p><em>Practice mode &dash; this attempt will not be scored or saved.</em></p>
<% } %>

<form method="post" action="TakeQuizServlet">
<% for (Question q : questions) { %>
    <div style="margin-bottom:20px; padding:10px; border:1px solid #ccc;">
        <p><strong>Q<%= q.getPosition() %>.</strong> <%= q.getQuestionText() %></p>

        <% if (q.getImageUrl() != null && !q.getImageUrl().isEmpty()) { %>
            <p><img src="<%= q.getImageUrl() %>" alt="question image" style="max-width:300px;"></p>
        <% } %>

        <% if (q instanceof MultipleChoice) {
               MultipleChoice mc = (MultipleChoice) q;
               for (QuestionOption opt : mc.getOptions()) { %>
                <label>
                    <input type="radio" name="q_<%= q.getId() %>" value="<%= opt.getOptionText() %>">
                    <%= opt.getOptionText() %>
                </label><br>
        <%     }
           } else { %>
            <input type="text" name="q_<%= q.getId() %>" size="40">
        <% } %>
    </div>
<% } %>
    <button type="submit">Submit Quiz</button>
</form>
</body>
</html>

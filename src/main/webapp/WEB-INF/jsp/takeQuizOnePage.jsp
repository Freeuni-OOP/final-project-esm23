<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.quizwebsite.model.*" %>
<%@ page import="java.util.List" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    User user = (User) session.getAttribute("user");
    Quiz quiz = (Quiz) request.getAttribute("quiz");
    List<Question> questions = (List<Question>) request.getAttribute("questions");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= quiz.getTitle() %></title>
    <link rel="stylesheet" href="css/main.css">
</head>
<body class="site-wrapper">

<nav class="navbar">
    <div class="container">
        <a class="navbar-brand" href="index.jsp">&#9670; Quiz Website</a>
        <span class="navbar-spacer"></span>
        <ul class="navbar-nav">
            <li><a href="index.jsp">Home</a></li>
            <li><a href="LogoutServlet">Log Out</a></li>
        </ul>
        <div class="navbar-user">Welcome, <strong><%= user.getUsername() %></strong></div>
    </div>
</nav>

<div class="main-content">
    <div class="container">

        <div class="quiz-header">
            <h1><%= quiz.getTitle() %></h1>
            <% if (quiz.getDescription() != null && !quiz.getDescription().isEmpty()) { %>
                <p><%= quiz.getDescription() %></p>
            <% } %>
            <% if (Boolean.TRUE.equals(session.getAttribute("takeQuiz_isPractice"))) { %>
                <p class="quiz-progress"><em>Practice mode &mdash; this attempt will not be scored or saved.</em></p>
            <% } %>
        </div>

        <form method="post" action="TakeQuizServlet">
        <% for (Question q : questions) { %>
            <div class="question-card">
                <p class="question-label">QUESTION <%= q.getPosition() %></p>
                <p class="question-text"><%= q.getQuestionText() %></p>

                <% if (q.getImageUrl() != null && !q.getImageUrl().isEmpty()) { %>
                    <img class="question-image" src="<%= q.getImageUrl() %>" alt="question image">
                <% } %>

                <% if (q instanceof MultipleChoice) {
                       MultipleChoice mc = (MultipleChoice) q;
                       for (QuestionOption opt : mc.getOptions()) { %>
                        <div class="mc-options">
                            <label class="mc-option">
                                <input type="radio" name="q_<%= q.getId() %>" value="<%= opt.getOptionText() %>">
                                <%= opt.getOptionText() %>
                            </label>
                        </div>
                <%     }
                   } else { %>
                    <input class="text-answer-input" type="text" name="q_<%= q.getId() %>" placeholder="Your answer...">
                <% } %>
            </div>
        <% } %>

            <div style="margin-top:1.5rem; margin-bottom:2rem;">
                <button class="btn btn-success btn-lg" type="submit">&#10003; Submit Quiz</button>
            </div>
        </form>

    </div>
</div>

<footer class="site-footer">
    <div class="container">&copy; 2025 Quiz Website</div>
</footer>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="js/main.js"></script>
</body>
</html>

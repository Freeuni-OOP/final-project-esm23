<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.quizwebsite.model.*" %>
<%@ page import="com.quizwebsite.service.AnswerReviewRow" %>
<%@ page import="java.util.List" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    User user = (User) session.getAttribute("user");
    Quiz quiz = (Quiz) request.getAttribute("quiz");
    Question question = (Question) request.getAttribute("question");

    //safely redirects if page is accessed outside the regular servlet lifecycle flow//
    if (quiz == null || question == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    int questionNumber = (Integer) request.getAttribute("questionNumber");
    int totalQuestions = (Integer) request.getAttribute("totalQuestions");
    boolean isLastQuestion = (Boolean) request.getAttribute("isLastQuestion");
    AnswerReviewRow feedback = (AnswerReviewRow) request.getAttribute("feedback");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= quiz.getTitle() %> &mdash; Question <%= questionNumber %></title>
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
            <p class="quiz-progress">Question <%= questionNumber %> of <%= totalQuestions %></p>
            <%-- Type-safe practice mode flag assessment --%>
            <% if (Boolean.TRUE.equals(session.getAttribute("takeQuiz_isPractice"))) { %>
                <p class="quiz-progress"><em>Practice mode &mdash; this attempt will not be scored or saved.</em></p>
            <% } %>
        </div>

        <div class="question-card">
            <p class="question-label">QUESTION <%= questionNumber %> OF <%= totalQuestions %></p>

            <% if (feedback != null) { %>
                <%-- immediate-correction result view block --%>
                <div class="<%= feedback.isCorrect() ? "feedback-correct" : "feedback-incorrect" %>">
                    <p class="feedback-verdict"><%= feedback.isCorrect() ? "Correct!" : "Incorrect." %></p>
                    <p class="feedback-answer-row">Your answer: <code><%= feedback.getUserResponse() %></code></p>
                    <% if (!feedback.isCorrect()) { %>
                        <p class="feedback-answer-row">Correct answer: <strong><%= feedback.getCorrectAnswerText() %></strong></p>
                    <% } %>
                </div>
                <div style="margin-top:1rem;">
                    <form method="get" action="TakeQuizServlet">
                        <input type="hidden" name="action" value="<%= isLastQuestion ? "finish" : "next" %>">
                        <button class="btn btn-primary" type="submit"><%= isLastQuestion ? "See Results" : "Continue &rarr;" %></button>
                    </form>
                </div>

            <% } else { %>
                <%-- Standard question input presentation view block --%>
                <p class="question-text"><%= question.getQuestionText() %></p>

                <% if (question.getImageUrl() != null && !question.getImageUrl().isEmpty()) { %>
                    <img class="question-image" src="<%= question.getImageUrl() %>" alt="question image">
                <% } %>

                <form method="post" action="TakeQuizServlet">
                    <% if (question instanceof MultipleChoice) {
                           MultipleChoice mc = (MultipleChoice) question;
                           if (mc.getOptions() != null) {
                               for (QuestionOption opt : mc.getOptions()) { %>
                                <div class="mc-options">
                                    <label class="mc-option">
                                        <input type="radio" name="response" value="<%= opt.getOptionText() %>" required>
                                        <%= opt.getOptionText() %>
                                    </label>
                                </div>
                    <%         }
                           }
                       } else { %>
                        <%-- Added 'required' attribute to avoid accidental empty un-tracked submissions --%>
                        <input class="text-answer-input" type="text" name="response" placeholder="Your answer..." required autofocus>
                    <% } %>
                    <div style="margin-top:1.5rem;">
                        <button class="btn <%= isLastQuestion ? "btn-success" : "btn-primary" %>" type="submit">
                            <%= isLastQuestion ? "&#10003; Submit Quiz" : "Next &rarr;" %>
                        </button>
                    </div>
                </form>
            <% } %>
        </div>

    </div>
</div>

<footer class="site-footer">
    <div class="container">&copy; 2025 Quiz Website</div>
</footer>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="js/main.js"></script>
</body>
</html>

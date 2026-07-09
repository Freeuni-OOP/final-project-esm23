<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.quizwebsite.model.*" %>
<%@ page import="com.quizwebsite.model.User" %>
<%@ page import="com.quizwebsite.service.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    User user = (User) session.getAttribute("user");
    Quiz quiz = (Quiz) request.getAttribute("quiz");
    ScoringResult result = (ScoringResult) request.getAttribute("result");
    List<AnswerReviewRow> review = (List<AnswerReviewRow>) request.getAttribute("review");

    if (quiz == null || result == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    boolean isPractice = request.getAttribute("isPractice") != null && (Boolean) request.getAttribute("isPractice");
    List<QuizAttempt> topScores = (List<QuizAttempt>) request.getAttribute("topScores");
    Map<Long, String> topScoreNames = (Map<Long, String>) request.getAttribute("topScoreNames");

    int pct = result.maxScore() == 0 ? 0 : (int) Math.round(100.0 * result.score() / result.maxScore());
    int totalSec = Math.max(0, result.timeTakenSeconds());
    int min = totalSec / 60;
    int sec = totalSec % 60;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= quiz.getTitle() %> &mdash; Results — Quiz Website</title>
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

        <h1 class="page-title"><%= quiz.getTitle() %> &mdash; Results</h1>

        <% if (isPractice) { %>
        <div class="alert alert-info mb-2">
            &#128218; Practice attempt &mdash; graded but not saved to your history.
        </div>
        <% } %>

        <%-- Score card --%>
        <div class="results-score-card">
            <h2><%= pct %>%</h2>
            <div class="score-fraction"><%= result.score() %> / <%= result.maxScore() %> correct</div>
            <div class="progress-bar-wrap">
                <div class="progress-bar-fill" data-pct="<%= pct %>" style="width:0%"></div>
            </div>
            <div class="score-time">&#128337; Time: <%= min %>m <%= String.format("%02d", sec) %>s</div>
        </div>

        <%-- Answer review --%>
        <h2 class="section-title">Answer Review</h2>
        <% if (review != null && !review.isEmpty()) { %>
        <% for (AnswerReviewRow row : review) { %>
        <div class="review-item <%= row.isCorrect() ? "correct" : "incorrect" %>">
            <span class="review-points"><%= row.getEarnedPoints() %>/<%= row.getMaxPoints() %> pts</span>
            <p class="review-question"><%= row.getQuestionText() %></p>
            <p class="review-user-answer">
                Your answer: <code><%= row.getUserResponse() != null ? row.getUserResponse() : "(skipped)" %></code>
                &mdash; <strong style="color:<%= row.isCorrect() ? "var(--success)" : "var(--danger)" %>">
                <%= row.isCorrect() ? "Correct" : "Incorrect" %>
            </strong>
            </p>
            <% if (!row.isCorrect()) { %>
            <p class="review-correct-answer">&#10003; Correct answer: <strong><%= row.getCorrectAnswerText() %></strong></p>
            <% } %>
        </div>
        <% } %>
        <% } else { %>
        <div class="empty-state"><p>No question breakdown available for this attempt.</p></div>
        <% } %>

        <%-- Leaderboard --%>
        <% if (!isPractice && topScores != null && !topScores.isEmpty()) { %>
        <h2 class="section-title">&#127942; Top Scores</h2>
        <ol class="leaderboard">
            <% int rank = 1; for (QuizAttempt a : topScores) {
                String uname = topScoreNames != null ? topScoreNames.get(a.getId()) : null;
                if (uname == null) uname = "Unknown";
            %>
            <li>
                <span class="rank">#<%= rank++ %></span>
                <strong><%= uname %></strong>
                <span class="text-muted">&mdash; <%= a.getScore() %>/<%= a.getMaxScore() %></span>
            </li>
            <% } %>
        </ol>
        <% } %>

        <div class="form-actions mt-3">
            <a href="index.jsp" class="btn btn-primary">&#8592; Back to Home</a>
            <a href="TakeQuizServlet?quizId=<%= quiz.getId() %>" class="btn btn-secondary">Try Again</a>
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

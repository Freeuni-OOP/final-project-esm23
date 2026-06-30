<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.quizwebsite.model.*" %>
<%@ page import="com.quizwebsite.service.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    Quiz quiz = (Quiz) request.getAttribute("quiz");
    ScoringResult result = (ScoringResult) request.getAttribute("result");
    List<AnswerReviewRow> review = (List<AnswerReviewRow>) request.getAttribute("review");
    boolean isPractice = (Boolean) request.getAttribute("isPractice");
    List<QuizAttempt> topScores = (List<QuizAttempt>) request.getAttribute("topScores");
    Map<Long, String> topScoreNames = (Map<Long, String>) request.getAttribute("topScoreNames");

    int pct = result.maxScore() == 0 ? 0 : (int) Math.round(100.0 * result.score() / result.maxScore());
%>
<!DOCTYPE html>
<html>
<head>
    <title><%= quiz.getTitle() %> &mdash; Results</title>
</head>
<body>
<h1><%= quiz.getTitle() %> &mdash; Results</h1>

<% if (isPractice) { %>
    <p><em>This was a practice attempt &mdash; it was graded but not saved to your history.</em></p>
<% } %>

<h2>Score: <%= result.score() %> / <%= result.maxScore() %> (<%= pct %>%)</h2>
<p>Time taken: <%= result.timeTakenSeconds() / 60 %> min <%= result.timeTakenSeconds() % 60 %> sec</p>

<h3>Review</h3>
<% for (AnswerReviewRow row : review) { %>
    <div style="margin-bottom:15px; padding:8px; border:1px solid <%= row.isCorrect() ? "#2e7d32" : "#c62828" %>;">
        <p><strong><%= row.getQuestionText() %></strong></p>
        <p>Your answer: <%= row.getUserResponse() %> &mdash;
           <%= row.isCorrect() ? "Correct" : "Incorrect" %>
           (<%= row.getEarnedPoints() %>/<%= row.getMaxPoints() %> pts)</p>
        <% if (!row.isCorrect()) { %>
            <p>Correct answer: <%= row.getCorrectAnswerText() %></p>
        <% } %>
    </div>
<% } %>

<% if (!isPractice && topScores != null && !topScores.isEmpty()) { %>
    <h3>Top Scores for this Quiz</h3>
    <ol>
        <% for (QuizAttempt a : topScores) { %>
            <li><%= topScoreNames.get(a.getId()) %> &mdash; <%= a.getScore() %>/<%= a.getMaxScore() %></li>
        <% } %>
    </ol>
<% } %>

<p><a href="index.jsp">Back to Home</a></p>
</body>
</html>

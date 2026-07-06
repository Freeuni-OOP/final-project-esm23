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

    //safety check to redirect if accessed outside normal servlet submission flow//
    if (quiz == null || result == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    boolean isPractice = request.getAttribute("isPractice") != null && (Boolean) request.getAttribute("isPractice");
    List<QuizAttempt> topScores = (List<QuizAttempt>) request.getAttribute("topScores");
    Map<Long, String> topScoreNames = (Map<Long, String>) request.getAttribute("topScoreNames");

    int pct = result.maxScore() == 0 ? 0 : (int) Math.round(100.0 * result.score() / result.maxScore());

    //cleans calculations for time display//
    int totalSec = Math.max(0, result.timeTakenSeconds());
    int min = totalSec / 60;
    int sec = totalSec % 60;
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
<p>Time taken: <%= min %> min <%= sec %> sec</p>

<h3>Review</h3>
<% if (review != null && !review.isEmpty()) { %>
    <% for (AnswerReviewRow row : review) { %>
        <div style="margin-bottom:15px; padding:10px; border:1px solid <%= row.isCorrect() ? "#2e7d32" : "#c62828" %>; background-color: <%= row.isCorrect() ? "#e8f5e9" : "#ffebee" %>;">
            <p><strong><%= row.getQuestionText() %></strong></p>
            <%-- Wrapped user input in <code> tags to prevent XSS string distortion --%>
            <p>Your answer: <code><%= row.getUserResponse() != null ? row.getUserResponse() : "(skipped)" %></code> &mdash;
               <strong><%= row.isCorrect() ? "Correct" : "Incorrect" %></strong>
               (<%= row.getEarnedPoints() %>/<%= row.getMaxPoints() %> pts)</p>
            <% if (!row.isCorrect()) { %>
                <p>Correct answer: <strong><%= row.getCorrectAnswerText() %></strong></p>
            <% } %>
        </div>
    <% } %>
<% } else { %>
    <p>No question breakdown review available for this attempt.</p>
<% } %>

<% if (!isPractice && topScores != null && !topScores.isEmpty()) { %>
    <h3>Top Scores for this Quiz</h3>
    <ol>
        <% for (QuizAttempt a : topScores) {
            String username = topScoreNames != null ? topScoreNames.get(a.getId()) : "Unknown User";
        %>
            <li><strong><%= username != null ? username : "Unknown User" %></strong> &mdash; <%= a.getScore() %>/<%= a.getMaxScore() %></li>
        <% } %>
    </ol>
<% } %>

<p><a href="index.jsp">Back to Home</a></p>
</body>
</html>
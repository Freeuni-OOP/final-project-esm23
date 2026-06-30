<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.quizwebsite.model.*" %>
<%@ page import="com.quizwebsite.service.AnswerReviewRow" %>
<%@ page import="java.util.List" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    Quiz quiz = (Quiz) request.getAttribute("quiz");
    Question question = (Question) request.getAttribute("question");

    // Safely redirect if page is accessed outside the regular servlet lifecycle flow
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
<html>
<head>
    <title><%= quiz.getTitle() %> &mdash; Question <%= questionNumber %></title>
</head>
<body>
<h1><%= quiz.getTitle() %></h1>
<p>Question <%= questionNumber %> of <%= totalQuestions %></p>

<%-- Type-safe practice mode flag assessment --%>
<% if (Boolean.TRUE.equals(session.getAttribute("takeQuiz_isPractice"))) { %>
    <p><em>Practice mode &mdash; this attempt will not be scored or saved.</em></p>
<% } %>

<% if (feedback != null) { %>
    <%-- immediate-correction result view block --%>
    <div style="margin:15px 0; padding:10px; border:1px solid <%= feedback.isCorrect() ? "#2e7d32" : "#c62828" %>; background-color: <%= feedback.isCorrect() ? "#e8f5e9" : "#ffebee" %>;">
        <p><strong><%= feedback.isCorrect() ? "Correct!" : "Incorrect." %></strong></p>
        <p>Your answer: <code><%= feedback.getUserResponse() %></code></p>
        <% if (!feedback.isCorrect()) { %>
            <p>Correct answer: <strong><%= feedback.getCorrectAnswerText() %></strong></p>
        <% } %>
    </div>
    <form method="get" action="TakeQuizServlet">
        <input type="hidden" name="action" value="<%= isLastQuestion ? "finish" : "next" %>">
        <button type="submit"><%= isLastQuestion ? "See Results" : "Continue" %></button>
    </form>

<% } else { %>
    <%-- Standard question input presentation view block --%>
    <div style="margin-bottom:20px;">
        <p><strong><%= question.getQuestionText() %></strong></p>

        <% if (question.getImageUrl() != null && !question.getImageUrl().isEmpty()) { %>
            <p><img src="<%= question.getImageUrl() %>" alt="question image" style="max-width:300px; border:1px solid #ddd;"></p>
        <% } %>

        <form method="post" action="TakeQuizServlet">
            <% if (question instanceof MultipleChoice) {
                   MultipleChoice mc = (MultipleChoice) question;
                   if (mc.getOptions() != null) {
                       for (QuestionOption opt : mc.getOptions()) { %>
                        <label>
                            <input type="radio" name="response" value="<%= opt.getOptionText() %>" required>
                            <%= opt.getOptionText() %>
                        </label><br>
            <%         }
                   }
               } else { %>
                <%-- Added 'required' attribute to avoid accidental empty un-tracked submissions --%>
                <input type="text" name="response" size="40" required autofocus>
            <% } %>
            <br><br>
            <button type="submit"><%= isLastQuestion ? "Submit Quiz" : "Next" %></button>
        </form>
    </div>
<% } %>
</body>
</html>
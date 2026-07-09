<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.quizwebsite.model.User" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    if (session.getAttribute("newQuizId") == null) {
        response.sendRedirect("createQuiz.jsp");
        return;
    }

    User user = (User) session.getAttribute("user");
    int position    = (Integer) session.getAttribute("questionPosition");
    String quizTitle = (String) session.getAttribute("newQuizTitle");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Add Question <%= position %> — <%= quizTitle %></title>
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

        <p class="text-muted text-small mb-1">Creating: <strong><%= quizTitle %></strong></p>
        <h1 class="page-title">Question #<%= position %></h1>

        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error">
            <span class="alert-icon">&#9888;</span>
            <%= request.getAttribute("error") %>
        </div>
        <% } %>

        <div class="quiz-form-card">
            <form id="addQuestionForm" method="post" action="AddQuestionServlet">

                <div class="form-group">
                    <label class="form-label" for="questionType">Question Type</label>
                    <select class="form-control" id="questionType" name="questionType">
                        <option value="QUESTION_RESPONSE">Question Response (typed answer)</option>
                        <option value="FILL_BLANK">Fill in the Blank</option>
                        <option value="MULTIPLE_CHOICE">Multiple Choice</option>
                        <option value="PICTURE_RESPONSE">Picture Response</option>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label" for="questionText">Question Text</label>
                    <input class="form-control" type="text" id="questionText" name="questionText" required>
                </div>

                <%-- ---- Text answer (QUESTION_RESPONSE + FILL_BLANK) ---- --%>
                <div id="sectionTextAnswer">
                    <div class="form-group">
                        <label class="form-label">Accepted Answer(s)</label>
                        <p class="form-hint">Add multiple rows to accept alternate spellings.</p>
                        <div id="answerSlots">
                            <div class="answer-slot">
                                <input class="form-control" type="text" name="correctAnswer" placeholder="Accepted answer 1">
                                <button type="button" class="btn-remove-answer" title="Remove">&times;</button>
                            </div>
                        </div>
                        <button type="button" id="btnAddAnswer" class="btn btn-secondary btn-sm mt-1">+ Add alternate answer</button>
                    </div>
                </div>

                <%-- ---- Multiple Choice ---- --%>
                <div id="sectionMultipleChoice" style="display:none;">
                    <div class="form-group">
                        <label class="form-label">Options <span class="form-hint">(select the radio next to the correct answer)</span></label>
                        <div id="mcOptions">
                            <div class="mc-option-row">
                                <input type="radio" name="correctOption" value="0" checked>
                                <input class="form-control" type="text" name="option0" placeholder="Option A">
                                <button type="button" class="btn-remove-option" title="Remove">&times;</button>
                            </div>
                            <div class="mc-option-row">
                                <input type="radio" name="correctOption" value="1">
                                <input class="form-control" type="text" name="option1" placeholder="Option B">
                                <button type="button" class="btn-remove-option" title="Remove">&times;</button>
                            </div>
                            <div class="mc-option-row">
                                <input type="radio" name="correctOption" value="2">
                                <input class="form-control" type="text" name="option2" placeholder="Option C">
                                <button type="button" class="btn-remove-option" title="Remove">&times;</button>
                            </div>
                            <div class="mc-option-row">
                                <input type="radio" name="correctOption" value="3">
                                <input class="form-control" type="text" name="option3" placeholder="Option D">
                                <button type="button" class="btn-remove-option" title="Remove">&times;</button>
                            </div>
                        </div>
                        <button type="button" id="btnAddOption" class="btn btn-secondary btn-sm mt-1">+ Add option</button>
                    </div>
                </div>

                <%-- ---- Picture Response ---- --%>
                <div id="sectionPicture" style="display:none;">
                    <div class="form-group">
                        <label class="form-label" for="imageUrl">Image URL</label>
                        <input class="form-control" type="text" id="imageUrl" name="imageUrl"
                               placeholder="https://example.com/image.jpg">
                        <div class="mt-1">
                            <img id="imagePreview" src="" alt="preview" class="question-image" style="display:none;">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="correctAnswerPicture">Correct Answer</label>
                        <input class="form-control" type="text" id="correctAnswerPicture" name="correctAnswerPicture">
                    </div>
                </div>

                <div class="form-actions">
                    <button class="btn btn-secondary" type="submit" name="action" value="addAnother">+ Add Another Question</button>
                    <button class="btn btn-success" type="submit" name="action" value="finish">&#10003; Finish &amp; Save Quiz</button>
                </div>

            </form>
        </div>
    </div>
</div>

<footer class="site-footer">
    <div class="container">&copy; 2025 Quiz Website</div>
</footer>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="js/main.js"></script>
<script src="js/addQuestion.js"></script>
</body>
</html>

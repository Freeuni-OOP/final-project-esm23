<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.quizwebsite.model.User" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    User user = (User) session.getAttribute("user");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Create Quiz — Quiz Website</title>
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
        <h1 class="page-title">Create a New Quiz</h1>

        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error">
            <span class="alert-icon">&#9888;</span>
            <%= request.getAttribute("error") %>
        </div>
        <% } %>

        <div class="quiz-form-card">
            <form method="post" action="CreateQuizServlet">

                <div class="form-group">
                    <label class="form-label" for="title">Quiz Title</label>
                    <input class="form-control" type="text" id="title" name="title" required maxlength="200"
                           value="<%= request.getAttribute("title") != null ? request.getAttribute("title") : "" %>">
                </div>

                <div class="form-group">
                    <label class="form-label" for="description">Description</label>
                    <textarea class="form-control" id="description" name="description" rows="4"><%= request.getAttribute("description") != null ? request.getAttribute("description") : "" %></textarea>
                </div>

                <div class="quiz-options-group">
                    <div class="section-label">Quiz Options</div>

                    <label class="form-check">
                        <input type="checkbox" name="randomOrder">
                        Randomize question order
                    </label>

                    <label class="form-check">
                        <input type="checkbox" name="onePage">
                        Show all questions on one page
                    </label>

                    <label class="form-check">
                        <input type="checkbox" name="immediateCorrection">
                        Immediate correction (show right/wrong after each answer)
                    </label>

                    <label class="form-check">
                        <input type="checkbox" name="practiceEnabled">
                        Allow practice mode (score not recorded)
                    </label>
                </div>

                <div class="form-actions">
                    <button class="btn btn-primary" type="submit">Next: Add Questions &rarr;</button>
                    <a href="index.jsp" class="btn btn-secondary">Cancel</a>
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
</body>
</html>

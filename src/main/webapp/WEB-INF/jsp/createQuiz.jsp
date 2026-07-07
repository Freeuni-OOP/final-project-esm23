<%@ page contentType="text/html;charset=UTF-8" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Create Quiz</title>
</head>
<body>
<h1>Create a New Quiz</h1>

<% if (request.getAttribute("error") != null) { %>
    <p style="color:red;"><%= request.getAttribute("error") %></p>
<% } %>

<form method="post" action="CreateQuizServlet">

    <label>Quiz Title:<br>
        <input type="text" name="title" required maxlength="200"
               value="<%= request.getAttribute("title") != null ? request.getAttribute("title") : "" %>">
    </label><br><br>

    <label>Description:<br>
        <textarea name="description" rows="4" cols="50"><%= request.getAttribute("description") != null ? request.getAttribute("description") : "" %></textarea>
    </label><br><br>

    <label>
        <input type="checkbox" name="randomOrder"> Randomize question order
    </label><br>

    <label>
        <input type="checkbox" name="onePage"> Show all questions on one page
    </label><br>

    <label>
        <input type="checkbox" name="immediateCorrection"> Immediate correction (show if answer right/wrong after each)
    </label><br>

    <label>
        <input type="checkbox" name="practiceEnabled"> Practice mode (no score recorded)
    </label><br><br>

    <button type="submit">Next: Add Questions →</button>
</form>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    if (session.getAttribute("newQuizId") == null) {
        response.sendRedirect("createQuiz.jsp");
        return;
    }

    int position = (Integer) session.getAttribute("questionPosition");
    String quizTitle = (String) session.getAttribute("newQuizTitle");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Add Question <%= position %></title>
    <script>
        function updateForm() {
            var type = document.getElementById("questionType").value;

            // Hide all type-specific sections first
            document.getElementById("sectionTextAnswer").style.display = "none";
            document.getElementById("sectionMultipleChoice").style.display = "none";
            document.getElementById("sectionPicture").style.display = "none";

            if (type === "QUESTION_RESPONSE" || type === "FILL_BLANK") {
                document.getElementById("sectionTextAnswer").style.display = "block";
            } else if (type === "MULTIPLE_CHOICE") {
                document.getElementById("sectionMultipleChoice").style.display = "block";
            } else if (type === "PICTURE_RESPONSE") {
                document.getElementById("sectionPicture").style.display = "block";
            }
        }

        // Run on page load to show correct section for default selection
        window.onload = function() { updateForm(); };
    </script>
</head>
<body>
<h2>Quiz: "<%= quizTitle %>"</h2>
<h3>Question #<%= position %></h3>

<% if (request.getAttribute("error") != null) { %>
    <p style="color:red;"><%= request.getAttribute("error") %></p>
<% } %>

<form method="post" action="AddQuestionServlet">

    <label>Question Type:
        <select id="questionType" name="questionType" onchange="updateForm()">
            <option value="QUESTION_RESPONSE">Question Response (typed answer)</option>
            <option value="FILL_BLANK">Fill in the Blank</option>
            <option value="MULTIPLE_CHOICE">Multiple Choice</option>
            <option value="PICTURE_RESPONSE">Picture Response</option>
        </select>
    </label><br><br>

    <label>Question Text:<br>
        <input type="text" name="questionText" required style="width: 450px">
    </label><br><br>

    <!-- QUESTION_RESPONSE and FILL_BLANK: just a text answer -->
    <div id="sectionTextAnswer">
        <label>Correct Answer:<br>
            <input type="text" name="correctAnswer" style="width: 300px">
        </label><br><br>
    </div>

    <!-- MULTIPLE_CHOICE: 4 options, radio button picks the correct one -->
    <div id="sectionMultipleChoice" style="display:none;">
        <p><strong>Enter options. Select the radio button next to the correct answer.</strong></p>
        <input type="radio" name="correctOption" value="0" checked>
        <input type="text" name="option0" placeholder="Option A" style="width:300px"><br><br>

        <input type="radio" name="correctOption" value="1">
        <input type="text" name="option1" placeholder="Option B" style="width:300px"><br><br>

        <input type="radio" name="correctOption" value="2">
        <input type="text" name="option2" placeholder="Option C" style="width:300px"><br><br>

        <input type="radio" name="correctOption" value="3">
        <input type="text" name="option3" placeholder="Option D" style="width:300px"><br><br>
    </div>

    <!-- PICTURE_RESPONSE: URL + typed answer -->
    <div id="sectionPicture" style="display:none;">
        <label>Image URL:<br>
            <input type="text" name="imageUrl" style="width: 450px" placeholder="https://example.com/image.jpg">
        </label><br><br>
        <label>Correct Answer:<br>
            <input type="text" name="correctAnswerPicture" style="width: 300px">
        </label><br><br>
    </div>

    <button type="submit" name="action" value="addAnother">+ Add Another Question</button>
    &nbsp;&nbsp;
    <button type="submit" name="action" value="finish">✓ Finish &amp; Save Quiz</button>

</form>
</body>
</html>
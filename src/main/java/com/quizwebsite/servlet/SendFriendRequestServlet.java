package com.quizwebsite.servlet;

import com.quizwebsite.dao.FriendshipDAO;
import com.quizwebsite.dao.UserDAO;
import com.quizwebsite.model.Friendship;
import com.quizwebsite.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@WebServlet("/SendFriendRequestServlet")
public class SendFriendRequestServlet extends HttpServlet {

    private final FriendshipDAO friendshipDAO = new FriendshipDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Ensure user is logged in
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String username = request.getParameter("username");

        try {
            // Validate that username was provided
            if (username == null || username.trim().isEmpty()) {
                redirectWithError(request, response, "Please enter a username.");
                return;
            }

            // Look up the target user
            User target = userDAO.findByUsername(username.trim());

            // If user doesn't exist, report error
            if (target == null) {
                redirectWithError(request, response, "User not found.");
                return;
            }

            // Can't friend yourself
            if (target.getId() == user.getId()) {
                redirectWithError(request, response, "You cannot send a friend request to yourself.");
                return;
            }

            // Check if a friendship (in any state) already exists
            if (friendshipDAO.find(user.getId(), target.getId()) != null) {
                redirectWithError(request, response, "You are already friends or a request is pending.");
                return;
            }

            // All checks passed - create the friendship (defaults to PENDING)
            friendshipDAO.insert(new Friendship(user.getId(), target.getId()));

            // Redirect back to friends page showing the updated list
            response.sendRedirect(request.getContextPath() + "/FriendsServlet");

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void redirectWithError(
            HttpServletRequest request,
            HttpServletResponse response,
            String errorMessage
    ) throws IOException {

        // URL-encode the error message so it can be safely passed as a query parameter
        // (spaces, special chars, etc. get encoded)
        String encodedError = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        // Redirect back to FriendsServlet with the error in the query string
        // FriendsServlet will pick it up and pass it to friends.jsp to display
        response.sendRedirect(
                request.getContextPath() + "/FriendsServlet?error=" + encodedError
        );
    }
}
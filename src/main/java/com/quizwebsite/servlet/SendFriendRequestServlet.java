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

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String username = request.getParameter("username");

        try {
            if (username == null || username.trim().isEmpty()) {
                redirectWithError(request, response, "Please enter a username.");
                return;
            }

            User target = userDAO.findByUsername(username.trim());

            if (target == null) {
                redirectWithError(request, response, "User not found.");
                return;
            }

            if (target.getId() == user.getId()) {
                redirectWithError(request, response, "You cannot send a friend request to yourself.");
                return;
            }

            if (friendshipDAO.find(user.getId(), target.getId()) != null) {
                redirectWithError(request, response, "You are already friends or a request is pending.");
                return;
            }

            friendshipDAO.insert(new Friendship(user.getId(), target.getId()));

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

        String encodedError = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        response.sendRedirect(
                request.getContextPath() + "/FriendsServlet?error=" + encodedError
        );
    }
}
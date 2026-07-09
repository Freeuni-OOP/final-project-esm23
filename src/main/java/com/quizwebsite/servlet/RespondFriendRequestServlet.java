package com.quizwebsite.servlet;

import com.quizwebsite.dao.FriendshipDAO;
import com.quizwebsite.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/RespondFriendRequestServlet")
public class RespondFriendRequestServlet extends HttpServlet {

    private final FriendshipDAO friendshipDAO = new FriendshipDAO();

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

        // Get the ID of the person who sent the request (or the friend to remove)
        String requesterIdStr = request.getParameter("requesterId");

        if (requesterIdStr == null || requesterIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/FriendsServlet");
            return;
        }

        // Parse the ID to a long
        long requesterId;

        try {
            requesterId = Long.parseLong(requesterIdStr);
        } catch (NumberFormatException e) {
            // Invalid ID format, just redirect back
            response.sendRedirect(request.getContextPath() + "/FriendsServlet");
            return;
        }

        // Get the action the user wants to perform
        String action = request.getParameter("action");

        try {
            if ("accept".equals(action)) {
                // Accept the incoming friend request by marking it as ACCEPTED
                friendshipDAO.accept(requesterId, user.getId());
            } else if ("reject".equals(action) || "remove".equals(action)) {
                // Reject an incoming request or remove an existing friend
                // (both just delete the friendship row)
                friendshipDAO.delete(user.getId(), requesterId);
            }

            // Redirect back to friends page to see the updated state
            response.sendRedirect(request.getContextPath() + "/FriendsServlet");

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
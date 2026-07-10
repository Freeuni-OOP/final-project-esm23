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

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String requesterIdStr = request.getParameter("requesterId");

        if (requesterIdStr == null || requesterIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/FriendsServlet");
            return;
        }

        long requesterId;

        try {
            requesterId = Long.parseLong(requesterIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/FriendsServlet");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("accept".equals(action)) {
                friendshipDAO.accept(requesterId, user.getId());
            } else if ("reject".equals(action) || "remove".equals(action)) {
                friendshipDAO.delete(user.getId(), requesterId);
            }

            response.sendRedirect(request.getContextPath() + "/FriendsServlet");

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
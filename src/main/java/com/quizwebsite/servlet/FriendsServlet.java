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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/FriendsServlet")
public class FriendsServlet extends HttpServlet {

    public record FriendView(long id, String username) {}

    private final FriendshipDAO friendshipDAO = new FriendshipDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            List<FriendView> friends = new ArrayList<>();
            for (Friendship f : friendshipDAO.findAccepted(user.getId())) {
                long otherUserId = f.getUserId() == user.getId()
                        ? f.getFriendId()
                        : f.getUserId();
                User otherUser = userDAO.findById(otherUserId);
                if (otherUser != null) {
                    friends.add(new FriendView(otherUser.getId(), otherUser.getUsername()));
                }
            }

            List<FriendView> pendingRequests = new ArrayList<>();
            for (Friendship f : friendshipDAO.findPendingReceived(user.getId())) {
                User requester = userDAO.findById(f.getUserId());
                if (requester != null) {
                    pendingRequests.add(new FriendView(requester.getId(), requester.getUsername()));
                }
            }

            request.setAttribute("friends", friends);
            request.setAttribute("pendingRequests", pendingRequests);

            // error message forwarded from SendFriendRequestServlet redirect
            String error = request.getParameter("error");
            if (error != null && !error.isEmpty()) {
                request.setAttribute("error", error);
            }

            // optional username search
            String q = request.getParameter("q");
            if (q != null && !q.trim().isEmpty()) {
                List<FriendView> searchResults = new ArrayList<>();
                for (User found : userDAO.searchByUsername(q.trim())) {
                    if (found.getId() != user.getId()) {
                        searchResults.add(new FriendView(found.getId(), found.getUsername()));
                    }
                }
                request.setAttribute("searchResults", searchResults);
            }

            request.getRequestDispatcher("/WEB-INF/jsp/friends.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

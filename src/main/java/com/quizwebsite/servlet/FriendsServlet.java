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

    // Simple data class to hold just the id and username of a user
    // (makes it cleaner to pass friend data to the JSP)
    public record FriendView(long id, String username) {}

    private final FriendshipDAO friendshipDAO = new FriendshipDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if user is logged in
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        // If not, send them to login (redundant check since AuthFilter also does this,
        // but keeping it as a safety measure)
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // Build list of accepted friends by looking up the other user in each friendship
            List<FriendView> friends = new ArrayList<>();
            for (Friendship f : friendshipDAO.findAccepted(user.getId())) {
                // Determine which side of the friendship we're on
                // (could be user_id or friend_id in the database row)
                long otherUserId = f.getUserId() == user.getId()
                        ? f.getFriendId()
                        : f.getUserId();
                User otherUser = userDAO.findById(otherUserId);
                if (otherUser != null) {
                    friends.add(new FriendView(otherUser.getId(), otherUser.getUsername()));
                }
            }

            // Build list of incoming friend requests (people who added us) with their names
            List<FriendView> pendingRequests = new ArrayList<>();
            for (Friendship f : friendshipDAO.findPendingReceived(user.getId())) {
                User requester = userDAO.findById(f.getUserId());
                if (requester != null) {
                    pendingRequests.add(new FriendView(requester.getId(), requester.getUsername()));
                }
            }

            // Pass both lists to the JSP
            request.setAttribute("friends", friends);
            request.setAttribute("pendingRequests", pendingRequests);

            // If SendFriendRequestServlet encountered an error (validation failed),
            // it passes the error message as a query param; we forward it to the JSP
            String error = request.getParameter("error");
            if (error != null && !error.isEmpty()) {
                request.setAttribute("error", error);
            }

            // Handle optional username search - if user submitted a query, find matching users
            // (but exclude the current user from results)
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

            // Forward all data to friends.jsp
            request.getRequestDispatcher("/WEB-INF/jsp/friends.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

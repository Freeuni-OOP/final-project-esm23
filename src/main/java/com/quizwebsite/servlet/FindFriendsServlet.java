package com.quizwebsite.servlet;

import com.quizwebsite.dao.FriendshipDAO;
import com.quizwebsite.dao.UserDAO;
import com.quizwebsite.model.Friendship;
import com.quizwebsite.model.FriendshipStatus;
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

//lets a logged-in user search for other users by username and see whether//
//they can send a friend request, have one pending, or are already friends//
//auth is enforced by AuthFilter.//
@WebServlet("/FindFriendsServlet")
public class FindFriendsServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final FriendshipDAO friendshipDAO = new FriendshipDAO();

    //status is one of: NONE, PENDING_SENT, PENDING_RECEIVED, FRIENDS//
    public record SearchResult(long userId, String username, String status) {}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        String query = request.getParameter("q");
        request.setAttribute("query", query);

        try {
            if (query != null && !query.trim().isEmpty()) {
                List<User> matches = userDAO.searchByUsername(query.trim());
                List<SearchResult> results = new ArrayList<>();
                for (User match : matches) {
                    if (match.getId() == user.getId()) {
                        continue; // don't show yourself in search results
                    }
                    results.add(new SearchResult(match.getId(), match.getUsername(),
                            friendshipStatus(user.getId(), match.getId())));
                }
                request.setAttribute("results", results);
            }
            request.getRequestDispatcher("/WEB-INF/jsp/findFriends.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Could not search for users", e);
        }
    }

    private String friendshipStatus(long currentUserId, long otherUserId) throws SQLException {
        Friendship f = friendshipDAO.find(currentUserId, otherUserId);
        if (f == null) {
            return "NONE";
        }
        if (f.getStatus() == FriendshipStatus.ACCEPTED) {
            return "FRIENDS";
        }
        return f.getUserId() == currentUserId ? "PENDING_SENT" : "PENDING_RECEIVED";
    }
}

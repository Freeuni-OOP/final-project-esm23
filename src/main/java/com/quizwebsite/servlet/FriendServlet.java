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
public class FriendServlet extends HttpServlet {

    private final FriendshipDAO friendshipDAO = new FriendshipDAO();
    private final UserDAO userDAO = new UserDAO();

    public record FriendView(long id, String username) {}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        User user = null;
        if (session != null) {
            user = (User) session.getAttribute("user");
        }

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            List<FriendView> friends = new ArrayList<>();

            List<Friendship> acceptedFriendships = friendshipDAO.findAccepted(user.getId());

            for (Friendship f : acceptedFriendships) {
                long otherUserId = f.getUserId() == user.getId()
                        ? f.getFriendId()
                        : f.getUserId();

                User otherUser = userDAO.findById(otherUserId);

                if (otherUser != null) {
                    friends.add(new FriendView(otherUser.getId(), otherUser.getUsername()));
                }
            }

            List<FriendView> pendingRequests = new ArrayList<>();

            List<Friendship> pendingFriendships = friendshipDAO.findPendingReceived(user.getId());

            for (Friendship f : pendingFriendships) {
                User requester = userDAO.findById(f.getUserId());

                if (requester != null) {
                    pendingRequests.add(new FriendView(requester.getId(), requester.getUsername()));
                }
            }

            String q = request.getParameter("q");

            if (q != null && !q.trim().isEmpty()) {
                List<FriendView> searchResults = new ArrayList<>();

                List<User> foundUsers = userDAO.searchByUsername(q.trim());

                for (User foundUser : foundUsers) {
                    if (foundUser.getId() != user.getId()) {
                        searchResults.add(
                                new FriendView(foundUser.getId(), foundUser.getUsername())
                        );
                    }
                }

                request.setAttribute("searchResults", searchResults);
            }

            String error = request.getParameter("error");

            if (error != null && !error.trim().isEmpty()) {
                request.setAttribute("error", error);
            }

            request.setAttribute("friends", friends);
            request.setAttribute("pendingRequests", pendingRequests);

            request.getRequestDispatcher("/WEB-INF/jsp/friends.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
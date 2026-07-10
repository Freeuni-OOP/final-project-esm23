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

//shows the logged-in user's incoming (pending) friend requests so they can//
//accept or decline each one. Auth is enforced by AuthFilter//
@WebServlet("/FriendRequestsServlet")
public class FriendRequestsServlet extends HttpServlet {

    private final FriendshipDAO friendshipDAO = new FriendshipDAO();
    private final UserDAO userDAO = new UserDAO();

    //simple DTO so the JSP has both the requester's info and the request itself//
    public record PendingRequest(long requesterId, String requesterUsername) {}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        try {
            List<Friendship> pending = friendshipDAO.findPendingReceived(user.getId());
            List<PendingRequest> requests = new ArrayList<>();
            for (Friendship f : pending) {
                User requester = userDAO.findById(f.getUserId());
                requests.add(new PendingRequest(f.getUserId(),
                        requester != null ? requester.getUsername() : "unknown"));
            }
            request.setAttribute("pendingRequests", requests);
            request.getRequestDispatcher("/WEB-INF/jsp/friendRequests.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Could not load friend requests", e);
        }
    }
}

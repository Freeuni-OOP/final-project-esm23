package main.java.com.quizwebsite.servlet;

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

//accepts or declines a pending friend request sent TO the logged-in user//
//auth is enforced by AuthFilter//
@WebServlet("/RespondFriendRequestServlet")
public class RespondFriendRequestServlet extends HttpServlet {

    private final FriendshipDAO friendshipDAO = new FriendshipDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        String requesterIdParam = request.getParameter("requesterId");
        String action = request.getParameter("action"); // "accept" or "decline"

        if (requesterIdParam == null || action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            long requesterId = Long.parseLong(requesterIdParam);

            if ("accept".equalsIgnoreCase(action)) {
                //requester = user_id (sender), current user = friend_id (receiver)//
                friendshipDAO.accept(requesterId, user.getId());
            } else {
                //decline: just remove the pending row//
                friendshipDAO.delete(requesterId, user.getId());
            }

            response.sendRedirect(request.getContextPath() + "/FriendRequestsServlet");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            throw new ServletException("Could not respond to friend request", e);
        }
    }
}

package main.java.com.quizwebsite.servlet;

import com.quizwebsite.dao.FriendshipDAO;
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

//sends a friend request from the logged-in user to another user//
//auth is enforced by AuthFilter//
@WebServlet("/SendFriendRequestServlet")
public class SendFriendRequestServlet extends HttpServlet {

    private final FriendshipDAO friendshipDAO = new FriendshipDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        String friendIdParam = request.getParameter("friendId");
        String redirect = request.getParameter("redirect");
        String target = redirect != null && !redirect.isBlank()
                ? redirect
                : request.getContextPath() + "/HomeServlet";

        if (friendIdParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            long friendId = Long.parseLong(friendIdParam);

            //can't friend yourself, and don't duplicate an existing request/friendship//
            if (friendId != user.getId() && friendshipDAO.find(user.getId(), friendId) == null) {
                friendshipDAO.insert(new Friendship(user.getId(), friendId));
            }

            response.sendRedirect(target);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            throw new ServletException("Could not send friend request", e);
        }
    }
}

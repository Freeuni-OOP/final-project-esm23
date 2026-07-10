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

//removes an existing friendship (unfriend) between the logged-in user and another user//
//auth is enforced by AuthFilter//
@WebServlet("/RemoveFriendServlet")
public class RemoveFriendServlet extends HttpServlet {

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
            friendshipDAO.delete(user.getId(), friendId);
            response.sendRedirect(target);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            throw new ServletException("Could not remove friend", e);
        }
    }
}

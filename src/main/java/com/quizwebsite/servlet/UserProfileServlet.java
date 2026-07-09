package com.quizwebsite.servlet;

import com.quizwebsite.dao.FriendshipDAO;
import com.quizwebsite.dao.QuizAttemptDAO;
import com.quizwebsite.dao.QuizDAO;
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

@WebServlet("/UserProfileServlet")
public class UserProfileServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final FriendshipDAO friendshipDAO = new FriendshipDAO();
    private final QuizDAO quizDAO = new QuizDAO();
    private final QuizAttemptDAO quizAttemptDAO = new QuizAttemptDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("user") : null;
        String idParam = request.getParameter("id");

        if (idParam == null) {
            response.sendRedirect("HomeServlet");
            return;
        }
        try {
            long profileUserId = Long.parseLong(idParam);

            // user whose profile we want to display
            User profileUser = userDAO.findById(profileUserId);
            if (profileUser == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // profile statistics
            int friendCount = friendshipDAO.findAccepted(profileUserId).size();
            int createdQuizCount = quizDAO.countByCreator(profileUserId);
            int takenQuizCount = quizAttemptDAO.findByUser(profileUserId).size();

            boolean isOwnProfile = false;
            if (currentUser != null && currentUser.getId() == profileUserId) {
                isOwnProfile = true;
            }

            // send data to jsp
            request.setAttribute("profileUser", profileUser);
            request.setAttribute("friendCount", friendCount);
            request.setAttribute("createdQuizCount", createdQuizCount);
            request.setAttribute("takenQuizCount", takenQuizCount);
            request.setAttribute("isOwnProfile", isOwnProfile);

            request.getRequestDispatcher("/WEB-INF/jsp/userProfile.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            throw new ServletException("Could not load user profile", e);
        }
    }
}

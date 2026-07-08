package com.quizwebsite.servlet;

import com.quizwebsite.dao.AnnouncementDAO;
import com.quizwebsite.dao.FriendshipDAO;
import com.quizwebsite.dao.QuizDAO;
import com.quizwebsite.dao.UserDAO;
import com.quizwebsite.model.Friendship;
import com.quizwebsite.model.Quiz;
import com.quizwebsite.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//builds the real landing page: recent/popular quizzes for everyone, plus
//the logged-in user's own quizzes, friends, and the latest announcements.
//index.jsp just forwards here so login/logout can keep redirecting to "index.jsp"//
@WebServlet("/HomeServlet")
public class HomeServlet extends HttpServlet {

    private static final int RECENT_LIMIT = 6;
    private static final int POPULAR_LIMIT = 6;
    private static final int ANNOUNCEMENT_LIMIT = 3;

    private final QuizDAO quizDAO = new QuizDAO();
    private final AnnouncementDAO announcementDAO = new AnnouncementDAO();
    private final FriendshipDAO friendshipDAO = new FriendshipDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;

        try {
            //quizzes anyone (logged in or not) can browse//
            request.setAttribute("recentQuizzes", quizDAO.findRecent(RECENT_LIMIT));
            request.setAttribute("popularQuizzes", quizDAO.findPopular(POPULAR_LIMIT));

            //latest few announcements, newest first//
            List<AnnouncementDAO.Announcement> announcements = announcementDAO.findAll();
            if (announcements.size() > ANNOUNCEMENT_LIMIT) {
                announcements = announcements.subList(0, ANNOUNCEMENT_LIMIT);
            }
            request.setAttribute("announcements", announcements);

            if (user != null) {
                //quizzes this user created, so they can jump back into them//
                request.setAttribute("myQuizzes", quizDAO.findByCreator(user.getId()));

                //friend feed: resolve each accepted friendship to the other user's name//
                List<Friendship> accepted = friendshipDAO.findAccepted(user.getId());
                Map<Long, String> friendNames = new LinkedHashMap<>();
                for (Friendship f : accepted) {
                    long otherId = f.getUserId() == user.getId() ? f.getFriendId() : f.getUserId();
                    User friend = userDAO.findById(otherId);
                    friendNames.put(otherId, friend != null ? friend.getUsername() : "unknown");
                }
                request.setAttribute("friendNames", friendNames);

                //pending friend requests waiting on this user's response//
                request.setAttribute("pendingRequestCount",
                        friendshipDAO.findPendingReceived(user.getId()).size());
            }

            request.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Failed to load homepage", e);
        }
    }
}

package com.quizwebsite.servlet;

import com.quizwebsite.dao.AnnouncementDAO;
import com.quizwebsite.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/create-announcement")
public class CreateAnnouncementServlet extends HttpServlet {
    private AnnouncementDAO announcementDAO = new AnnouncementDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // show announcement creation form
        request.getRequestDispatcher("/WEB-INF/jsp/createAnnouncement.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        User user = (User) session.getAttribute("user");
        String body = request.getParameter("body");

        // do not save empty announcement
        if (body == null || body.trim().isEmpty()) {
            request.setAttribute("error", "Announcement cannot be empty.");
            request.getRequestDispatcher("/WEB-INF/jsp/createAnnouncement.jsp")
                    .forward(request, response);
            return;
        }

        try {
            // save announcement in database
            announcementDAO.insert(user.getId(), body.trim());

            // go back to announcements page
            response.sendRedirect(request.getContextPath() + "/announcements");

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

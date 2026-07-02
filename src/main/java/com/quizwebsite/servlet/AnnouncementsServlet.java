package com.quizwebsite.servlet;

import com.quizwebsite.dao.AnnouncementDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/announcements")
public class AnnouncementsServlet extends HttpServlet {

    private AnnouncementDAO announcementDAO = new AnnouncementDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // load announcements and send them to the JSP page
            request.setAttribute("announcements", announcementDAO.findAll());

            // show announcements page
            request.getRequestDispatcher("/WEB-INF/jsp/announcements.jsp")
                    .forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

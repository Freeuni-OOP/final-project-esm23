package com.quizwebsite.servlet;

import com.quizwebsite.model.User;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

// same as AuthFilter.java but for ADMIN checks.

@WebFilter(urlPatterns = {
				"/AdminDashboardServlet",
				"/AdminRemoveUserServlet",
				"/AdminRemoveQuizServlet",
				"/AdminClearHistoryServlet",
				"/AdminPromoteUserServlet",
				"/AdminStatsServlet",
				"/AdminDeleteAnnouncementServlet",
				"/create-annoucement"
})
public class AdminFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
					throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		HttpSession session = request.getSession(false);
		User user = session != null ? (User) session.getAttribute("user") : null;

		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		}
		if(!user.isAdmin()) {
			response.sendRedirect(request.getContextPath() + "/HomeServlet");
			return;
		}

		chain.doFilter(req, res);
	}
}

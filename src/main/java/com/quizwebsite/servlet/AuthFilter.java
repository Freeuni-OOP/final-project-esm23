package com.quizwebsite.servlet;

import com.quizwebsite.model.User;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

// this enables a filter for the urls, instead of filtering and checking each session in different servlets.

// Gatekeeper for servlets that require a logged-in user (quiz creation/taking).
// Replaces the per-servlet "if (user == null) redirect to login.jsp" checks that
// re-implementing the check in the servlet itself.


@WebFilter(urlPatterns = {"/CreateQuizServlet", "/AddQuestionServlet", "/TakeQuizServlet", "/ViewQuizServlet"})
public class AuthFilter implements Filter {

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
		chain.doFilter(req, res);
	}
}

package com.quizwebsite.servlet;

import com.quizwebsite.model.User;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

// Gatekeeper filter for endpoints that require authentication.
// Checks every request to these servlets and redirects to login if no user session exists.
// This centralizes the auth check instead of repeating it in every servlet.
// (The individual servlets also do a safety check, which is fine as defense-in-depth.)

@WebFilter(urlPatterns = {"/CreateQuizServlet", "/AddQuestionServlet", "/TakeQuizServlet",
		"/ReviewQuizServlet", "/FriendsServlet",
		"/SendFriendRequestServlet", "/RespondFriendRequestServlet"})
public class AuthFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
					throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		// Try to get the session; false means don't create a new one if none exists
		HttpSession session = request.getSession(false);
		User user = session != null ? (User) session.getAttribute("user") : null;

		// If no logged-in user, redirect to login page
		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		}
		// Otherwise, allow the request to proceed to the servlet
		chain.doFilter(req, res);
	}
}

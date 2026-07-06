package com.quizwebsite.servlet;

import com.quizwebsite.authModule.AuthResult;
import com.quizwebsite.authModule.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

	private final AuthService authService = new AuthService();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

		String username = request.getParameter("username");
		String password = request.getParameter("password");

		AuthResult result = authService.login(username, password);

		if (!result.isSuccess()) {
			request.setAttribute("error", result.getMessage());
			request.getRequestDispatcher("login.jsp").forward(request, response);
			return;
		}

		HttpSession session = request.getSession();
		session.setAttribute("user", result.getUser());
		response.sendRedirect("index.jsp");
	}
}
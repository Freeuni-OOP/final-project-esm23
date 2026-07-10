package com.quizwebsite.servlet;

import com.quizwebsite.authModule.AuthResult;
import com.quizwebsite.authModule.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

	private final AuthService authService = new AuthService();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

		String username = request.getParameter("username");
		String password = request.getParameter("password");

		AuthResult result = authService.register(username, password);

		if (!result.isSuccess()) {
			request.setAttribute("error", result.getMessage());
			request.setAttribute("username", username);
			request.getRequestDispatcher("register.jsp").forward(request, response);
			return;
		}

		request.setAttribute("registered", true);
		request.getRequestDispatcher("login.jsp").forward(request, response);
	}
}
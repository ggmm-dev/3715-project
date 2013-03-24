package com.kittens.controller;

import com.kittens.Controller;
import com.kittens.database.User;
import com.kittens.Utils;

import java.io.IOException;
import java.lang.String;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

public class UsersController extends Controller {

	// Java complains without this
	public static final long serialVersionUID = 42;

	/**
	 * Try to create a user from the parameters given.
	 */
	private User getUserFromRequest(HttpServletRequest request) {
		String email    = request.getParameter("email");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		if (email == null || username == null || password == null) {
			// error
			return null;
		}
		return new User(username, email, password, /* is admin */ false);
	}
	/**
	 * Register the user.
	 */
	private void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final User user = getUserFromRequest(request);
		try {
			if (user == null) {
				// the form was not filled out properly
				response.sendRedirect("/");
				return;
			}
			else if (database.usernameAlreadyExists(user.getUsername())) {
				// the requested username already exists
				response.sendRedirect("/");
				return;
			}
			else if (database.emailAlreadyExists(user.getEmail())) {
				// the email address has already been used
				response.sendRedirect("/");
				return;
			}
			database.createUser(user);
		}
		catch (SQLException sqle) {
			sqle.printStackTrace();
			return;
		}
		// allow access
		login(request, response, user);
	}
	/**
	 * Handle logging in the user.
	 */
	private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// login
	}
	/**
	 * Handle logging in the user without any checks.
	 */
	private void login(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException, IOException {
		HttpSession session = request.getSession();
		session.setAttribute(Utils.CURRENT_SESSION_USER, user);
		response.sendRedirect("/projects");
	}
	/**
	 * Handle logging out the user.
	 */
	private void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Utils.pleaseDontCache(response);
		// invalidate our current session
		request.getSession().invalidate();
		response.sendRedirect("/");
	}
	/**
	 * Handle POST requests.
	 */
	@Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String requestUri = request.getRequestURI().substring(1);
		if (database == null) {
			// serious database issues
			response.sendError(500);
			return;
		}
		else if (requestUri.endsWith("register")) { register(request, response); }
		else if (requestUri.endsWith("login"))    { login(request, response); }
		else if (requestUri.endsWith("logout"))   { logout(request, response); }
	}

}

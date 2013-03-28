package com.kittens.controller;

import com.google.common.base.Strings;

import com.kittens.database.User;
import com.kittens.Utils;
import com.kittens.view.ViewRenderer;

import java.io.IOException;
import java.lang.Boolean;
import java.lang.Object;
import java.lang.String;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;

public class AdminController extends BaseController {

	// Java complains without this
	public static final long serialVersionUID = 42;

	/**
	 * Displays the list of all the projects.
	 */
	private void displayProjects(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// ask for this response to not be cached
		Utils.pleaseDontCache(response);
		// only if the user is logged in
		final User currentSessionUser = Utils.getUserFromRequest(request);
		if (currentSessionUser == null || !currentSessionUser.isAdmin()) {
			response.sendRedirect(Utils.ADMIN_ROOT);
			return;
		}
		// set some values
		final HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("title", "Group Data - All Projects");
		values.put("logo", "Group Data");
		// render the view
		response.setContentType("text/html");
		ViewRenderer.render(response, "admin/projects", values);
	}
	/**
	 * Displays the list of all the users.
	 */
	private void displayUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// ask for this response to not be cached
		Utils.pleaseDontCache(response);
		// only if the user is logged in
		final User currentSessionUser = Utils.getUserFromRequest(request);
		if (currentSessionUser == null || !currentSessionUser.isAdmin()) {
			response.sendRedirect(Utils.ADMIN_ROOT);
			return;
		}
		// set some values
		final HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("title", "Group Data - All Users");
		values.put("logo", "Group Data");
		// render the view
		response.setContentType("text/html");
		ViewRenderer.render(response, "admin/users", values);
	}
	/**
	 * Shows the login page for administrators (the index page) if they are not logged in,
	 * shows the list of all users if an administrator is logged in.
	 */
	private void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// ask for this response to not be cached
		Utils.pleaseDontCache(response);
		// set some values
		final HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("title", "Group Data - Admin Login");
		values.put("logo", "Group Data");
		values.put("lerror", new Boolean(false));
		final String emessage = Utils.getErrorMessageFromRequest(request);
		if (emessage != null) {
			// there is an error message to display
			values.put("lerror", new Boolean(true));
			values.put("emessage", emessage);
			// there is likely a better way to do this
			request.getSession().invalidate();
		}
		// render the view
		response.setContentType("text/html");
		ViewRenderer.render(response, "admin/login", values);
	}
	/**
	 *
	 */
	private void logout (HttpServletRequest request, HttpServletResponse response) throws IOException {
		// no cache please
		Utils.pleaseDontCache(response);
		// invalidate our current session
		Utils.invalidateSession(request, response);
	}
	/**
	 * Handle GET requests.
	 */
	@Override public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String cmd = request.getRequestURI().substring(1);
		if (cmd.equals("admin"))               { index(request, response); }
		else if (cmd.equals("admin/projects")) { displayProjects(request, response); }
		else if (cmd.equals("admin/users"))    { displayUsers(request, response); }
		else if (cmd.equals("admin/logout")) { logout(request, response); }
		// a lot more urls are mapped to this controller
		else { response.sendError(HttpServletResponse.SC_NOT_FOUND); }
	}
	/**
	 * Logs the user in, given valid credentials and the user is an admin.
	 */
	private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// ask for the response to not be cached
		Utils.pleaseDontCache(response);
		// try to get credentials from form
		String email    = request.getParameter("email");
		String password = request.getParameter("password");
		User user;
		try {
			if (Strings.isNullOrEmpty(email) || Strings.isNullOrEmpty(password)) {
				throw new Exception(Utils.ErrorCode.COMPLETE_FORM);
			}
			user = database.getUserWithCredentials(email, password);
			if (user == null || !user.isAdmin()) {
				throw new Exception(Utils.ErrorCode.INVALID_CREDENTIALS);
			}
		}
		catch (Exception e) {
			// e.printStackTrace()
			if (e instanceof SQLException) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			else {
				request.getSession().setAttribute(Utils.ErrorCode.ERROR_MSG, e.getMessage());
				response.sendRedirect(Utils.ADMIN_ROOT);
			}
			return;
		}
		// log this admin in
		// get the session
		HttpSession session = request.getSession();
		// attach the user to the session
		session.setAttribute(Utils.CURRENT_SESSION_USER, user);
		// show them their projects
		response.sendRedirect(Utils.ADMIN_ROOT + "/projects");
	}
	/**
	 * Handle POST requests.
	 * Can/should be one of two commands (paths) - login or logout.
	 */
	@Override public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String cmd = request.getRequestURI().substring(1);
		if (cmd.equals("admin/login")) { login(request, response); }
		// moar URLs are mapped to this controller that do not
		// and should not be handled via POST
		else { response.sendError(HttpServletResponse.SC_NOT_FOUND); }
	}

}

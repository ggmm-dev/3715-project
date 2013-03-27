package com.kittens.controller;

import com.kittens.Controller;
import com.kittens.Utils;
import com.kittens.view.ViewRenderer;

import java.io.IOException;
import java.lang.Boolean;
import java.lang.String;
import java.lang.Object;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

public class AdminController extends Controller {

	// Java complains without this
	public static final long serialVersionUID = 42;

	/**
	 * Displays the list of all the projects.
	 */
	private void displayProjects(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// ask for this response to not be cached
		Utils.pleaseDontCache(response);
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
	 * Handle GET requests.
	 */
	@Override public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String cmd = request.getRequestURI().substring(1);
		if (cmd.equals("admin"))               { index(request, response); }
		else if (cmd.equals("admin/projects")) { displayProjects(request, response); }
		else if (cmd.equals("admin/users"))    { displayUsers(request, response); }
		else { response.sendError(HttpServletResponse.SC_NOT_FOUND); }
	}

}

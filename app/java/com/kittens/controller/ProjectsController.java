package com.kittens.controller;

import com.kittens.Controller;
import com.kittens.database.User;
import com.kittens.Utils;
import com.kittens.view.ViewRenderer;

import java.io.IOException;
import java.lang.String;
import java.lang.Object;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

public class ProjectsController extends Controller {

	// Java complains without this
	public static final long serialVersionUID = 42;

	/**
	 * Returns the user from the current session.
	 */
	private User getUserFromRequest(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (User) session.getAttribute(Utils.CURRENT_SESSION_USER);
	}
	/**
	 * Displays all the user's projects, allowing them to edit
	 */
	private void projects(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// ask for this response to not be cached
		Utils.pleaseDontCache(response);
		// get/set some things
		final User currentSessionUser = getUserFromRequest(request);
		// render the view
		response.setContentType("text/html");
		ViewRenderer.render(response, "projects/index", new Object() {
			public String title = "All your projects";
			public User user = currentSessionUser;
		});
	}
	/**
	 * Displays all the user's projects, allowing them to edit
	 */
	private void data(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// ask for this response to not be cached
		Utils.pleaseDontCache(response);
		// get/set some things
		final User currentSessionUser = getUserFromRequest(request);
		// render the view
		response.setContentType("text/html");
		ViewRenderer.render(response, "projects/data", new Object() {
			public String title = "You are editing your data";
			public User user = currentSessionUser;
		});
	}
	/**
	 * Displays all the user's projects, allowing them to edit
	 */
	private void stats(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// ask for this response to not be cached
		Utils.pleaseDontCache(response);
		// get/set some things
		final User currentSessionUser = getUserFromRequest(request);
		// render the view
		response.setContentType("text/html");
		ViewRenderer.render(response, "projects/stats", new Object() {
			public String title = "You are viewing your stats";
			public User user = currentSessionUser;
		});
	}
	/**
	 * Handle GET requests.
	 */
	@Override public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String requestUri = request.getRequestURI().substring(1);
		if (database == null) {
			// serious database issues
			response.sendError(500);
			return;
		}
		else if (requestUri.endsWith("data")) { data(request, response); }
		else if (requestUri.endsWith("stats")) { stats(request, response); }
		else { projects(request, response); }
	}

}

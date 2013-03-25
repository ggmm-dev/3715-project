package com.kittens.controller;

import com.kittens.Controller;
import com.kittens.database.User;
import com.kittens.Utils;
import com.kittens.view.ViewRenderer;

import java.io.IOException;
import java.lang.String;
import java.lang.Object;
import java.util.HashMap;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

public class ProjectsController extends Controller {

	// Java complains without this
	public static final long serialVersionUID = 42;

	/**
	 * Displays all the user's projects, allowing them to edit
	 */
	private void projects(HttpServletRequest request, HttpServletResponse response, final User currentSessionUser) throws ServletException, IOException {
		// render the view
		response.setContentType("text/html");
		final HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("title", String.format(
			"Group Data - %s Projects",
			currentSessionUser.getUsername() + ((currentSessionUser.getUsername().endsWith("s")) ? "\'" : "\'s")
		));
		values.put("logo", "Group Data");
		values.put("empty", "You have no projects yet");
		values.put("user", currentSessionUser);
		ViewRenderer.render(response, "projects/index", values);
	}
	/**
	 * Displays all the user's projects, allowing them to edit
	 */
	private void data(HttpServletRequest request, HttpServletResponse response, final User currentSessionUser) throws ServletException, IOException {
		// render the view
		response.setContentType("text/html");
		// set some values
		final HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("title", "You are editing your data");
		values.put("user", currentSessionUser);
		ViewRenderer.render(response, "projects/data", values);
	}
	/**
	 * Displays all the user's projects, allowing them to edit
	 */
	private void stats(HttpServletRequest request, HttpServletResponse response, final User currentSessionUser) throws ServletException, IOException {
		// render the view
		response.setContentType("text/html");
		// set some values
		final HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("title", "You are reviewing your stats");
		values.put("user", currentSessionUser);
		ViewRenderer.render(response, "projects/stats", values);
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
		// ask for this response to not be cached
		Utils.pleaseDontCache(response);
		// get/set some things
		final User currentSessionUser = Utils.getUserFromRequest(request);
		if (currentSessionUser == null) {
			response.sendRedirect("/");
			return;
		}
		else if (requestUri.endsWith("data")) { data(request, response, currentSessionUser); }
		else if (requestUri.endsWith("stats")) { stats(request, response, currentSessionUser); }
		else { projects(request, response, currentSessionUser); }
	}

}

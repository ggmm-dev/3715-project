package com.kittens.controller;

import com.kittens.database.Dataset;
import com.kittens.database.User;
import com.kittens.Utils;
import com.kittens.view.ViewRenderer;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.io.IOException;
import java.io.Writer;
import java.lang.Object;
import java.lang.String;
import java.util.HashMap;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;

public class ProjectsController extends BaseController {

	// the version of this object
	public static final long serialVersionUID = 0L;
	// the values to be outputted
	public static final HashMap<String, Object> values = new HashMap<String, Object>();

	/**
	 * Returns the possessive form of the given user's name.
	 */
	private String possessive(final User user) {
		return user.getUsername() + ((user.getUsername().endsWith("s")) ? "\'" : "\'s");
	}
	/**
	 * Handle GET requests.
	 */
	@Override public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String requestUri = request.getRequestURI().substring(1);
		if (database == null) {
			// serious database issues
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		// ask for this response to not be cached
		Utils.pleaseDontCache(response);
		// get/set some things
		final User currentSessionUser = Utils.getUserFromRequest(request);
		if (currentSessionUser == null) {
			response.sendRedirect(Utils.APP_ROOT);
			return;
		}
		response.setContentType("text/html");
		values.put("title", String.format("Group Data - %s Projects", possessive(currentSessionUser)));
		values.put("logo", "Group Data");
		values.put("user", currentSessionUser);
		values.put("datasets", database.getDatasetsForUser(currentSessionUser));
		values.put("manage", new Mustache.Lambda() {
			@Override public void execute(Template.Fragment fragment, Writer out) throws IOException {
				try {
					if (currentSessionUser.equals(database.getDataset(fragment.execute()).getOwner())) {
						out.write("manage");
					}
				} catch (SQLException e) {/* */}
			}
		});
		if (requestUri.endsWith("data"))
			ViewRenderer.render(response, "projects/data", values);
		else if (requestUri.endsWith("stats"))
			ViewRenderer.render(response, "projects/stats", values);
		else
			ViewRenderer.render(response, "projects/index", values);
	}

}

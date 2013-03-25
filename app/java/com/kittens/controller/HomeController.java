package com.kittens.controller;

import com.kittens.Controller;
import com.kittens.database.User;
import com.kittens.Utils;
import com.kittens.view.ViewRenderer;

import java.io.IOException;
import java.lang.Boolean;
import java.lang.Object;
import java.lang.String;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

public class HomeController extends Controller {

	// Java complains without this
	public static final long serialVersionUID = 42;

	/**
	 * Process GET requests.
	 */
	@Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// ask for this response to not be cached
		Utils.pleaseDontCache(response);
		// get/set some things
		final User currentSessionUser = Utils.getUserFromRequest(request);
		if (currentSessionUser != null) {
			// a user is logged in
			response.sendRedirect("/projects");
			return;
		}
		// set some values
		final HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("title", "Group Data - Collaborate on group projects");
		values.put("logo", "Group Data");
		values.put("lerror", new Boolean(false));
		values.put("rerror", new Boolean(false));
		values.put("about", "Data management made simple");
		values.put("tagline", "Collaborate with groups and report on results");
		final String emessage = Utils.getErrorMessageFromRequest(request);
		if (emessage != null) {
			// there is an error message to display
			if (emessage.equals(Utils.ErrorCode.INVALID_CREDENTIALS)) {
				// previously provided invalid creds
				values.put("lerror", new Boolean(true));
			}
			else {
				// other errors are registration related
				values.put("rerror", new Boolean(true));
			}
			values.put("emessage", emessage);
			// there is likely a better way to do this
			request.getSession().invalidate();
		}
		// render the view
		response.setContentType("text/html");
		ViewRenderer.render(response, "index", values);
	}

}

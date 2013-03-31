package com.kittens.controller.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import com.kittens.database.ApplicationDatabase;
import com.kittens.database.User;
import com.kittens.Utils;

import java.io.IOException;
import java.lang.String;
import java.sql.SQLException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

class BaseAPIController extends HttpServlet {

	// the version of this object
	public static final long serialVersionUID = 0L;
	// the app's database
	protected ApplicationDatabase database = null;
	// Google's JSON serializer/deserializer
	protected final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
	// JSON serilaizers/deserializers
	protected final JsonParser parser = new JsonParser();

	/**
	 * Returns the user from the request, checks to ensure authorization,
	 * and sends an error in the case of no user/auth.
	 */
	protected User getUserOrSendError(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (database == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
		final User user = Utils.getUserFromRequest(request);
		if (user == null) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		return user;
	}
	/**
	 * Some initialization stuff.
	 */
	@Override public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// the root of the webapp context
		String webPath = config.getServletContext().getRealPath(Utils.APP_ROOT);
		try { database = new ApplicationDatabase(webPath); }
		// print stack trace
		catch (SQLException e) { e.printStackTrace(); return; }
	}

}

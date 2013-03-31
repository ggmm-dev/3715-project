package com.kittens.controller.api;

import com.kittens.database.User;
import com.kittens.Utils;

import java.io.IOException;
import java.lang.Boolean;
import java.lang.Object;
import java.lang.String;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

public class AjaxUsersAPIController extends BaseAPIController {

	// the version of this object
	private static final long serialVersionUID = 0L;

	/**
	 * Handle PUT requests.
	 */
	@Override public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User currentSessionUser = getUserOrSendError(request, response);
		if (currentSessionUser == null) return;
		String json = Utils.readStream(request.getInputStream());
		String[] data = gson.fromJson(json, String[].class);
		try {
			if (data.length != 2 || !database.emailInDatabase(data[1])) {
				gson.toJson(new Boolean(false), response.getWriter());
				return;
			}
			User newCollaborator = database.getUserForEmail(data[1]);
			if (newCollaborator.getUUID().equals(currentSessionUser.getUUID())) {
				// the user is trying to add themself
				gson.toJson(new Boolean(false), response.getWriter());
				return;
			}
			database.addCollaborator(newCollaborator.getUUID(), data[0]);
			gson.toJson(newCollaborator, response.getWriter());
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

package com.kittens.controller.api;

import com.kittens.database.User;
import com.kittens.Utils;

import java.io.IOException;
import java.lang.Boolean;
import java.lang.String;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

public class AjaxUsersAPIController extends BaseAPIController {

	// the version of this object
	private static final long serialVersionUID = 0L;

	/**
	 * Handle DELETE requests.
	 * Deletes the given user.
	 */
	@Override public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User currentSessionUser = getUserOrSendError(request, response);
		if (currentSessionUser == null) return;
		final String json = Utils.readStream(request.getInputStream());
		String[] uuids = gson.fromJson(json, String[].class);
		ArrayList<String> deleted = new ArrayList<String>();
		for (String uuid : uuids) {
			try {
				// delete user
				database.deleteUser(uuid);
				deleted.add(uuid);
			}
			catch (SQLException e) { e.printStackTrace(); }
		}
		response.setContentType("application/json");
		gson.toJson(deleted, response.getWriter());
	}
	/**
	 * Handle PUT requests.
	 * Makes the given user an administrator.
	 */
	@Override public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User currentSessionUser = getUserOrSendError(request, response);
		if (currentSessionUser == null) return;
		final String json = Utils.readStream(request.getInputStream());
		String[] uuids = gson.fromJson(json, String[].class);
		ArrayList<String> madeAdmin = new ArrayList<String>();
		for (String uuid : uuids) {
			try {
				// make admin
				database.makeAdmin(uuid);
				madeAdmin.add(uuid);
			}
			catch (SQLException e) { e.printStackTrace(); }
		}
		response.setContentType("application/json");
		gson.toJson(madeAdmin, response.getWriter());
	}

}

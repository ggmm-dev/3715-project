package com.kittens.controller;

import com.kittens.Controller;
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
	 * Handle GET requests.
	 */
	@Override public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// render the view
		response.setContentType("text/html");
		// set some values
		final HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("title", "Group Data - Admin Login");
		values.put("logo", "Group Data");
		values.put("lerror", new Boolean(false));
		// render the view
		response.setContentType("text/html");
		ViewRenderer.render(response, "admin/login", values);
	}
	/**
	 *
	 */
	@Override public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write("Works.");
	}

}

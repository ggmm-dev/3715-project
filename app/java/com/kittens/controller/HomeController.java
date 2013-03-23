package com.kittens.controller;

import com.kittens.Controller;
import com.kittens.util.Utils;
import com.kittens.view.ViewRenderer;

import java.io.IOException;
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
		// set some values
		final HashMap<String, String> values = new HashMap<String, String>();
		values.put("title", "Project awesome!");
		// render the view
		response.setContentType("text/html");
		ViewRenderer.render(response, "index", values);
	}

}

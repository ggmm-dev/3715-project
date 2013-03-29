package com.kittens.controller;

import com.kittens.database.ApplicationDatabase;
import com.kittens.Utils;

import java.lang.String;
import java.sql.SQLException;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

class BaseController extends HttpServlet {

	// the version of this object
	public static final long serialVersionUID = 0L;
	// the app's database
	protected ApplicationDatabase database = null;

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

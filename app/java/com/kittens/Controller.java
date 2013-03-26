package com.kittens;

import com.kittens.database.ApplicationDatabase;
import com.kittens.Utils;

import java.lang.Object;
import java.lang.String;
import java.sql.SQLException;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

public class Controller extends HttpServlet {

	// Java complains without this
	public static final long serialVersionUID = 42;
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
		// print stack traces
		catch (SQLException sqle) { sqle.printStackTrace(); return; }
	}

}

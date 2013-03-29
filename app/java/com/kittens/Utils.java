package com.kittens;

import com.kittens.database.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Object;
import java.lang.String;
import java.lang.StringBuilder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Utils extends Object {

	// the newline character for all systems
	public static final String NEWLINE = "\r\n";
	// tag for session
	public static final String CURRENT_SESSION_USER = "currentUserInSession";
	// the administrator's section root
	public static final String ADMIN_ROOT = "/admin";
	// the root of the webapp
	public static final String APP_ROOT = "/";
	// a custom list of possible erro codes
	public static final class ErrorCode extends Object {

		public static final String ERROR_MSG = "emessage";
		public static final String INVALID_CREDENTIALS = "Invalid credentials";
		public static final String EMAIL_IN_USE = "Please choose a different email";
		public static final String COMPLETE_FORM = "Please fill out the form";

	}
	// a simple date formatter
	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

	/**
	 * Returns the given date in ISO format.
	 */
	public static String formatDate(Date d) {
		return formatter.format(d);
	}
	/**
	 * Ask for the response to not be cached by the client.
	 */
	public static void pleaseDontCache(HttpServletResponse res) {
		// set some headers
		res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		res.setDateHeader("Expires", 0);
		res.setHeader("Pragma", "no-cache");
	}
	/**
	 * Invalidates the session associated with the request and redirects.
	 */
	public static void invalidateSession(HttpServletRequest req, HttpServletResponse res) throws IOException {
		req.getSession().invalidate();
		res.sendRedirect(APP_ROOT);
	}
	/**
	 * Dumps the request to the user.
	 */
	public static void dumpRequest(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setContentType("text/plain");
		PrintWriter out = res.getWriter();
		out.printf("You sent a %s request.%n", req.getMethod());
		out.println(Utils.dumpRequest(req));
	}
	/**
	 * Dump the request in plain text to the response.
	 */
	public static String dumpRequest(HttpServletRequest req) {
		StringBuilder s = new StringBuilder();
		s.append("\n");
		// the keys for the header fields
		Enumeration names = req.getHeaderNames();
		Enumeration values = null;
		// add all the headers
		while (names.hasMoreElements()) {
			String header = (String) names.nextElement();
			values = req.getHeaders(header);
			while (values.hasMoreElements()) {
				s.append(header + ": " + values.nextElement() + "\n");
			}
		}
		// add all the request parameters
		Map<String, String[]> params = req.getParameterMap();
		if (params.size() > 0) {
			// a bit of extra formatting space
			s.append("\n");
		}
		for (Map.Entry<String, String[]> entry : params.entrySet()) {
			String[] vals = entry.getValue();
			String key = entry.getKey();
			for (String val : vals) s.append("" + key + ": " + val + "\n");
		}
		return s.toString();
	}
	/**
	 * Returns the user from the current session.
	 */
	public static User getUserFromRequest(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (User) session.getAttribute(CURRENT_SESSION_USER);
	}
	/**
	 * Returns the error message from the current session, null otherwise.
	 */
	public static String getErrorMessageFromRequest(HttpServletRequest request) {
		return (String) request.getSession().getAttribute(Utils.ErrorCode.ERROR_MSG);
	}
	/**
	 * Returns a random uuid as a string.
	 */
	public static String uuid() {
		return java.util.UUID.randomUUID().toString();
	}

}

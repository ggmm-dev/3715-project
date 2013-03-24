package com.kittens;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Object;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Utils extends Object {

	// tag for session
	public static final String CURRENT_SESSION_USER = "currentUserInSession";

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

}

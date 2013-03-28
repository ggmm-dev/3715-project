package com.kittens.view;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.ClassLoader;
import java.lang.Exception;
import java.lang.Object;
import java.lang.String;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

public class ViewRenderer extends Object {

	// loads partial templates
	private static final Mustache.TemplateLoader templateLoader = new Mustache.TemplateLoader() {
		// http://git.io/oFzz3w
		public Reader getTemplate(String fname) throws Exception {
			String filename = "partials" + File.separator + fname + TEMPLATE_EXT;
			return new InputStreamReader(ClassLoader.getSystemResourceAsStream(filename));
		}
	};
	// the compiler for all the templates
	private static final Mustache.Compiler mCompiler = Mustache.compiler().withLoader(templateLoader);
	// the file extension for the templates
	private static final String TEMPLATE_EXT = ".mustache.html";
	// cache the templates
	private static final HashMap<String, Template> templates = new HashMap<String, Template>();

	/**
	 * No args constructor.
	 */
	public ViewRenderer() {
		// empty
	}

	/**
	 *
	 */
	public static void render(HttpServletResponse response, String fname, Object obj) throws IOException {
		render(response.getWriter(), fname, obj);
	}
	/**
	 * Generate the template from the given file and print it out.
	 */
	public static void render(PrintWriter out, String fname, Object obj) {
		// store the compiled template if it doesn't already exist
		// if it already exists, use the precompiled template
		final Template t;
		if (templates.containsKey(fname)) {
			// use precompiled version
			t = templates.get(fname);
		}
		else {
			// compile
			t = mCompiler.compile(new InputStreamReader(ClassLoader.getSystemResourceAsStream(fname + TEMPLATE_EXT)));
			// store
			templates.put(fname, t);
		}
		// execute the template with
		// the given object's fields
		out.print(t.execute(obj));
	}

}

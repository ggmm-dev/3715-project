package com.kittens;

import com.kittens.Utils;

import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.String;
import java.lang.System;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServer extends Object {

	/**
	 * Run the server.
	 */
	public static void main(String[] args) throws Exception {
		final int argc = 3;
		if (args.length != argc) {
			// not enough arguments
			System.out.println("Moar arguments!");
			System.exit(argc);
		}

		SelectChannelConnector conn = new SelectChannelConnector();
		conn.setPort(Integer.parseInt(args[0]));
		Connector[] conns = { conn };
		Server server = new Server();
		server.setConnectors(conns);

		server.setHandler(new WebAppContext(/* wardir */ args[1], /* path */ args[2]));
		int timeoutMS = 2000;
		server.setGracefulShutdown(timeoutMS);
		server.start(); // throws Exception
		server.join();  // throws java.lang.InterruptedException
	}

}

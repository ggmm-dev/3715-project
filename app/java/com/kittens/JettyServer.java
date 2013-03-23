package com.kittens;

import java.lang.Integer;
import java.lang.Object;
import java.lang.String;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServer extends Object {

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			// not enough arguments
			System.out.println("You done messed up.");
			System.exit(3);
		}

		String wardir = args[1];
		String path = args[2];

		SelectChannelConnector conn = new SelectChannelConnector();
		conn.setPort(Integer.parseInt(args[0]));
		Connector[] conns = { conn };
		Server server = new Server();
		server.setConnectors(conns);

		server.setHandler(new WebAppContext(wardir, path));
		server.setGracefulShutdown(2000);
		server.start();
		server.join();
	}

}

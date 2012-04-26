package com.taskadapter.launcher;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class TALauncher {

	private static final int DEFAULT_HTTP_SERVER_PORT = 8080;
	private static final String WEB_APPLICATION_ROOT_CONTEXT = "ta";
	private static final String WAR_FILE = "files/ta.war";

	public static void main(String[] args) {

        int portNumber = findPortNumberInArgs(args);
        System.out.println("Starting HTTP server on port " + portNumber);

		final Server server = new Server(portNumber);
		server.setHandler(new WebAppContext(WAR_FILE, "/" + WEB_APPLICATION_ROOT_CONTEXT));

		try {
			server.start();
			while (!server.isStarted() || !server.getHandler().isStarted()) {
				try {
					Thread.sleep(500);
				} catch (Exception e) {
					//logger.error(e);
				}
			}
		} catch (Exception e) {
            System.out.println("Error starting server: " + e.toString());
		}
	}

    static int findPortNumberInArgs(String[] args) {
        for (String arg : args) {
            String prefix = "--port=";
            if (arg.startsWith(prefix)) {
                String portString = arg.substring(prefix.length());
                return Integer.parseInt(portString);
            }
        }
        return DEFAULT_HTTP_SERVER_PORT;
    }

}

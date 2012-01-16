package com.taskadapter.launcher;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;


public class TALauncher {
	

	private static final int SERVER_PORT = 8080;
	private static String CONTEXT = "ta";
	private static String WAR_FILE = "ta.war";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String caminhoApp = "files/" + WAR_FILE ;
		
		final Server server = new Server(SERVER_PORT);
		server.setHandler(new WebAppContext(caminhoApp, "/" + CONTEXT ));

		try {
			//logger.info("Iniciando container...");
			server.start();
			while (!server.isStarted() || !server.getHandler().isStarted()) {
				try {
					Thread.sleep(500);
				} catch (Exception e) {
					//logger.error(e);
				}
			}
		} catch (Exception e) {
			//logger.fatal("Erro ao iniciar aplicação web.", e);
		}
		

	}

}

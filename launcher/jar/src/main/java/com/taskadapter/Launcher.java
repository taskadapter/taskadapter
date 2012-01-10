package com.taskadapter;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class Launcher {
    public static void main(String[] args) throws Exception {
        try {
            Server server = new Server();
            Connector connector = new SelectChannelConnector();
            connector.setPort(8080);
            server.addConnector(connector);

            // WAR
            URL url = Launcher.class.getClassLoader().getResource("com/taskadapter/Launcher.class");
            File warFile = new File(((JarURLConnection) url.openConnection()).getJarFile().getName());

            WebAppContext context = new WebAppContext(warFile.getAbsolutePath(), "/launcher");

            context.setConfigurationClasses(new String[]{
                    "org.mortbay.jetty.webapp.WebInfConfiguration",
                    "org.mortbay.jetty.plus.webapp.EnvConfiguration",
                    "org.mortbay.jetty.plus.webapp.Configuration",
                    "org.mortbay.jetty.webapp.JettyWebXmlConfiguration",
                    "org.mortbay.jetty.webapp.TagLibConfiguration"
                });

            server.setHandler(context);


            server.start();
            server.join();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

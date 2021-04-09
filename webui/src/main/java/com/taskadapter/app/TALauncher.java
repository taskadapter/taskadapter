package com.taskadapter.app;

import com.taskadapter.webui.config.ApplicationSettings;
import com.taskadapter.webui.service.EditorManager;
import com.taskadapter.webui.service.Preservices;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.startup.ServletContextListeners;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;

/**
 * Run {@link #main(String[])} to launch your app in Embedded Jetty.
 */
public final class TALauncher {
    private static final Logger logger = LoggerFactory.getLogger(TALauncher.class);

    static final String PARAMETER_OPEN_TASK_ADAPTER_PAGE_IN_WEB_BROWSER = "--openTaskAdapterPageInWebBrowser";

    private static final int DEFAULT_HTTP_SERVER_PORT = 10842;
    private static final String WEB_APPLICATION_ROOT_CONTEXT = "/";

    private static Server server;

    public static void main(String[] args) throws Exception {
        start(args);
        server.join();
    }

    public static void start(String[] args) throws Exception {
        int portNumber = findPortNumberInArgs(args);
        System.out.println("Starting HTTP server on port " + portNumber);

        // detect&enable production mode
        if (isProductionMode()) {
            // fixes https://github.com/mvysny/vaadin14-embedded-jetty/issues/1
            System.out.println("Production mode detected, enforcing");
            System.setProperty("vaadin.productionMode", "true");
        }

        WebAppContext context = new WebAppContext();
        Resource webRoot = findWebRoot();
        context.setBaseResource(webRoot);
        context.setContextPath(WEB_APPLICATION_ROOT_CONTEXT);
        context.addServlet(VaadinServlet.class, "/*");
        context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*\\.jar|.*/classes/.*");
        context.setConfigurationDiscovered(true);
        context.getServletContext().setExtendedListenerTypes(true);
        context.addEventListener(new ServletContextListeners());
        context.setThrowUnavailableOnStartupException(true);
        // fixes IllegalStateException: Unable to configure jsr356 at that stage. ServerContainer is null
        WebSocketServerContainerInitializer.initialize(context);

        server = new Server(portNumber);
        server.setHandler(context);
        Configuration.ClassList classlist = Configuration.ClassList.setServerDefault(server);
        classlist.addBefore(JettyWebXmlConfiguration.class.getName(), AnnotationConfiguration.class.getName());

        server.start();

        String uri = "http://localhost:" + portNumber + WEB_APPLICATION_ROOT_CONTEXT;
        System.out.println("=======================================================================");
        System.out.println("Task Adapter is started as a WEB-server running on port " + portNumber);
        System.out.println("Please OPEN your web browser with this URL: " + uri);
        System.out.println("=======================================================================");

        checkLicense();

        if (needToOpenBrowser(args)) {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(uri));
        } else {
            System.out.println("Task Adapter launcher will open the browser automatically if you provide this parameter" +
                    " to the start script: " + PARAMETER_OPEN_TASK_ADAPTER_PAGE_IN_WEB_BROWSER);
        }
    }

    public static void stop() throws Exception {
        server.stop();
        server = null;
    }

    private static boolean isProductionMode() {
        final String probe = "META-INF/maven/com.vaadin/flow-server-production-mode/pom.xml";
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResource(probe) != null;
    }

    private static Resource findWebRoot() throws MalformedURLException {
        // don't look up directory as a resource, it's unreliable: https://github.com/eclipse/jetty.project/issues/4173#issuecomment-539769734
        // instead we'll look up the /webapp/ROOT and retrieve the parent folder from that.
        final URL f = TALauncher.class.getResource("/webapp/ROOT");
        if (f == null) {
            throw new IllegalStateException("Invalid state: the resource /webapp/ROOT doesn't exist, has webapp been packaged in as a resource?");
        }
        final String url = f.toString();
        if (!url.endsWith("/ROOT")) {
            throw new RuntimeException("Parameter url: invalid value " + url + ": doesn't end with /ROOT");
        }
        System.err.println("/webapp/ROOT is " + f);

        // Resolve file to directory
        URL webRoot = new URL(url.substring(0, url.length() - 5));
        System.err.println("WebRoot is " + webRoot);
        return Resource.newResource(webRoot);
    }

    private static void checkLicense() {
        // Application config root folder.
        var rootFolder = ApplicationSettings.getDefaultRootFolder();

        var services = new Preservices(rootFolder, EditorManager.fromResource("editors.txt"));

        if (services.licenseManager.isSomeValidLicenseInstalled()) {
            var license = services.licenseManager.getLicense();
            logger.info("License info: valid until " + license.getExpiresOn() + ". Registered to " + license.getEmail());
        } else {
            logger.info("License NOT installed or is NOT valid. Trial mode.");
        }

        logger.info("Started TaskAdapter " + services.currentTaskAdapterVersion);
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

    static boolean needToOpenBrowser(String[] args) {
        return Arrays.stream(args).anyMatch(a -> a.equals(PARAMETER_OPEN_TASK_ADAPTER_PAGE_IN_WEB_BROWSER));
    }
}

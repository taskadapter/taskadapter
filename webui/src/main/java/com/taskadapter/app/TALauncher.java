package com.taskadapter.app;

import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.startup.ServletContextListeners;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

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
    static final String PARAMETER_OPEN_TASK_ADAPTER_PAGE_IN_WEB_BROWSER = "--openTaskAdapterPageInWebBrowser";
    private static final String PARAMETER_APPLY_HACK_FOR_SCALA_DEV = "--applyHackForScalaDev";

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

        if (needToApplyScalaFolderHackForLocalDevRuns(args)) {
            applyHackForJavaInScalaFolder(webRoot);
        }
        server.start();

        String uri = "http://localhost:" + portNumber + WEB_APPLICATION_ROOT_CONTEXT;
        System.out.println("=======================================================================");
        System.out.println("Task Adapter is started as a WEB-server running on port " + portNumber);
        System.out.println("Please OPEN your web browser with this URL: " + uri);
        System.out.println("=======================================================================");

        if (needToOpenBrowser(args)) {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(uri));
        } else {
            System.out.println("Task Adapter launcher will open the browser automatically if you provide this parameter" +
                    " to the start script: " + PARAMETER_OPEN_TASK_ADAPTER_PAGE_IN_WEB_BROWSER);
        }
    }

    /**
     * create a dummy folder to allow local dev runs from IDEA.
     *
     * unfortunately, java+scala combination leads to a conflict where the binary class files are stored in
     * build/classes/scala for both java and scala classes, but one of the Jetty layers demands something to be
     * present in build/classes/java folder. this method creates a dummy build/classes/java/main folder right
     * before the server starts, when all compilation and build is already completed
     * (because those would drop the folder again).
     * @param webRoot
     */
    private static void applyHackForJavaInScalaFolder(Resource webRoot) {
        String name = webRoot.getName();
        String requiredFolder = name.replace("resources/main/webapp", "classes/java/main");
        new File(requiredFolder).mkdir();
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

    static boolean needToApplyScalaFolderHackForLocalDevRuns(String[] args) {
        return Arrays.stream(args).anyMatch(a -> a.equals(PARAMETER_APPLY_HACK_FOR_SCALA_DEV));
    }

    static boolean needToOpenBrowser(String[] args) {
        return Arrays.stream(args).anyMatch(a -> a.equals(PARAMETER_OPEN_TASK_ADAPTER_PAGE_IN_WEB_BROWSER));
    }
}

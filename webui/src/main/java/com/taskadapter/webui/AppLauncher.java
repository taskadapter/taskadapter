package com.taskadapter.webui;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration.Dynamic;

import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinSession;

/**
 * TaskAdapter application launcher. Initializes Vaadin servlet.
 */
public final class AppLauncher implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        String webAppRealPath = sce.getServletContext().getRealPath("/");
        if (webAppRealPath.contains("/exploded/")) {
            // dev mode when running from IDEA
            TAApplicationProvider.skipGoogleAnalytics();
        } else {
            TAApplicationProvider.withGoogleAnalytics();
        }

        final Dynamic appServlet = sce.getServletContext().addServlet(
                "Task Adapter Application", VaadinServlet.class);
        appServlet.setInitParameter("widgetset",
                "com.taskadapter.webui.widgetset.Vaadin1Widgetset");
        appServlet.setInitParameter("UIProvider",
                "com.taskadapter.webui.AppUIProxy");
        appServlet.addMapping("/*");

        VaadinSession.getCurrent().setErrorHandler(new MyCustomErrorHandler());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to destroy
    }

}

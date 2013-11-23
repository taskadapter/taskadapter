package com.taskadapter.webui;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration.Dynamic;

import com.vaadin.server.VaadinServlet;

/**
 * Task-adapter application launcher. Performs all hardcore magic. Creates
 * services, register servlets, etc...
 */
public final class AppLauncher implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        AppUIProxy.instance = new TAApplicationProvider();

        final Dynamic appServlet = sce.getServletContext().addServlet(
                "Task Adapter Application", VaadinServlet.class);
        appServlet.setInitParameter("widgetset",
                "com.taskadapter.webui.widgetset.Vaadin1Widgetset");
        appServlet.setInitParameter("UIProvider",
                "com.taskadapter.webui.AppUIProxy");
        appServlet.addMapping("/*");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        /* Nothing to destroy yet! */
    }

}

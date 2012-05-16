package com.taskadapter.webui;

import com.taskadapter.web.service.Services;
import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * This is the web application entry point.
 *
 * @author Alexey Skorokhodov
 */
public class TAApplication extends Application implements HttpServletRequestListener {

    private final Window mainWindow = new Window("Task Adapter");

    private final Services services = new Services();

    @Override
    public String getVersion() {
        return services.getUpdateManager().getCurrentVersion();
    }

    @Override
    public void init() {
//        if(services.getLicenseManager().isSomeValidLicenseInstalled()) {
//            String host = getURL().getHost();
//
//            if(License.Type.SINGLE.equals(services.getLicenseManager().getLicense().getType())
//                    && !("localhost".equals(host) || "127.0.0.1".equals(host))) {
//                return;  // TODO: some warning page should be implemented
//            }
//        }

        setTheme("mytheme");

        VerticalLayout layout = new VerticalLayout();
        layout.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        mainWindow.setContent(layout);
        setMainWindow(mainWindow);
        services.getAuthenticator().init();

        Navigator navigator = new Navigator(layout, services);
        navigator.show(Navigator.HOME);
    }

    @Override
    public void onRequestStart(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        services.getCookiesManager().init(httpServletRequest, httpServletResponse);
        services.getSessionInfo().setRequestCameFromLocalhost(isRequestFromLocalhost(httpServletRequest));
    }

    @Override
    public void onRequestEnd(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
    }

    private boolean isRequestFromLocalhost(HttpServletRequest request) {
        return ("127.0.0.1".equals(request.getRemoteHost())) || ("localhost".equals(request.getServerName()));
    }
}

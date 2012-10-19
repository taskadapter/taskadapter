package com.taskadapter.webui;

import com.taskadapter.connector.testlib.FileBasedTest;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.service.EditorManager;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.service.UserNotFoundException;
import com.taskadapter.web.service.WrongPasswordException;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class NavigatorTest extends FileBasedTest {
    @Test
    public void feedbackPageIsShownWithoutLogin() {
        Services services = getServices();
        Navigator navigator = getNavigator(services);
        navigator.show(new SupportPage("1.1", new LicenseManager()));
        assertEquals("support", navigator.getCurrentPage().getPageGoogleAnalyticsID());
    }

    @Test
    public void homePageRedirectsToLoginIfNotLoggedIn() {
        Navigator navigator = getNavigator();
        navigator.show(new ConfigsPage());
        assertEquals("login", navigator.getCurrentPage().getPageGoogleAnalyticsID());
    }

    @Test
    public void homePageIsShownIfLoggedIn() throws UserNotFoundException, WrongPasswordException {
        Services services = getServices();
        services.getAuthenticator().tryLogin("admin", "admin", false);
        Navigator navigator = getNavigator(services);
        ConfigsPage home = new ConfigsPage();
        navigator.show(home);
        assertEquals(home.getPageGoogleAnalyticsID(), navigator.getCurrentPage().getPageGoogleAnalyticsID());
    }

    private Navigator getNavigator() {
        return getNavigator(getServices());
    }

    private Navigator getNavigator(Services services) {
        Window window = new Window("Task Adapter");
        VerticalLayout layout = new VerticalLayout();
        window.setContent(layout);
        return new Navigator(layout, services);
    }

    private Services getServices() {
        return new Services(tempFolder, new EditorManager(Collections.<String,PluginEditorFactory<?>>emptyMap()));
    }

}

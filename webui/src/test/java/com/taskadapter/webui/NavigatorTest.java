package com.taskadapter.webui;

import com.taskadapter.web.service.Services;
import com.taskadapter.web.service.UserNotFoundException;
import com.taskadapter.web.service.WrongPasswordException;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class NavigatorTest {
    @Test
    public void feedbackPageIsShownWithoutLogin() {
        Navigator navigator = getNavigator();
        navigator.show(Navigator.FEEDBACK_PAGE);
        assertEquals("support", navigator.getCurrentPage().getPageGoogleAnalyticsID());
    }

    @Test
    public void homePageRedirectsToLoginIfNotLoggedIn() {
        Navigator navigator = getNavigator();
        navigator.show(Navigator.HOME);
        assertEquals("login", navigator.getCurrentPage().getPageGoogleAnalyticsID());
    }

    @Test
    public void homePageIsShownIfLoggedIn() throws UserNotFoundException, WrongPasswordException {
        Services services = getServices();
        services.getAuthenticator().tryLogin("admin", "admin", false);
        Navigator navigator = getNavigator(services);
        navigator.show(Navigator.HOME);
        assertEquals("home", navigator.getCurrentPage().getPageGoogleAnalyticsID());
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
        File dataRootFolder = new File("tmp");
        return new Services(dataRootFolder);
    }

}

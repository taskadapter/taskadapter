package com.taskadapter.webui;

import com.taskadapter.web.MessageDialog;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;
import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;

import java.util.HashMap;
import java.util.Map;

public class Navigator {
    public static final String MAIN_WIDTH = "920px";// like GitHub

    private Map<String, Page> pages = new HashMap<String, Page>();
    private HorizontalLayout navigationPanel;
    private HorizontalLayout currentComponentArea = new HorizontalLayout();
    private Layout mainArea = new CssLayout();
    private VerticalLayout layout;
    private Services services;
    private Page previousPage;
    private Page currentPage;
    private GoogleAnalyticsTracker tracker;

    public Navigator(VerticalLayout layout, Services services) {
        this.layout = layout;
        this.services = services;
        addGoogleAnalytics();
        buildUI();
    }

    private void addGoogleAnalytics() {
        tracker = new GoogleAnalyticsTracker("UA-3768502-12", "none");
        // Add ONLY ONE tracker per window
        layout.getWindow().addComponent(tracker);
    }

    private void buildUI() {
        Header header = new Header(this, services);
        header.setHeight(50, Sizeable.UNITS_PIXELS);
        header.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        layout.addComponent(header);

        addNavigationPanel();

        // the big shadowed page on middle
        //mainArea.setStyleName(Runo.CSSLAYOUT_SHADOW);
        mainArea.setStyleName("no-shadow");
        mainArea.setWidth(MAIN_WIDTH);

        // container for currentComponentArea to be aligned in mainArea correctly
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        verticalLayout.addComponent(currentComponentArea);

        mainArea.addComponent(verticalLayout);
        verticalLayout.setComponentAlignment(currentComponentArea, Alignment.MIDDLE_CENTER);

        layout.addComponent(mainArea);
        layout.setComponentAlignment(mainArea, Alignment.MIDDLE_CENTER);
    }

    private void addNavigationPanel() {
        navigationPanel = new HorizontalLayout();
        navigationPanel.setHeight("30px");
        navigationPanel.setSpacing(true);
        layout.addComponent(navigationPanel);
        layout.setComponentAlignment(navigationPanel, Alignment.MIDDLE_CENTER);
    }

    public void registerPage(String id, Page page) {
        pages.put(id, page);
    }

    public void show(String pageId) {
        Page page = pages.get(pageId);
        if (page != null) {
            show(page);
        } else {
            showError("Page \"" + pageId + "\" is not registered");
        }
    }

    public void show(final Page page) {
        previousPage = currentPage;

        currentPage = page;
        if (!services.getAuthenticator().isLoggedIn() && requiresLogin(page)) {
            currentPage = new LoginPage();
        }

        if (services.getAuthenticator().isLoggedIn()
                && services.getAuthenticator().getUserName().equals("admin")
                && !services.getSettingsManager().isLicenseAgreementAccepted()) {
            currentPage = new LicenseAgreementPage();
        }

        setServicesToPage(currentPage);

        currentComponentArea.removeAllComponents();
        Component ui = currentPage.getUI();
        ui.setSizeUndefined();
        currentComponentArea.addComponent(ui);
        currentComponentArea.setComponentAlignment(ui, Alignment.TOP_LEFT);

        navigationPanel.removeAllComponents();

        tracker.trackPageview("/" + currentPage.getPageGoogleAnalyticsID());
    }

    private boolean requiresLogin(Page page) {
        // TODO refactor!
        return ! "support".equals(page.getPageGoogleAnalyticsID());
    }

    private void setServicesToPage(Page page) {
        page.setNavigator(this);
        page.setServices(services);
    }

    // TODO these 5 showXX methods are not in line with the other show(). refactor!
    public void showConfigureTaskPage(UISyncConfig config) {
        showConfigureTaskPage(config, null);
    }

    public void showConfigureTaskPage(UISyncConfig config, String errorMessage) {
        EditConfigPage page = new EditConfigPage();
        page.setServices(services);
        page.setConfig(config);
        page.setErrorMessage(errorMessage);

        show(page);
    }

    public void showError(String message) {
        showNotification("Internal error!", message);
    }

    public void showNotification(String caption, String message) {
        layout.getWindow().showNotification(caption, "<pre>" + message + "</pre>", Window.Notification.TYPE_HUMANIZED_MESSAGE);
    }

    public void addWindow(MessageDialog messageDialog) {
        layout.getApplication().getMainWindow().addWindow(messageDialog);
    }

    public Application getApplication() {
        return layout.getApplication();
    }

    /**
     * return to previous page
     */
    public void back() {
        if (previousPage != null) {
            show(previousPage);
        }
    }

    Page getCurrentPage() {
        return currentPage;
    }
}

package com.taskadapter.webui;

import com.taskadapter.config.TAFile;
import com.taskadapter.web.MessageDialog;
import com.taskadapter.web.service.Services;
import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;
import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;

import java.util.HashMap;
import java.util.Map;

public class Navigator {
    public static final String MAIN_WIDTH = "920px";// like GitHub

    public static final String HOME = "home";
    public static final String CONFIGURE_SYSTEM_PAGE = "configure_system";
    public static final String FEEDBACK_PAGE = "feedback";
    public static final String NEW_CONFIG_PAGE = "new_config";
    public static final String LICENSE_AGREEMENT_PAGE = "license_agreement";

    private static final String EDIT_CONFIG_PAGE = "configure_task";
    private static final String LOGIN_PAGE = "login";

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
        registerPages();
        addGoogleAnalytics();
        buildUI();
    }

    private void addGoogleAnalytics() {
        tracker = new GoogleAnalyticsTracker("UA-3768502-12", "none");
        // Add ONLY ONE tracker per window
        layout.getWindow().addComponent(tracker);
    }

    private void registerPages() {
        ConfigsPage configsPage = new ConfigsPage();
        registerPage(LOGIN_PAGE, new LoginPage());
        registerPage(HOME, configsPage);
        registerPage(CONFIGURE_SYSTEM_PAGE, new ConfigureSystemPage());
        registerPage(FEEDBACK_PAGE, new SupportPage(services.getUpdateManager()));
        registerPage(NEW_CONFIG_PAGE, new NewConfigPage());
        registerPage(EDIT_CONFIG_PAGE, new EditConfigPage());
        registerPage(LICENSE_AGREEMENT_PAGE, new LicenseAgreementPage());
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
            currentPage = pages.get(LOGIN_PAGE);
        }

        if (services.getAuthenticator().isLoggedIn()
                && services.getAuthenticator().getUserName().equals("admin")
                && !services.getSettingsManager().isLicenseAgreementAccepted()) {
            currentPage = pages.get(LICENSE_AGREEMENT_PAGE);
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
    public void showConfigureTaskPage(TAFile file) {
        showConfigureTaskPage(file, null);
    }

    public void showConfigureTaskPage(TAFile file, String errorMessage) {
        EditConfigPage page = (EditConfigPage) pages.get(EDIT_CONFIG_PAGE);
        page.setFile(file);
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

    public Services getServices() {
        return services;
    }
}

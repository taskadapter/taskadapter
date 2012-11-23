package com.taskadapter.webui;

import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.web.MessageDialog;
import com.taskadapter.web.service.EditableCurrentUserInfo;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.service.WrongPasswordException;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.config.EditConfigPage;
import com.taskadapter.webui.user.ChangePasswordDialog;
import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;

public class Navigator {
    private static final String GOOGLE_ANALYTICS_ID = "UA-3768502-12";
    static final String MAIN_WIDTH = "900px";

    private HorizontalLayout navigationPanel;
    private HorizontalLayout currentComponentArea = new HorizontalLayout();
    private Layout mainArea = new CssLayout();
    private VerticalLayout layout;
    private Services services;
    private Page previousPage;
    private Page currentPage;
    private GoogleAnalyticsTracker tracker;
    private final Authenticator authenticator;
    private final CredentialsManager credentialsManager;

    public Navigator(VerticalLayout layout, Services services,
            CredentialsManager credManager) {
        this.layout = layout;
        this.services = services;
        this.credentialsManager = credManager;
        this.authenticator = new Authenticator(credManager,
                services.getCookiesManager(),
                (EditableCurrentUserInfo) services.getCurrentUserInfo());
        addGoogleAnalytics();
        buildUI();
    }

    private void addGoogleAnalytics() {
        tracker = new GoogleAnalyticsTracker(GOOGLE_ANALYTICS_ID, "none");
        // Add ONLY ONE tracker per window
        layout.getWindow().addComponent(tracker);
    }

    private void buildUI() {
        // TODO !! this is a hack
        Header header = new Header(this, services);
        header.setHeight(50, Sizeable.UNITS_PIXELS);
        header.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        layout.addComponent(header);

        addEmptyNavigationPanelUsedAsASpacerForNow();

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

    private void addEmptyNavigationPanelUsedAsASpacerForNow() {
        navigationPanel = new HorizontalLayout();
        navigationPanel.setHeight(30, Sizeable.UNITS_PIXELS);
        navigationPanel.setSpacing(true);
        layout.addComponent(navigationPanel);
        layout.setComponentAlignment(navigationPanel, Alignment.MIDDLE_CENTER);
    }
    
    public void logout() {
        authenticator.logout();
        show(new LoginPage());
    }
    
    public void changePassword() {
        final ChangePasswordDialog passwordDialog = new ChangePasswordDialog(
                Page.MESSAGES, credentialsManager,
                services.getCurrentUserInfo());
        layout.getWindow().addWindow(passwordDialog);        
    }
    
    public void login(String login, String password, boolean keepAlive)
            throws WrongPasswordException {
        authenticator.tryLogin(login, password, keepAlive);
        show(new ConfigsPage());
    }

    public void show(final Page page) {
        previousPage = currentPage;

        currentPage = page;
        if (!services.getCurrentUserInfo().isLoggedIn() && requiresLogin(page)) {
            currentPage = new LoginPage();
        }

        if (services.getCurrentUserInfo().isLoggedIn()
                && services.getCurrentUserInfo().getUserName().equals("admin")
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
        layout.getWindow().showNotification("Oops", message , Window.Notification.TYPE_ERROR_MESSAGE);
    }

    public void showNotification(String caption, String message) {
        layout.getWindow().showNotification(caption, message , Window.Notification.TYPE_HUMANIZED_MESSAGE);
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

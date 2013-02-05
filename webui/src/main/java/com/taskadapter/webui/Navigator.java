package com.taskadapter.webui;

import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.config.EditConfigPage;
import com.taskadapter.webui.service.Services;
import com.taskadapter.webui.service.WrongPasswordException;
import com.taskadapter.webui.user.ChangePasswordDialog;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.server.Sizeable.Unit.PIXELS;

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
                     CredentialsManager credManager, Authenticator authenticator) {
        this.layout = layout;
        this.services = services;
        this.credentialsManager = credManager;
        this.authenticator = authenticator;
        addGoogleAnalyticsIfNotTestMode();
        buildUI();
    }

    private void addGoogleAnalyticsIfNotTestMode() {
        UI ui = UI.getCurrent();
        /* ui is NULL when we run unit tests.
           there's no need in tracking our test activities in Google Analytics.
         */
        if (ui != null) {
            addGoogleAnalytics(ui);
        }
    }

    private void addGoogleAnalytics(UI ui) {
        tracker = new GoogleAnalyticsTracker(GOOGLE_ANALYTICS_ID, "none");
        tracker.extend(ui);
    }

    private void buildUI() {
        // TODO !! this is a hack
        Header header = new Header(this, services);
        header.setHeight(50, PIXELS);
        header.setWidth(100, PERCENTAGE);
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
        navigationPanel.setHeight(30, PIXELS);
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
        layout.getUI().addWindow(passwordDialog);
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

        trackPageInGoogleAnalytics();
    }

    private void trackPageInGoogleAnalytics() {
        if (tracker != null) {
            tracker.trackPageview("/" + currentPage.getPageGoogleAnalyticsID());
        }
    }

    private boolean requiresLogin(Page page) {
        // TODO refactor!
        return !"support".equals(page.getPageGoogleAnalyticsID());
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

    public void showSystemConfiguration() {
        show(new ConfigureSystemPage(credentialsManager));
    }
}

package com.taskadapter.webui;

import com.taskadapter.license.LicenseChangeListener;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.service.LoginEventListener;
import com.taskadapter.web.service.Services;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;

/**
 * Top-level header shown in the web UI.
 * Includes logo, "configure", "logout" and other buttons.
 */
public class Header extends HorizontalLayout implements LicenseChangeListener, LoginEventListener {
    private HorizontalLayout internalLayout = new HorizontalLayout();
    private HorizontalLayout panelForLoggedInUsers;
    private VerticalLayout trialLayout = new VerticalLayout();
    private Navigator navigator;
    private Services services;
    private Button configureButton;

    public Header(Navigator navigator, Services services) {
        this.navigator = navigator;
        this.services = services;
        buildMainLayout();
        checkLicense();
        services.getLicenseManager().addLicenseChangeListener(this);
        services.getAuthenticator().addLoginEventListener(this);
        userLoginInfoChanged(services.getAuthenticator().isLoggedIn());
    }

    private void buildMainLayout() {
        internalLayout.setWidth(Navigator.MAIN_WIDTH);
        internalLayout.setSpacing(true);
        addComponent(internalLayout);
        setComponentAlignment(internalLayout, Alignment.MIDDLE_CENTER);

        setSpacing(true);
        addStyleName("header-panel");

        addLogo();
        addMenuItems();
        addPanelForLoggedInUsers();
        addTrialSection();
    }

    private void addPanelForLoggedInUsers() {
        panelForLoggedInUsers = new HorizontalLayout();
        panelForLoggedInUsers.setSpacing(true);
        internalLayout.addComponent(panelForLoggedInUsers);
        internalLayout.setComponentAlignment(panelForLoggedInUsers, Alignment.MIDDLE_RIGHT);
        addLogoutButton();
        addSetPasswordButton();
    }

    private void addLogoutButton() {
        Button logoutButton = new Button("Logout");
        logoutButton.setStyleName(BaseTheme.BUTTON_LINK);
        logoutButton.addStyleName("personalMenuItem");
        logoutButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                services.getAuthenticator().logout();
                navigator.show(Navigator.HOME);
            }
        });
        panelForLoggedInUsers.addComponent(logoutButton);
    }

    private void addSetPasswordButton() {
        Button setPasswordButton = new Button("Change password");
        setPasswordButton.setStyleName(BaseTheme.BUTTON_LINK);
        setPasswordButton.addStyleName("personalMenuItem");
        setPasswordButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                EditorUtil.startChangePasswordProcess(getWindow(), services.getUserManager(), services.getAuthenticator());
            }
        });
        panelForLoggedInUsers.addComponent(setPasswordButton);
    }

    private void addTrialSection() {
        trialLayout.setSizeFull();
        Label trialLabel = new Label("Trial mode");
        trialLabel.setSizeUndefined();
        trialLabel.addStyleName("trial-mode-label");
        trialLayout.addComponent(trialLabel);
        trialLayout.setComponentAlignment(trialLabel, Alignment.MIDDLE_CENTER);
        internalLayout.addComponent(trialLayout);
        internalLayout.setExpandRatio(trialLayout, 1f);
        trialLayout.setVisible(false);
    }

    private void addLogo() {
        Button logo = createButtonLink("Task Adapter", Navigator.HOME, "logo");
        internalLayout.addComponent(logo);
        internalLayout.setExpandRatio(logo, 2f);
    }

    private void addMenuItems() {
        HorizontalLayout menu = new HorizontalLayout();
        menu.setSpacing(true);
        configureButton = createButtonLink("Configure", Navigator.CONFIGURE_SYSTEM_PAGE, "menu");
        menu.addComponent(configureButton);
        Button supportButton = createButtonLink("Support", Navigator.FEEDBACK_PAGE, "menu");
        menu.addComponent(supportButton);
        internalLayout.addComponent(menu);
        internalLayout.setExpandRatio(menu, 1f);
        internalLayout.setComponentAlignment(menu, Alignment.MIDDLE_CENTER);
    }

    private Button createButtonLink(String caption, final String pageId, String additionalStyle) {
        Button button = new Button(caption);
        button.setStyleName(BaseTheme.BUTTON_LINK);
        button.addStyleName(additionalStyle);
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.show(pageId);
            }
        });
        return button;
    }

    private void checkLicense() {
        if (!services.getLicenseManager().isSomeValidLicenseInstalled()) {
            trialLayout.setVisible(true);
        } else {
            trialLayout.setVisible(false);
        }
    }

    @Override
    public void licenseInfoUpdated() {
        checkLicense();
    }

    @Override
    public void userLoginInfoChanged(boolean userLoggedIn) {
        panelForLoggedInUsers.setVisible(userLoggedIn);
        configureButton.setVisible(userLoggedIn);
    }
}

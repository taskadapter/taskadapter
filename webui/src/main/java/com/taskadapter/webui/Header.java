package com.taskadapter.webui;

import com.taskadapter.license.LicenseChangeListener;
import com.taskadapter.webui.service.LoginEventListener;
import com.taskadapter.webui.service.Services;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

/**
 * Top-level header shown in the web UI.
 * Includes logo, "configure", "logout" and other buttons.
 */
public class Header extends HorizontalLayout implements LicenseChangeListener, LoginEventListener {
    private HorizontalLayout internalLayout = new HorizontalLayout();
    private HorizontalLayout panelForLoggedInUsers;
    private VerticalLayout trialLayout = new VerticalLayout();

    private final Navigator navigator;
    private final Services services;
    private Button configureButton;

    public Header(Navigator navigator, Services services) {
        this.navigator = navigator;
        this.services = services;
        buildMainLayout();
        checkLicense();
        services.getLicenseManager().addLicenseChangeListener(this);
        services.getCurrentUserInfo().addChangeEventListener(this);
        userLoginInfoChanged();
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
        logoutButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.logout();
            }
        });
        panelForLoggedInUsers.addComponent(logoutButton);
    }

    private void addSetPasswordButton() {
        Button setPasswordButton = new Button("Change password");
        setPasswordButton.setStyleName(BaseTheme.BUTTON_LINK);
        setPasswordButton.addStyleName("personalMenuItem");
        setPasswordButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.changePassword();
            }
        });
        panelForLoggedInUsers.addComponent(setPasswordButton);
    }

    private void addTrialSection() {
        trialLayout.setSizeFull();
        Label trialLabel = new Label("Trial mode");
        trialLabel.setDescription("Trial version will only transfer up to 10 tasks.<BR>It cannot be used in production environment.");
        trialLabel.setSizeUndefined();
        trialLabel.addStyleName("trial-mode-label");
        trialLayout.addComponent(trialLabel);
        trialLayout.setComponentAlignment(trialLabel, Alignment.MIDDLE_CENTER);
        internalLayout.addComponent(trialLayout);
        internalLayout.setExpandRatio(trialLayout, 1f);
        trialLayout.setVisible(false);
    }

    private void addLogo() {
        Button logo = createButtonLink("Task Adapter", new ConfigsPage(), "logo");
        internalLayout.addComponent(logo);
        internalLayout.setExpandRatio(logo, 2f);
    }

    private void addMenuItems() {
        HorizontalLayout menu = new HorizontalLayout();
        menu.setSpacing(true);
        configureButton = createButtonLink("Configure", "menu",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        navigator.showSystemConfiguration();
                    }
                });
        menu.addComponent(configureButton);

        addSupportItem(menu);
        internalLayout.addComponent(menu);
        internalLayout.setExpandRatio(menu, 1f);
        internalLayout.setComponentAlignment(menu, Alignment.MIDDLE_CENTER);
    }

    private void addSupportItem(HorizontalLayout menu) {
        Button supportButton = createButtonWithoutListener("Support");
        supportButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.show(new SupportPage(services.getCurrentTaskAdapterVersion(), services.getLicenseManager()));
            }
        });
        menu.addComponent(supportButton);
    }

    private Button createButtonWithoutListener(String caption) {
        Button button = new Button(caption);
        button.setStyleName(BaseTheme.BUTTON_LINK);
        button.addStyleName("menu");
        return button;
    }

    private Button createButtonLink(String caption, final Page page, String additionalStyle) {
        final Button.ClickListener handler = new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.show(page);
            }
        };
        return createButtonLink(caption, additionalStyle, handler);
    }

    private Button createButtonLink(String caption, String additionalStyle,
            final Button.ClickListener handler) {
        Button button = new Button(caption);
        button.setStyleName(BaseTheme.BUTTON_LINK);
        button.addStyleName(additionalStyle);
        button.addClickListener(handler);
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
    public void userLoginInfoChanged() {
        final boolean userLoggedIn = services.getCurrentUserInfo().isLoggedIn();
        panelForLoggedInUsers.setVisible(userLoggedIn);
        configureButton.setVisible(userLoggedIn);
    }
}

package com.taskadapter.webui;

import com.taskadapter.license.LicenseChangeListener;
import com.taskadapter.web.service.Services;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;
import sun.management.HotspotMemoryMBean;


/**
 * @author Alexey Skorokhodov
 */
public class Header extends HorizontalLayout implements LicenseChangeListener {
    private HorizontalLayout internalLayout = new HorizontalLayout();
    private VerticalLayout trialLayout = new VerticalLayout();
    private Button logoutButton;
    private Navigator navigator;
    private Services services;

    public Header(Navigator navigator, Services services) {
        this.navigator = navigator;
        this.services = services;
        buildMainLayout();
        checkLicense();
        services.getLicenseManager().addLicenseChangeListener(this);
        updateLogoutButtonState();
    }

    private void buildMainLayout() {
        internalLayout.setWidth(800, UNITS_PIXELS);
        addComponent(internalLayout);
        setComponentAlignment(internalLayout, Alignment.MIDDLE_CENTER);

        setSpacing(true);
        addStyleName("header-panel");

        addLogo();
        addMenuItems();
        addLogoutButton();
        addTrialSection();
    }

    private void addLogoutButton() {
        logoutButton = new Button("Logout");
        logoutButton.setStyleName(BaseTheme.BUTTON_LINK);
        logoutButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                services.getAuthenticator().logout();
                updateLogoutButtonState();
                navigator.show(Navigator.HOME);
            }
        });
        logoutButton.setVisible(false);
        internalLayout.addComponent(logoutButton);
    }

    private void addTrialSection() {
        trialLayout.setSizeFull();
        trialLayout.addStyleName("trial-mode-area");
        Label trialLabel = new Label("TRIAL MODE");
        trialLabel.setSizeUndefined();
        trialLabel.addStyleName("trial-mode-label");
        trialLayout.addComponent(trialLabel);
        trialLayout.setComponentAlignment(trialLabel, Alignment.MIDDLE_CENTER);

        Link buyLink = new Link("Buy now", new ExternalResource("http://www.taskadapter.com/buy"));
        buyLink.addStyleName("trial-mode-link");
        buyLink.setTargetName("_blank");

        trialLayout.addComponent(buyLink);
        trialLayout.setComponentAlignment(buyLink, Alignment.MIDDLE_CENTER);

        internalLayout.addComponent(trialLayout);
        internalLayout.setExpandRatio(trialLayout, 1f);
        trialLayout.setVisible(false);
    }

    public void updateLogoutButtonState() {
        if (services.getAuthenticator().isLoggedIn()) {
            logoutButton.setVisible(true);
        } else {
            logoutButton.setVisible(false);
        }

    }

    private void addLogo() {
        Button logo = createButtonLink("Task Adapter", Navigator.HOME, "logo");
        internalLayout.addComponent(logo);
        internalLayout.setExpandRatio(logo, 2f);
    }

    private void addMenuItems() {
        HorizontalLayout menu = new HorizontalLayout();
        menu.setSpacing(true);
        menu.addComponent(createButtonLink("Configure", Navigator.CONFIGURE_SYSTEM_PAGE, "menu"));
        menu.addComponent(createButtonLink("Support", Navigator.FEEDBACK_PAGE, "menu"));
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
}

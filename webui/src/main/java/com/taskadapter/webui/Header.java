package com.taskadapter.webui;

import com.taskadapter.license.LicenseChangeListener;
import com.taskadapter.license.LicenseManager;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.*;

/**
 * @author Alexey Skorokhodov
 */
public class Header extends HorizontalLayout implements LicenseChangeListener {
    private VerticalLayout trialLayout = new VerticalLayout();
    private Navigator navigator;

    public Header(Navigator navigator) {
        this.navigator = navigator;
        buildMainLayout();
        checkLicense();
    }

    private void buildMainLayout() {
        LicenseManager.addLicenseChangeListener(this);

        setSpacing(true);
        addStyleName("header-panel");

        Label label = new Label("Task Adapter");
        label.addStyleName("header-logo-label");
        addComponent(label);
        setExpandRatio(label, 2f);

        Label spaceLabel = new Label(" ");
        addComponent(spaceLabel);
//        setExpandRatio(spaceLabel, 3f);

        addMenuItems();

        trialLayout.setSizeFull();
        trialLayout.addStyleName("trial-mode-area");

        Label trialLabel = new Label(" --- TRIAL MODE --- ");
        trialLabel.setSizeUndefined();
        trialLabel.addStyleName("trial-mode-label");
        trialLayout.addComponent(trialLabel);
        trialLayout.setComponentAlignment(trialLabel, Alignment.MIDDLE_CENTER);

        Link buyLink = new Link("Buy it!", new ExternalResource("http://www.taskadapter.com/buy"));
        buyLink.setTargetName("_blank");

        trialLayout.addComponent(buyLink);
        trialLayout.setComponentAlignment(buyLink, Alignment.MIDDLE_CENTER);

        addComponent(trialLayout);
        setExpandRatio(trialLayout, 1f);
        trialLayout.setVisible(false);

        setSizeFull();
    }

    private void addMenuItems() {
        HorizontalLayout menu = new HorizontalLayout();
        menu.setSpacing(true);
        menu.addComponent(createMenu("Configure", Navigator.CONFIGURE_SYSTEM_PAGE));
        menu.addComponent(createMenu("Support", Navigator.FEEDBACK_PAGE));
        addComponent(menu);
        setExpandRatio(menu, 1f);
    }

    private Component createMenu(String label, String pageId) {
        MenuLinkBuilder menuLinkBuilder = new MenuLinkBuilder(navigator);
        Button buttonLink = menuLinkBuilder.createButtonLink(label, pageId);
        buttonLink.addStyleName("menu");
        return buttonLink;
    }

    private void checkLicense() {
        if(!LicenseManager.isTaskAdapterLicenseOK()) {
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

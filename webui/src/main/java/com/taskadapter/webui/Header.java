package com.taskadapter.webui;

import com.taskadapter.license.LicenseChangeListener;
import com.taskadapter.license.LicenseManager;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;

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

        Button logo = createButtonLink("Task Adapter", Navigator.HOME, "logo");
        addComponent(logo);
        setExpandRatio(logo, 2f);

        Label spaceLabel = new Label(" ");
        addComponent(spaceLabel);

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
        menu.addComponent(createButtonLink("Configure", Navigator.CONFIGURE_SYSTEM_PAGE, "menu"));
        menu.addComponent(createButtonLink("Support", Navigator.FEEDBACK_PAGE, "menu"));
        addComponent(menu);
        setExpandRatio(menu, 1f);
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
        if (!LicenseManager.isTaskAdapterLicenseOK()) {
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

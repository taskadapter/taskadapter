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

    public Header() {
        buildMainLayout();
        checkLicense();
    }

    private void buildMainLayout() {
        LicenseManager.addLicenseChangeListener(this);

        setSpacing(true);
        addStyleName("header_panel");

        Label label = new Label("Task Adapter");
        label.addStyleName("header_logo_label");
        addComponent(label);
        setExpandRatio(label, 2f);

        Label spaceLabel = new Label(" ");
        addComponent(spaceLabel);
        setExpandRatio(spaceLabel, 3f);


        trialLayout.setSizeFull();
        trialLayout.addStyleName("trial_mode_area");

        Label trialLabel = new Label(" --- TRIAL MODE --- ");
        trialLabel.setSizeUndefined();
        trialLabel.addStyleName("trial_mode_label");
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

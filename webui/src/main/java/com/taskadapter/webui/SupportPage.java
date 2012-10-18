package com.taskadapter.webui;

import com.taskadapter.web.service.LastVersionLoader;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class SupportPage extends Page {
    private final String currentTaskAdapterVersion;
    private VerticalLayout layout = new VerticalLayout();
    private Panel versionPanel;
    private VerticalLayout lastVersionInfoLayout = new VerticalLayout();

    public SupportPage(String currentTaskAdapterVersion) {
        this.currentTaskAdapterVersion = currentTaskAdapterVersion;
        buildUI();
    }

    private void buildUI() {
        layout.setSpacing(true);
        addVersionInfo();
        addBuyLicenseLink();
        addEmailLink();
    }

    private void addVersionInfo() {
        versionPanel = new Panel("Version Info");
        versionPanel.setWidth(300, Sizeable.UNITS_PIXELS);

        Label currentVersionLabel = new Label("Task Adapter version " + currentTaskAdapterVersion);
        versionPanel.addComponent(currentVersionLabel);

        Button checkButton = new Button("Check for update");
        checkButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                checkForUpdate();
            }
        });
        versionPanel.addComponent(checkButton);
        versionPanel.addComponent(lastVersionInfoLayout);
        layout.addComponent(versionPanel);
    }

    private void checkForUpdate() {
        lastVersionInfoLayout.removeAllComponents();

        String lastAvailableVersion = LastVersionLoader.loadLastVersion();
        Label latestVersionLabel = new Label("Latest available version: " + lastAvailableVersion);
        lastVersionInfoLayout.addComponent(latestVersionLabel);
        if (VersionComparator.isCurrentVersionOutdated(currentTaskAdapterVersion, lastAvailableVersion)) {
            Link downloadLink = new Link();
            downloadLink.setResource(new ExternalResource("http://www.taskadapter.com/download"));
            downloadLink.setCaption("Open Download page");
            downloadLink.setTargetName("_new");
            lastVersionInfoLayout.addComponent(downloadLink);
        }
    }

    private void addBuyLicenseLink() {
        Link buyLink = new Link("Buy license", new ExternalResource("http://www.taskadapter.com/buy"));
        buyLink.setTargetName("_blank");
        layout.addComponent(buyLink);
    }

    private void addEmailLink() {
        Link emailLink = new Link();
        emailLink.setResource(new ExternalResource("mailto:support@taskadapter.com"));
        emailLink.setCaption("Send us an email");
        emailLink.setTargetName("_new");
        layout.addComponent(emailLink);
    }

    @Override
    public String getPageGoogleAnalyticsID() {
        return "support";
    }

    @Override
    public Component getUI() {
        return layout;
    }
}

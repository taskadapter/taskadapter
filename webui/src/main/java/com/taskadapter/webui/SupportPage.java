package com.taskadapter.webui;

import com.taskadapter.license.LicenseManager;
import com.taskadapter.web.service.LastVersionLoader;
import com.taskadapter.webui.license.LicensePanel;
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
    private final LicenseManager licenseManager;
    private VerticalLayout layout = new VerticalLayout();
    private Panel versionPanel;
    private VerticalLayout lastVersionInfoLayout = new VerticalLayout();

    public SupportPage(String currentTaskAdapterVersion, LicenseManager licenseManager) {
        this.currentTaskAdapterVersion = currentTaskAdapterVersion;
        this.licenseManager = licenseManager;
        buildUI();
    }

    private void buildUI() {
        layout.setSpacing(true);
        addVersionInfo();
        createLicenseSection();
        addEmailLink();
    }

    private void addVersionInfo() {
        versionPanel = new Panel("Version Info");
        versionPanel.setWidth(400, Sizeable.UNITS_PIXELS);

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

        try {
            String lastAvailableVersion = LastVersionLoader.loadLastVersion();
            Label latestVersionLabel = new Label("Latest available version: " + lastAvailableVersion);
            lastVersionInfoLayout.addComponent(latestVersionLabel);
            if (VersionComparator.isCurrentVersionOutdated(currentTaskAdapterVersion, lastAvailableVersion)) {
                addDownloadLink();
            }
        } catch (RuntimeException e) {
            lastVersionInfoLayout.addComponent(new Label("Can't find information about the last available version."));
            addDownloadLink();
        }
    }

    private void createLicenseSection() {
        LicensePanel panel = new LicensePanel(licenseManager);
        layout.addComponent(panel);
    }

    private void addDownloadLink() {
        Link downloadLink = new Link();
        downloadLink.setResource(new ExternalResource("http://www.taskadapter.com/download"));
        downloadLink.setCaption("Open Download page");
        downloadLink.setTargetName("_new");
        lastVersionInfoLayout.addComponent(downloadLink);
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

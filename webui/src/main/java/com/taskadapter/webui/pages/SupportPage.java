package com.taskadapter.webui.pages;

import com.taskadapter.webui.LastVersionLoader;
import com.taskadapter.webui.VersionComparator;
import com.taskadapter.webui.license.LicenseFacade;
import com.taskadapter.webui.license.LicensePanel;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public final class SupportPage {
    private final String currentTaskAdapterVersion;
    private final LicenseFacade licenseManager;
    private VerticalLayout layout = new VerticalLayout();
    private Panel versionPanel;
    private VerticalLayout lastVersionInfoLayout = new VerticalLayout();

    private SupportPage(String currentTaskAdapterVersion,
            LicenseFacade licenseManager) {
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
        versionPanel.setWidth(400, Sizeable.Unit.PIXELS);

        Label currentVersionLabel = new Label("Task Adapter version "
                + currentTaskAdapterVersion);

        VerticalLayout view = new VerticalLayout();
        view.addComponent(currentVersionLabel);
        view.setMargin(true);

        Button checkButton = new Button("Check for update");
        checkButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                checkForUpdate();
            }
        });
        view.addComponent(checkButton);
        view.addComponent(lastVersionInfoLayout);
        versionPanel.setContent(view);
        layout.addComponent(versionPanel);
    }

    private void checkForUpdate() {
        lastVersionInfoLayout.removeAllComponents();

        try {
            String lastAvailableVersion = LastVersionLoader.loadLastVersion();
            Label latestVersionLabel = new Label("Latest available version: "
                    + lastAvailableVersion);
            lastVersionInfoLayout.addComponent(latestVersionLabel);
            if (VersionComparator.isCurrentVersionOutdated(
                    currentTaskAdapterVersion, lastAvailableVersion)) {
                addDownloadLink();
            }
        } catch (RuntimeException e) {
            lastVersionInfoLayout
                    .addComponent(new Label(
                            "Can't find information about the last available version."));
            addDownloadLink();
        }
    }

    private void createLicenseSection() {
        layout.addComponent(LicensePanel.renderLicensePanel(licenseManager));
    }

    private void addDownloadLink() {
        Link downloadLink = new Link();
        downloadLink.setResource(new ExternalResource(
                "http://www.taskadapter.com/download"));
        downloadLink.setCaption("Open Download page");
        downloadLink.setTargetName("_new");
        lastVersionInfoLayout.addComponent(downloadLink);
    }

    private void addEmailLink() {
        Link emailLink = new Link();
        emailLink.setResource(new ExternalResource(
                "mailto:support@taskadapter.com"));
        emailLink.setCaption("Send us an email");
        emailLink.setTargetName("_new");
        layout.addComponent(emailLink);
    }

    /**
     * Renders a support page.
     * @param taVersion task-adapter version.
     * @param license license text.
     * @return support UI.
     */
    public static Component render(String taVersion, LicenseFacade license) {
        return new SupportPage(taVersion, license).layout;
    }
}

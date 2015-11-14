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

import static com.taskadapter.webui.Page.message;

public final class SupportPage {
    private static final String TASKADAPTER_DOWNLOAD_URL = "http://www.taskadapter.com/download";

    private final String currentTaskAdapterVersion;
    private final LicenseFacade licenseManager;
    private final VerticalLayout layout = new VerticalLayout();
    private final VerticalLayout lastVersionInfoLayout = new VerticalLayout();

    private Panel versionPanel;

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
        versionPanel = new Panel(message("supportPage.versionInfo"));
        versionPanel.setWidth(400, Sizeable.Unit.PIXELS);

        Label currentVersionLabel = new Label(message("supportPage.taskAdapterVersion", currentTaskAdapterVersion));

        VerticalLayout view = new VerticalLayout();
        view.addComponent(currentVersionLabel);
        view.setMargin(true);

        Button checkButton = new Button(message("supportPage.checkForUpdate"));
        checkButton.addClickListener((Button.ClickListener) event -> checkForUpdate());
        view.addComponent(checkButton);
        view.addComponent(lastVersionInfoLayout);
        versionPanel.setContent(view);
        layout.addComponent(versionPanel);
    }

    private void checkForUpdate() {
        lastVersionInfoLayout.removeAllComponents();

        try {
            String lastAvailableVersion = LastVersionLoader.loadLastVersion();
            Label latestVersionLabel = new Label(message("supportPage.latestAvailableVersion", lastAvailableVersion));
            lastVersionInfoLayout.addComponent(latestVersionLabel);
            if (VersionComparator.isCurrentVersionOutdated(
                    currentTaskAdapterVersion, lastAvailableVersion)) {
                addDownloadLink();
            }
        } catch (RuntimeException e) {
            lastVersionInfoLayout.addComponent(new Label(message("supportPage.cantFindInfoOnLatestVersion")));
            addDownloadLink();
        }
    }

    private void createLicenseSection() {
        layout.addComponent(LicensePanel.renderLicensePanel(licenseManager));
    }

    private void addDownloadLink() {
        Link downloadLink = new Link();
        downloadLink.setResource(new ExternalResource(TASKADAPTER_DOWNLOAD_URL));
        downloadLink.setCaption(message("supportPage.openDownloadPage"));
        downloadLink.setTargetName("_new");
        lastVersionInfoLayout.addComponent(downloadLink);
    }

    private void addEmailLink() {
        Link emailLink = new Link();
        emailLink.setResource(new ExternalResource("mailto:support@taskadapter.com"));
        emailLink.setCaption(message("supportPage.sendUsAnEmail"));
        emailLink.setTargetName("_new");
        layout.addComponent(emailLink);
    }

    public static Component render(String taVersion, LicenseFacade license) {
        return new SupportPage(taVersion, license).layout;
    }
}

package com.taskadapter.webui.pages;

import com.taskadapter.webui.LastVersionLoader;
import com.taskadapter.webui.LogFinder;
import com.taskadapter.webui.Tracker;
import com.taskadapter.webui.VersionComparator;
import com.taskadapter.webui.license.LicenseFacade;
import com.taskadapter.webui.license.LicensePanel;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import static com.taskadapter.webui.Page.message;

public final class SupportPage {

    private final String currentTaskAdapterVersion;
    private final LicenseFacade licenseManager;
    private Tracker tracker;
    private final String cacheFileLocation;
    private final VerticalLayout layout = new VerticalLayout();
    private final VerticalLayout lastVersionInfoLayout = new VerticalLayout();

    private SupportPage(String currentTaskAdapterVersion,
                        LicenseFacade licenseManager,
                        Tracker tracker,
                        String cacheFileLocation) {
        this.currentTaskAdapterVersion = currentTaskAdapterVersion;
        this.licenseManager = licenseManager;
        this.tracker = tracker;
        this.cacheFileLocation = cacheFileLocation;
        buildUI();
    }

    private void buildUI() {
        layout.setSpacing(true);
        addEmailPanel();
        addVersionInfo();
        addFileLocationsSection();
        createLicenseSection();
    }

    private void addVersionInfo() {
        Panel versionPanel = new Panel(message("supportPage.versionInfo"));
        versionPanel.setWidth(700, Sizeable.Unit.PIXELS);

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
                lastVersionInfoLayout.addComponent(WebAppUpdater.addDownloadLink());
            }
        } catch (RuntimeException e) {
            lastVersionInfoLayout.addComponent(new Label(message("supportPage.cantFindInfoOnLatestVersion")));
            lastVersionInfoLayout.addComponent(WebAppUpdater.addDownloadLink());
        }
    }

    private void createLicenseSection() {
        layout.addComponent(LicensePanel.renderLicensePanel(licenseManager, tracker));
    }

    private void addFileLocationsSection() {
        Panel logsPanel = new Panel(message("supportPage.fileLocationsPanel"));
        GridLayout grid = new GridLayout(2, 2);
        grid.setWidth("100%");
        grid.setSpacing(true);
       // grid.setMargin(true);
        grid.addComponent(new Label(message("supportPage.logLocation")));
        grid.addComponent(new Label(LogFinder.getLogFileLocation()));

        grid.addComponent(new Label(message("supportPage.cacheFileLocation")));
        grid.addComponent(new Label(cacheFileLocation));

        logsPanel.setContent(grid);
        this.layout.addComponent(logsPanel);
    }

    private void addEmailPanel() {
        VerticalLayout l = new VerticalLayout();
        l.setMargin(true);
        String emailMessage = message("supportPage.sendUsAnEmail");
        l.addComponent(new Label(emailMessage));
        l.addComponent(new Label("support@taskadapter.com"));
        Panel panel = new Panel(message("supportPage.contactPanelTitle"));
        panel.setContent(l);
        layout.addComponent(panel);
    }

    public static Component render(String taVersion, LicenseFacade license, Tracker tracker, String cacheFileLocation) {
        return new SupportPage(taVersion, license, tracker, cacheFileLocation).layout;
    }
}

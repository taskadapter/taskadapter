package com.taskadapter.webui.pages;

import com.taskadapter.web.TaskKeeperLocationStorage;
import com.taskadapter.webui.BasePage;
import com.taskadapter.webui.LastVersionLoader;
import com.taskadapter.webui.Layout;
import com.taskadapter.webui.LogFinder;
import com.taskadapter.webui.SessionController;
import com.taskadapter.webui.Sizes;
import com.taskadapter.webui.VersionComparator;
import com.taskadapter.webui.license.LicenseFacade;
import com.taskadapter.webui.license.LicensePanel;
import com.taskadapter.webui.service.Preservices;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.taskadapter.webui.Page.message;

@Route(value = Navigator.SUPPORT, layout = Layout.class)
@CssImport(value = "./styles/views/mytheme.css")
public class SupportPage extends BasePage {
    private final Preservices services;
    private final TaskKeeperLocationStorage storage;
    private final String logFileLocation;
    private final String cacheFileLocation;

    public SupportPage() {
        services = SessionController.getServices();
        storage = new TaskKeeperLocationStorage(services.rootDir);
        cacheFileLocation = storage.cacheFolder().getAbsolutePath();
        logFileLocation = LogFinder.getLogFileLocation();
    }

    @Override
    protected void beforeEnter() {
        buildUI();
    }

    private void buildUI() {
        setSpacing(true);

        List<Component> components = Stream.of(
                addSection("supportPage.contactPanelTitle"),
                addEmailPanel(),

                addSection("supportPage.versionInfo"),
                addVersionInfo(),

                addSection("supportPage.fileLocationsPanel"),
                addFileLocationsSection(),

                addSection("supportPage.licenseInformation"),
                createLicenseSection()
        ).flatMap(Collection::stream)
                .collect(Collectors.toList());

        add(LayoutsUtil.centered(Sizes.mainWidth(),
                components));
    }

    private List<Component> addVersionInfo() {
        return List.of(new AppVersionComponent());
    }

    private List<Component> createLicenseSection() {
        return List.of(
                LicensePanel.renderLicensePanel(
                        new LicenseFacade(services.licenseManager)));
    }

    private List<Component> addSection(String captionKey) {
        return List.of(new Hr(),
                new H2(message(captionKey)));
    }

    private List<Component> addFileLocationsSection() {
        var grid = new FormLayout();
        grid.setResponsiveSteps(
                new FormLayout.ResponsiveStep("20em", 1),
                new FormLayout.ResponsiveStep("20em", 2));
        grid.add(new Label(message("supportPage.logLocation")),
                new Label(logFileLocation),
                new Label(message("supportPage.cacheFileLocation")),
                new Label(cacheFileLocation));
        return List.of(grid);
    }

    private List<Component> addEmailPanel() {
        var l = new VerticalLayout();
        l.setMargin(true);
        var emailMessage = message("supportPage.sendUsAnEmail");
        l.add(new Label(emailMessage));
        l.add(new Label("support@taskadapter.com"));
        return List.of(l);
    }
}

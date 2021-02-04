package com.taskadapter.webui.pages;

import com.taskadapter.webui.LastVersionLoader;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.VersionComparator;
import com.taskadapter.webui.service.CurrentVersionLoader;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import static com.taskadapter.webui.Page.message;

public class AppVersionComponent extends VerticalLayout {

    public AppVersionComponent() {
        var lastAvailableVersion = LastVersionLoader.loadLastVersion();
        var currentVersion = new CurrentVersionLoader().getCurrentVersion();

        var message = Page.message("appVersionComponent.versionInfo", currentVersion, lastAvailableVersion);
        Label label = new Label(message);
        Component downloadLink = WebAppUpdater.createDownloadLink();

        add(label,
                downloadLink);

        try {
            boolean outdated = VersionComparator.isCurrentVersionOutdated(currentVersion, lastAvailableVersion);
            if (outdated) {
                label.addClassName("important-notification-label");
            }
        } catch (RuntimeException e) {
            Label errorLabel = new Label(message("supportPage.cantFindInfoOnLatestVersion"));
            errorLabel.addClassName("important-notification-label");
            add(errorLabel);
        }
    }
}

package com.taskadapter.webui.pages;

import com.taskadapter.webui.LastVersionLoader;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.VersionComparator;
import com.taskadapter.webui.service.CurrentVersionLoader;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class AppUpdateNotificationComponent extends VerticalLayout {

    public AppUpdateNotificationComponent() {

        String lastAvailableVersion = LastVersionLoader.loadLastVersion();
        String currentVersion = new CurrentVersionLoader().getCurrentVersion();

        boolean outdated = VersionComparator.isCurrentVersionOutdated(currentVersion, lastAvailableVersion);
        if (outdated) {
            String message = Page.message("appUpdaterNotification.versionOutdated", currentVersion, lastAvailableVersion);
            Label label = new Label(message);
            label.addClassName("important-notification-label");
            add(label);
            Component link = WebAppUpdater.createDownloadLink();
            add(link);
        }
        setVisible(outdated);
    }
}

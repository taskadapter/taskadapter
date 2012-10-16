package com.taskadapter.webui;

import com.taskadapter.web.service.UpdateManager;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

public class SupportPage extends Page {
    private VerticalLayout layout = new VerticalLayout();
    private UpdateManager updateManager;

    public SupportPage(UpdateManager updateManager) {
        this.updateManager = updateManager;
        buildUI();
    }

    private void buildUI() {
        layout.setSpacing(true);
        addCurrentVersion();
        addCheckForUpdateLink();
        addBuyLicenseLink();
        addEmailLink();
    }

    private void addCurrentVersion() {
        Label currentVersionLabel = new Label("Task Adapter version " + updateManager.getCurrentVersion());
        currentVersionLabel.setWidth(400, Sizeable.UNITS_PIXELS);
        layout.addComponent(currentVersionLabel);
    }

    private void addCheckForUpdateLink() {
        Link link = new Link();
        link.setResource(new ExternalResource("http://www.taskadapter.com/download"));
        link.setCaption("Check for update");
        link.setTargetName("_new");
        layout.addComponent(link);
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

package com.taskadapter.webui;

import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.web.LocalRemoteOptionsPanel;
import com.taskadapter.webui.user.UsersPanel;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class ConfigureSystemPage extends Page {
    private VerticalLayout layout = new VerticalLayout();

    private final CredentialsManager credManager;
    
    public ConfigureSystemPage(CredentialsManager credManager) {
        this.credManager = credManager;
    }

    private void createLocalRemoteSection() {
        LocalRemoteOptionsPanel panel = new LocalRemoteOptionsPanel(services);
        setPanelWidth(panel);
        layout.addComponent(panel);
    }

    @Override
    public String getPageGoogleAnalyticsID() {
        return "system_configuration";
    }

    @Override
    public Component getUI() {
        layout.removeAllComponents();
        layout.setSpacing(true);

        createLocalRemoteSection();
        createUsersSection();
        return layout;
    }

    private void createUsersSection() {
        layout.addComponent(new UsersPanel(MESSAGES, services, credManager));
    }

    private void setPanelWidth(Panel panel) {
        panel.setWidth(500, Sizeable.UNITS_PIXELS);
    }
}

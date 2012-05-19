package com.taskadapter.webui;

import com.taskadapter.web.LocalRemoteOptionsPanel;
import com.taskadapter.webui.license.LicensePanel;
import com.taskadapter.webui.user.UsersPanel;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class ConfigureSystemPage extends Page {
    private VerticalLayout layout = new VerticalLayout();

    private void createLocalRemoteSection() {
        LocalRemoteOptionsPanel panel = new LocalRemoteOptionsPanel(services);
        setPanelWidth(panel);
        layout.addComponent(panel);
    }

    private void createLicenseSection() {
        LicensePanel panel = new LicensePanel(services);
        setPanelWidth(panel);
        layout.addComponent(panel);
    }

    private void setPanelWidth(Panel panel) {
        panel.setWidth(500, Sizeable.UNITS_PIXELS);
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
        createLicenseSection();
        createUsersSection();
        return layout;
    }

    private void createUsersSection() {
        layout.addComponent(new UsersPanel(services));
    }
}

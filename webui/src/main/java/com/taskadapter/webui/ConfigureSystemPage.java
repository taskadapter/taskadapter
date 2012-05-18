package com.taskadapter.webui;

import com.taskadapter.web.LocalRemoteOptionsPanel;
import com.taskadapter.webui.license.LicensePanel;
import com.taskadapter.webui.user.UsersPanel;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class ConfigureSystemPage extends Page {
    private VerticalLayout layout = new VerticalLayout();

    private void createLocalRemoteSection() {
        layout.addComponent(new LocalRemoteOptionsPanel(services));
    }

    private void createLicenseSection() {
        layout.addComponent(new LicensePanel(services));
    }

    @Override
    public String getPageTitle() {
        return "System configuration";
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

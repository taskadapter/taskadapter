package com.taskadapter.webui;

import com.taskadapter.web.LocalRemoteOptionsPanel;
import com.taskadapter.webui.license.LicensePanel;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class ConfigureSystemPage extends Page {
    private VerticalLayout layout = new VerticalLayout();
    private String message;

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

        if (message != null) {
            Label label = new Label(message);
            label.setContentMode(Label.CONTENT_XHTML);
            label.addStyleName("errorMessage");
            layout.addComponent(label);
        }
        createLocalRemoteSection();
        createLicenseSection();
        return layout;
    }

    public void setError(String message) {
        this.message = message;
    }

    public void clearError() {
        this.message = null;
    }
}

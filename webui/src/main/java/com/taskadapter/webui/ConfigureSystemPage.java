package com.taskadapter.webui;

import com.taskadapter.auth.AuthorizedOperations;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.webui.user.UsersPanel;
import com.vaadin.data.Property;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import static com.vaadin.server.Sizeable.Unit.PIXELS;

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
        createAdminPermissionsSection();
        createUsersSection();
        return layout;
    }

    private void createAdminPermissionsSection() {
        Panel panel = new Panel();
        VerticalLayout view = new VerticalLayout();
        view.setMargin(true);
        panel.setContent(view);
        CheckBox checkbox = new CheckBox("Admin can view and manage all users' configs");
        checkbox.setValue(services.getSettingsManager().adminCanManageAllConfigs());
        checkbox.setImmediate(true);
        checkbox.addValueChangeListener(new CheckBox.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                services.getSettingsManager().setAdminCanManageAllConfigs(((Boolean) valueChangeEvent.getProperty().getValue()));
            }
        });
        view.addComponent(checkbox);
        view.setComponentAlignment(checkbox, Alignment.MIDDLE_LEFT);
        final AuthorizedOperations allowedOps = services.getAuthorizedOperations();
        checkbox.setEnabled(allowedOps.canChangeServerSettings());
        layout.addComponent(panel);
    }

    private void createUsersSection() {
        layout.addComponent(new UsersPanel(MESSAGES, services, credManager));
    }

    private void setPanelWidth(Panel panel) {
        panel.setWidth(500, PIXELS);
    }
}

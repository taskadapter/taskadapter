package com.taskadapter.webui;

import com.taskadapter.auth.AuthorizedOperations;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.license.License;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.webui.user.UsersPanel;
import com.vaadin.data.Property;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import static com.vaadin.server.Sizeable.Unit.PIXELS;

public final class ConfigureSystemPage {

    private static Component createAdminPermissionsSection(
            final SettingsManager settingsManager, boolean modifiable) {
        final Panel panel = new Panel();
        final VerticalLayout view = new VerticalLayout();
        view.setMargin(true);
        panel.setContent(view);

        final CheckBox checkbox = new CheckBox(
                "Admin can view and manage all users' configs");
        checkbox.setValue(settingsManager.adminCanManageAllConfigs());
        checkbox.setImmediate(true);
        checkbox.addValueChangeListener(new CheckBox.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                settingsManager.setAdminCanManageAllConfigs(checkbox.getValue());
            }
        });
        view.addComponent(checkbox);
        view.setComponentAlignment(checkbox, Alignment.MIDDLE_LEFT);
        checkbox.setEnabled(modifiable);

        return panel;
    }

    public static Component render(CredentialsManager credentialsManager,
            SettingsManager settings, License license,
            AuthorizedOperations authorizedOps) {
        final VerticalLayout layout = new VerticalLayout();

        layout.setSpacing(true);
        final Component cmt = LocalRemoteOptionsPanel.createLocalRemoteOptions(
                settings, authorizedOps.canConfigureServer());
        cmt.setWidth(500, PIXELS);
        layout.addComponent(cmt);

        layout.addComponent(createAdminPermissionsSection(settings,
                authorizedOps.canConfigureServer()));

        layout.addComponent(UsersPanel.render(credentialsManager,
                authorizedOps, license));

        return layout;

    }
}

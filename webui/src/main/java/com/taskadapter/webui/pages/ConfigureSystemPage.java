package com.taskadapter.webui.pages;

import com.taskadapter.auth.AuthorizedOperations;
import com.taskadapter.auth.BasicCredentialsManager;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.webui.BasePage;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.webui.Layout;
import com.taskadapter.webui.LocalRemoteOptionsPanel;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.SessionController;
import com.taskadapter.webui.Sizes;
import com.taskadapter.webui.service.Preservices;
import com.taskadapter.webui.user.UsersPanel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.util.function.Function;

@Route(value = Navigator.CONFIGURE_SYSTEM, layout = Layout.class)
@CssImport(value = "./styles/views/mytheme.css")
public class ConfigureSystemPage extends BasePage {

    private final ConfigOperations configOps;
    private final Preservices services;
    private final AuthorizedOperations authorizedOps;
    private final BasicCredentialsManager credentialsManager;

    public ConfigureSystemPage() {
        configOps = SessionController.buildConfigOperations();
        services = SessionController.getServices();
        authorizedOps = configOps.getAuthorizedOps();
        credentialsManager = services.credentialsManager;
        buildUI();
    }

    private void buildUI() {
        setSpacing(true);
        var cmt = LocalRemoteOptionsPanel.createLocalRemoteOptions(authorizedOps.canConfigureServer());
        var allowedToEdit = authorizedOps.canConfigureServer();
        add(LayoutsUtil.centered(Sizes.mainWidth,
                cmt,
                createAdminPermissionsSection(allowedToEdit),
                createResultsNumberSection(),
                new UsersPanel(credentialsManager, authorizedOps)
        ));
    }

    private static Component createResultsNumberSection() {
        var label = new Label(Page.message("configurePage.maxNumberOfResultsToSave"));
        var field = new TextField();
        EditorUtil.setTooltip(field, Page.message("configurePage.maxNumberExplanation"));
        field.setValue(SettingsManager.getMaxNumberOfResultsToKeep() + "");
        // TODO 14 add type safe int parsing
        field.addValueChangeListener(e -> SettingsManager.setMaxNumberOfResultsToKeep(Integer.parseInt(field.getValue())));
        return new VerticalLayout(label, field);
    }

    private static VerticalLayout createAdminPermissionsSection(boolean modifiable) {
        var showAllUserConfigsCheckbox = checkbox(Page.message("configurePage.showAllUsersConfigs"),
                SettingsManager.adminCanManageAllConfigs(), modifiable,
                (newValue) -> {
                    SettingsManager.setAdminCanManageAllConfigs(newValue);
                    return null;
                });

        var anonymousErrorReportingCheckbox = checkbox(Page.message("configurePage.anonymousErrorReporting"),
                SettingsManager.isErrorReportingEnabled(), modifiable,
                (newValue) -> {
                    SettingsManager.setErrorReporting(newValue);
                    return null;
                });

        return new VerticalLayout(showAllUserConfigsCheckbox, anonymousErrorReportingCheckbox);
    }

    private static Checkbox checkbox(String label, boolean value,
                              boolean modifiable, Function<Boolean, Void> listener) {
        var checkbox = new Checkbox(label);
        checkbox.setValue(value);
        checkbox.addValueChangeListener(e -> listener.apply(checkbox.getValue()));
        checkbox.setEnabled(modifiable);
        return checkbox;
    }
}

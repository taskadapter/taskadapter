package com.taskadapter.webui.pages;

import com.taskadapter.auth.AuthorizedOperations;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.license.License;
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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.util.function.Function;

@Route(value = Navigator.CONFIGURE_SYSTEM, layout = Layout.class)
@CssImport(value = "./styles/views/mytheme.css")
public class ConfigureSystemPage extends BasePage {

    private final ConfigOperations configOps;
    private final Preservices services;

    public ConfigureSystemPage() {
        configOps = SessionController.buildConfigOperations();
        services = SessionController.getServices();
        buildUI();
    }

    private void buildUI() {
        render(services.credentialsManager,
                services.settingsManager,
                services.licenseManager.getLicense(),
                configOps.authorizedOps());
    }

    private void render(CredentialsManager credentialsManager,
                        SettingsManager settingsManager,
                        License license,
                        AuthorizedOperations authorizedOps) {

        setSpacing(true);
        var cmt = LocalRemoteOptionsPanel.createLocalRemoteOptions(settingsManager, authorizedOps.canConfigureServer());
        var allowedToEdit = authorizedOps.canConfigureServer() && license != null;
        add(LayoutsUtil.centered(Sizes.mainWidth(),
                cmt,
                createAdminPermissionsSection(settingsManager, allowedToEdit),
                createResultsNumberSection(settingsManager),
                new UsersPanel(credentialsManager, authorizedOps, license)
        ));
    }

    private Component createResultsNumberSection(SettingsManager settingsManager) {
        var view = new VerticalLayout();
        var field = new TextField(Page.message("configurePage.maxNumberOfResultsToSave"));
        EditorUtil.setDescriptionTooltip(field, Page.message("configurePage.maxNumberExplanation"));
        field.setValue(settingsManager.getMaxNumberOfResultsToKeep() + "");
        // TODO 14 add type safe int parsing
        field.addValueChangeListener(e -> settingsManager.setMaxNumberOfResultsToKeep(Integer.parseInt(field.getValue())));
        view.add(field);
        return view;
    }

    private VerticalLayout createAdminPermissionsSection(SettingsManager settingsManager, boolean modifiable) {
        var showAllUserConfigsCheckbox = checkbox(Page.message("configurePage.showAllUsersConfigs"),
                settingsManager.schedulerEnabled(), modifiable,
                (newValue) -> {
                    settingsManager.setSchedulerEnabled(newValue);
                    return null;
                });

        var anonymousErrorReportingCheckbox = checkbox(Page.message("configurePage.anonymousErrorReporting"),
                settingsManager.isErrorReportingEnabled(), modifiable,
                (newValue) -> {
                    settingsManager.setErrorReporting(newValue);
                    return null;
                });

        return new VerticalLayout(showAllUserConfigsCheckbox, anonymousErrorReportingCheckbox);
    }

    private Checkbox checkbox(String label, boolean value,
                              boolean modifiable, Function<Boolean, Void> listener) {
        var checkbox = new Checkbox(label);
        checkbox.setValue(value);
        checkbox.addValueChangeListener(e -> listener.apply(checkbox.getValue()));
        checkbox.setEnabled(modifiable);
        return checkbox;
    }
}

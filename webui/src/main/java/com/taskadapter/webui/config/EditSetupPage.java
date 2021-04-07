package com.taskadapter.webui.config;

import com.taskadapter.config.StorageException;
import com.taskadapter.connector.definition.ConnectorSetup;
import com.taskadapter.web.ConnectorSetupPanel;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.web.uiapi.SetupId;
import com.taskadapter.webui.BasePage;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.webui.Layout;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.SessionController;
import com.taskadapter.webui.Sizes;
import com.taskadapter.webui.pages.LayoutsUtil;
import com.taskadapter.webui.pages.Navigator;
import com.taskadapter.webui.service.Preservices;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

@Route(value = "edit-setup", layout = Layout.class)
@CssImport(value = "./styles/views/mytheme.css")
public class EditSetupPage extends BasePage implements HasUrlParameter<String> {

    private final ConfigOperations configOps = SessionController.buildConfigOperations();
    private final Preservices services = SessionController.getServices();
    private final Sandbox sandbox = SessionController.createSandbox();
    private ConnectorSetupPanel editSetupPanel;

    @Override
    public void setParameter(BeforeEvent event, String setupIdStr) {
        showSetup(new SetupId(setupIdStr));
    }

    private void showSetup(SetupId setupId) {
        try {
            ConnectorSetup setup = configOps.getSetup(setupId);

            PluginEditorFactory<?, ConnectorSetup> editor = services.editorManager.getEditorFactory(setup.getConnectorId());
            editSetupPanel = editor.getEditSetupPanel(sandbox, setup);

            var saveButton = new Button(Page.message("editSetupPage.saveButton"),
                    e -> saveClicked(setupId));

            var closeButton = new Button(Page.message("editSetupPage.closeButton"),
                    e -> Navigator.setupsList());

            add(LayoutsUtil.centered(Sizes.mainWidth,
                    editSetupPanel.getComponent(),
                    new HorizontalLayout(saveButton, closeButton)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveClicked(SetupId setupId) {
        var maybeError = editSetupPanel.validate();
        if (maybeError.isEmpty()) {
            try {
                configOps.saveSetup(editSetupPanel.getResult(), setupId);
            } catch (StorageException e) {
                throw new RuntimeException(e);
            }
            Navigator.setupsList();
        } else {
            editSetupPanel.showError(maybeError.get());
        }
    }
}

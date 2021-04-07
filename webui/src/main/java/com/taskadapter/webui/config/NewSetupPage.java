package com.taskadapter.webui.config;

import com.taskadapter.PluginManager;
import com.taskadapter.config.StorageException;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ConnectorSetup;
import com.taskadapter.web.ConnectorSetupPanel;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.webui.BasePage;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.webui.Layout;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.SessionController;
import com.taskadapter.webui.pages.Navigator;
import com.taskadapter.webui.service.EditorManager;
import com.taskadapter.webui.service.Preservices;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.function.Function;

@Route(value = Navigator.NEW_SETUP, layout = Layout.class)
@CssImport(value = "./styles/views/mytheme.css")
public class NewSetupPage extends BasePage {
    private ConfigOperations configOps = SessionController.buildConfigOperations();
    private Preservices services = SessionController.getServices();
    private Sandbox sandbox = SessionController.createSandbox();
    private PluginManager pluginManager = services.pluginManager;
    private EditorManager editorManager = services.editorManager;

    private VerticalLayout panelForEditor = new VerticalLayout();

    public NewSetupPage() {
        panelForEditor.setVisible(false);

        var component = new SelectConnectorComponent(pluginManager, showAddPanelForConnector());
        add(component, panelForEditor);
    }

    private Function<String, Void> showAddPanelForConnector() {
        return connectorId -> {
            // if you remove these class declarations, you will get runtime ClassCastExceptions saying cannot convert
            // WebConnectorSetup to Nothing!
            PluginEditorFactory<ConnectorConfig, ConnectorSetup> editor = editorManager.getEditorFactory(connectorId);
            ConnectorSetup setup = editor.createDefaultSetup(sandbox);
            var editSetupPanel = editor.getEditSetupPanel(sandbox, setup);
            panelForEditor.removeAll();
            panelForEditor.add(editSetupPanel.getComponent());
            var saveButton = new Button(Page.message("newSetupPage.saveButton"),
                    e -> saveClicked(editSetupPanel));
            var closeButton = new Button(Page.message("newSetupPage.cancelButton"),
                    e -> Navigator.setupsList());
            panelForEditor.add(new HorizontalLayout(saveButton, closeButton));
            panelForEditor.setVisible(true);
            return null;
        };
    }

    private void saveClicked(ConnectorSetupPanel panel) {
        var maybeError = panel.validate();
        if (maybeError.isEmpty()) {
            try {
                configOps.saveNewSetup(panel.getResult());
            } catch (StorageException e) {
                throw new RuntimeException(e);
            }
            Navigator.setupsList();
        } else {
            panel.showError(maybeError.get());
        }
    }
}
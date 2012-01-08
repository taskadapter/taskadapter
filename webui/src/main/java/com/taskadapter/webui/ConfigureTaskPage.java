package com.taskadapter.webui;

import com.taskadapter.config.ConfigStorage;
import com.taskadapter.config.TAConfig;
import com.taskadapter.config.TAConnectorDescriptor;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.vaadin.ui.*;

/**
 * @author Alexey Skorokhodov
 */
public class ConfigureTaskPage extends Page {
    private TAConfig config;
    private EditorManager editorManager;
    private ConfigStorage configStorage;
    private SettingsManager settingsManager;
    private TextField name;
    private ConfigEditor panel1;
    private ConfigEditor panel2;

    public ConfigureTaskPage(TAConfig config, EditorManager editorManager, ConfigStorage configStorage, SettingsManager settingsManager) {
        this.config = config;
        this.editorManager = editorManager;
        this.configStorage = configStorage;
        this.settingsManager = settingsManager;
        buildUI();
    }

    private void buildUI() {
        Panel panel = new Panel();
        setCompositionRoot(panel);
        name = new TextField("Name");
        panel.addComponent(name);
        name.setValue(config.getName());

        TabSheet tabSheet = new TabSheet();
        panel.addComponent(tabSheet);

        panel1 = getPanel(config.getConnector1());

        tabSheet.addTab(panel1, "panel 1");

        panel2 = getPanel(config.getConnector2());
        tabSheet.addTab(panel2, "panel 2");

        Button saveButton = new Button("Save");
        saveButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                save();
            }
        });
        panel.addComponent(saveButton);
    }

    private void save() {
        try {
            panel1.validateAll();
            panel2.validateAll();
            ConnectorConfig c1 = panel1.getConfig();
            ConnectorConfig c2 = panel2.getConfig();
            TAConnectorDescriptor d1 = new TAConnectorDescriptor(config.getConnector1().getType(), c1);
            TAConnectorDescriptor d2 = new TAConnectorDescriptor(config.getConnector2().getType(), c2);

            TAConfig newConfig = new TAConfig((String) name.getValue(), d1, d2);
            configStorage.saveConfig(newConfig);
            getWindow().showNotification("Saved");
        } catch (ValidationException e) {
            getWindow().showNotification("Validation", e.getMessage());
        }
    }

    private ConfigEditor getPanel(TAConnectorDescriptor descriptor) {
        ConnectorConfig configData = (ConnectorConfig) descriptor.getData();
        PluginEditorFactory editorFactory = editorManager.getEditorFactory(descriptor.getType());
        return editorFactory.createEditor(configData, settingsManager);
    }

    @Override
    public String getNavigationPanelTitle() {
        return config.getName();
    }
}

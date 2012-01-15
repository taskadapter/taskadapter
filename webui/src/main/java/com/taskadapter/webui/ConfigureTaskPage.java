package com.taskadapter.webui;

import com.taskadapter.config.ConfigStorage;
import com.taskadapter.config.ConnectorDataHolder;
import com.taskadapter.config.TAFile;
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
    private TAFile file;
    private EditorManager editorManager;
    private ConfigStorage configStorage;
    private SettingsManager settingsManager;
    private TextField name;
    private ConfigEditor panel1;
    private ConfigEditor panel2;

    public ConfigureTaskPage(TAFile file, EditorManager editorManager, ConfigStorage configStorage, SettingsManager settingsManager) {
        this.file = file;
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
        name.setValue(file.getName());

        TabSheet tabSheet = new TabSheet();
        panel.addComponent(tabSheet);

        panel1 = getPanel(file.getConnectorDataHolder1());

        tabSheet.addTab(panel1, "panel 1");

        panel2 = getPanel(file.getConnectorDataHolder2());
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
            ConnectorDataHolder d1 = new ConnectorDataHolder(file.getConnectorDataHolder1().getType(), c1);
            ConnectorDataHolder d2 = new ConnectorDataHolder(file.getConnectorDataHolder2().getType(), c2);

            TAFile newFile = new TAFile((String) name.getValue(), d1, d2);
            configStorage.saveConfig(newFile);
            getWindow().showNotification("Saved");
        } catch (ValidationException e) {
            getWindow().showNotification("Validation", e.getMessage());
        }
    }

    private ConfigEditor getPanel(ConnectorDataHolder dataHolder) {
        ConnectorConfig configData = (ConnectorConfig) dataHolder.getData();
        PluginEditorFactory editorFactory = editorManager.getEditorFactory(dataHolder.getType());
        return editorFactory.createEditor(configData, settingsManager);
    }

    @Override
    public String getNavigationPanelTitle() {
        return file.getName();
    }
}

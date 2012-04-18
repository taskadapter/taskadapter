package com.taskadapter.webui;

import com.taskadapter.config.ConnectorDataHolder;
import com.taskadapter.config.TAFile;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.vaadin.ui.*;

/**
 * @author Alexey Skorokhodov
 */
public class ConfigureTaskPage extends Page {

    private Panel panel = new Panel();
    private TAFile file;
    private TextField name;
    private ConfigEditor panel1;
    private ConfigEditor panel2;

    public ConfigureTaskPage() {
    }

    private void buildUI() {
        panel.removeAllComponents();
        HorizontalLayout buttonsLayout = new HorizontalLayout();

        Button saveButton = new Button("Save");
        saveButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                save();
                navigator.showTaskDetailsPage(file);
            }
        });
        buttonsLayout.addComponent(saveButton);
        panel.addComponent(buttonsLayout);

        name = new TextField("Name");
        name.setValue(file.getConfigLabel());
        panel.addComponent(name);

        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeUndefined();

        ConnectorDataHolder leftConnectorDataHolder = file.getConnectorDataHolder1();
        panel1 = getPanel(leftConnectorDataHolder);
        tabSheet.addTab(panel1, getPanelCaption(leftConnectorDataHolder));

        ConnectorDataHolder rightConnectorDataHolder = file.getConnectorDataHolder2();
        panel2 = getPanel(rightConnectorDataHolder);
        tabSheet.addTab(panel2, getPanelCaption(rightConnectorDataHolder));

        panel.addComponent(tabSheet);
    }

    private String getPanelCaption(ConnectorDataHolder connectorDataHolder) {
        return connectorDataHolder.getData().getLabel();
    }

    public void setFile(TAFile file) {
        this.file = file;
    }

    private void save() {
        try {
            panel1.validateAll();
            panel2.validateAll();

            updateFileWithDataInForm();
            services.getConfigStorage().saveConfig(file);
            navigator.showNotification("Saved", "All saved OK");

        } catch (ValidationException e) {
            navigator.showError("Validation", e.getMessage());
        }
    }

    private void updateFileWithDataInForm() {
        ConnectorConfig c1 = panel1.getConfig();
        ConnectorConfig c2 = panel2.getConfig();
        ConnectorDataHolder d1 = new ConnectorDataHolder(file.getConnectorDataHolder1().getType(), c1);
        ConnectorDataHolder d2 = new ConnectorDataHolder(file.getConnectorDataHolder2().getType(), c2);

        file.setConfigLabel((String) name.getValue());
        file.setConnectorDataHolder1(d1);
        file.setConnectorDataHolder2(d2);
    }

    private ConfigEditor getPanel(ConnectorDataHolder dataHolder) {
        ConnectorConfig configData = dataHolder.getData();
        PluginEditorFactory editorFactory = services.getEditorManager().getEditorFactory(dataHolder.getType());
        return editorFactory.createEditor(configData, services.getSettingsManager());
    }

    @Override
    public String getPageTitle() {
        return file.getConfigLabel();
    }

    @Override
    public Component getUI() {
        buildUI();
        return panel;
    }
}

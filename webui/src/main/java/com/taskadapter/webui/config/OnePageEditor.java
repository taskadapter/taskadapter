package com.taskadapter.webui.config;

import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.configeditor.MiniPanel;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class OnePageEditor extends VerticalLayout implements WindowProvider {

    private Messages messages;
    // TODO for MaximK: why is this deprecated? what's a replacement for it?
    @Deprecated
    private Services services;

    private UISyncConfig config;
    private TaskFieldsMappingFragment taskFieldsMappingFragment;
    private ExportButtonsFragment exportButtonsFragment;

    public OnePageEditor(Messages messages, Services services, UISyncConfig config) {
        this.messages = messages;
        this.services = services;
        this.config = config;
        buildUI();
    }

    private void buildUI() {
        setWidth(500, UNITS_PIXELS);
        setMargin(true);
        addConnectorsPanel();
        addMappingPanel();
    }

    private void addConnectorsPanel() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(createMiniPanel(config.getConnector1()));
        exportButtonsFragment = new ExportButtonsFragment();
        layout.addComponent(exportButtonsFragment);
        layout.addComponent(createMiniPanel(config.getConnector2()));
        addComponent(layout);
    }

    private void addMappingPanel() {
        taskFieldsMappingFragment = createOnePageMappingPanel();
        addComponent(taskFieldsMappingFragment);
    }

    private Panel createMiniPanel(UIConnectorConfig connectorConfig) {
        MiniPanel miniPanel = new MiniPanel(this, connectorConfig);
        // "services" instance is only used by MSP Editor Factory
        miniPanel.setPanelContents(connectorConfig.createMiniPanel(this, services));

        Panel panel = new Panel(miniPanel);
        panel.setWidth(200, UNITS_PIXELS);
        panel.setCaption(connectorConfig.getConnectorTypeId());
        return panel;
    }

    private TaskFieldsMappingFragment createOnePageMappingPanel() {
        return new TaskFieldsMappingFragment(messages, config.getConnector1(), config.getConnector2(), config.getNewMappings());
    }

    public void validate() throws BadConfigException {
        // TODO !!! validate left/right editors too. this was lost during the last refactoring.
        taskFieldsMappingFragment.validate();
    }

    Button getButtonRight() {
        return exportButtonsFragment.getButtonRight();
    }

    Button getButtonLeft() {
        return exportButtonsFragment.getButtonLeft();
    }

}

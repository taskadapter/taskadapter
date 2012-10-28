package com.taskadapter.webui.config;

import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.configeditor.MiniPanel;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;

public class OnePageEditor extends GridLayout implements WindowProvider {
    private static final int COLUMNS_NUMBER = 2;

    // TODO for MaximK: why is this deprecated? what's the replacement for it?
    @Deprecated
    private Services services;
    
    private UISyncConfig config;
    private OnePageMappingPanel onePageMappingPanel;

    public OnePageEditor(Services services, UISyncConfig config) {
        this.services = services;
        this.config = config;
        buildUI();
    }

    private void buildUI() {
        setColumns(COLUMNS_NUMBER);
        setRows(2);
        setWidth(700, UNITS_PIXELS);
        addComponent(createMiniPanel(config.getConnector1()));
        addComponent(createMiniPanel(config.getConnector2()));
        onePageMappingPanel = createOnePageMappingPanel();
        addComponent(onePageMappingPanel, 0, 1, 1, 1);
    }
    
    private Panel createMiniPanel(UIConnectorConfig connectorConfig) {
        MiniPanel miniPanel = new MiniPanel(this, connectorConfig);
        // "services" instance is only used by MSP Editor Factory
        miniPanel.setPanelContents(connectorConfig.createMiniPanel(this, services));

        Panel panel = new Panel(miniPanel);
        panel.setCaption(connectorConfig.getConnectorTypeId());
        return panel;
    }

    private OnePageMappingPanel createOnePageMappingPanel() {
        return new OnePageMappingPanel(config.getConnector1(), config.getConnector2(), config.getNewMappings());
    }

    public void validate() throws BadConfigException {
        onePageMappingPanel.validate();
    }
}

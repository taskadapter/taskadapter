package com.taskadapter.webui;

import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.configeditor.MiniPanel;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;

public class OnePageEditor extends GridLayout implements WindowProvider {
    private static final int COLUMNS_NUMBER = 2;

    @Deprecated
    private Services services;
    
    private UISyncConfig config;

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
        addComponent(addOnePageMappingPanel(), 0, 1, 1, 1);
    }
    
    private Panel createMiniPanel(UIConnectorConfig connectorConfig) {
        MiniPanel miniPanel = new MiniPanel(this, connectorConfig);
        // "services" instance is only used by MSP Editor Factory
        miniPanel.setPanelContents(connectorConfig.createMiniPanel(this, services));

        Panel panel = new Panel(miniPanel);
        panel.setCaption(connectorConfig.getConnectorTypeId());
        return panel;
    }

    private OnePageMappingPanel addOnePageMappingPanel() {
        return new OnePageMappingPanel(config.getConnector1(), config.getConnector2(), config.getNewMappings());
    }
}

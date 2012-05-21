package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.service.Services;
import com.vaadin.ui.*;

/**
 * Author: Alexander Kulik
 * Date: 21.05.12 15:25
 */
public abstract class TwoColumnsConfigEditor extends ConfigEditor {
    private VerticalLayout leftVerticalLayout;
    private VerticalLayout rightVerticalLayout;

    protected TwoColumnsConfigEditor(ConnectorConfig config) {
        super(config);
        buildUI();
    }

    public TwoColumnsConfigEditor(ConnectorConfig config, Services services) {
        super(config, services);
        buildUI();
    }

    private void buildUI() {
        HorizontalLayout root = new HorizontalLayout();
        root.setSpacing(true);

        leftVerticalLayout = new VerticalLayout();
        leftVerticalLayout.setWidth(DefaultPanel.WIDE_PANEL_WIDTH);
        leftVerticalLayout.setSpacing(true);

        rightVerticalLayout = new VerticalLayout();
        rightVerticalLayout.setWidth(DefaultPanel.NARROW_PANEL_WIDTH);
        rightVerticalLayout.setSpacing(true);

        root.addComponent(leftVerticalLayout);
        root.addComponent(rightVerticalLayout);
        addComponent(root);
    }

    private void addToColumn(Layout column, Panel panel, String width) {
        addPanelToCustomComponent(column, panel);
        panel.setWidth(width);
    }
    
    protected void addToLeftColumn(Panel panel) {
        addToColumn(leftVerticalLayout, panel, DefaultPanel.WIDE_PANEL_WIDTH);
    }

    protected void addToRightColumn(Panel panel) {
        addToColumn(rightVerticalLayout, panel, DefaultPanel.NARROW_PANEL_WIDTH);
    }

    protected void addToLeftColumn(Component component) {
        leftVerticalLayout.addComponent(component);
    }

    protected void addToRightColumn(Component component) {
        rightVerticalLayout.addComponent(component);
    }

    protected Label createEmptyLabel(String height) {
        Label label = new Label("&nbsp;", Label.CONTENT_XHTML);
        label.setHeight(height);
        return label;
    }

    /**
     * Create [Server Panel] [Project Panel]
     */
    protected void createServerAndProjectPanelOnTopDefault(ProjectProcessor projectProcessor) {
        // left column
        serverPanel = new ServerPanel();
        addToLeftColumn(serverPanel);

        // right column
        projectPanel = new ProjectPanel(this, projectProcessor);
        addToRightColumn(projectPanel);
        addToRightColumn(createEmptyLabel("10px"));
    }


}

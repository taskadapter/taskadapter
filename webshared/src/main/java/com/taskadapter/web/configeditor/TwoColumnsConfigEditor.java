package com.taskadapter.web.configeditor;

import java.util.List;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.WebConfig;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
import com.taskadapter.web.service.Services;
import com.vaadin.data.Property;
import com.vaadin.ui.*;

public abstract class TwoColumnsConfigEditor extends ConfigEditor {
    private VerticalLayout leftVerticalLayout;
    private VerticalLayout rightVerticalLayout;

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
        addPanelToLayout(column, panel);
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
	protected void createServerAndProjectPanelOnTopDefault(
			Property projectKey,
			Property queryId,
			DataProvider<List<? extends NamedKeyedObject>> projectProvider, 
			SimpleCallback projectInfoCallback,
			DataProvider<List<? extends NamedKeyedObject>> queryProvider) {
		// left column
		addToLeftColumn(new ServerPanel(((WebConfig) config)));

		// right column
		addToRightColumn(new ProjectPanel(this, projectKey, queryId, 
				projectProvider, projectInfoCallback, queryProvider));
		addToRightColumn(createEmptyLabel("10px"));
	}

}

package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
import com.taskadapter.web.service.Services;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import java.util.List;

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
			DataProvider<List<? extends NamedKeyedObject>> queryProvider, WebServerInfo serverInfo) {
		// left column
        ServerPanel serverPanel = new ServerPanel(new MethodProperty<String>(config, "label"),
                new MethodProperty<String>(serverInfo, "host"),
                new MethodProperty<String>(serverInfo, "userName"),
                new MethodProperty<String>(serverInfo, "password"));
        addToLeftColumn(serverPanel);

		// right column
		addToRightColumn(new ProjectPanel(this, projectKey, queryId, 
				projectProvider, projectInfoCallback, queryProvider));
		addToRightColumn(createEmptyLabel("10px"));
	}

}

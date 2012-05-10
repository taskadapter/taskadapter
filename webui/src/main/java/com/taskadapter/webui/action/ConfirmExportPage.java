package com.taskadapter.webui.action;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Mapping;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.web.configeditor.FieldsMappingPanel;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import java.util.List;
import java.util.Map;

public class ConfirmExportPage extends CustomComponent {
    private List<GTask> rootLevelTasks;
    private Connector connectorTo;
    private Button.ClickListener goListener;
    private FieldsMappingPanel fieldMappingPanel;
    private Map<FIELD, Mapping> oldFieldsMapping;
    private MyTree connectorTree;

    public ConfirmExportPage(List<GTask> rootLevelTasks, Connector destinationConnector, Button.ClickListener goListener) {
        this.rootLevelTasks = rootLevelTasks;
        this.connectorTo = destinationConnector;
        this.goListener = goListener;
        buildUI();
    }

    private void buildUI() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setSizeFull();

        Label text1 = new Label("Please confirm export to " + connectorTo.getConfig().getTargetLocation());
        layout.addComponent(text1);

        connectorTree = new MyTree();
        connectorTree.setSizeFull();
        connectorTree.setTasks(rootLevelTasks);
        layout.addComponent(connectorTree);

        oldFieldsMapping = connectorTo.getConfig().getFieldsMapping();

        Button go = new Button("Go");
        go.addListener(goListener);
        layout.addComponent(go);
        setCompositionRoot(layout);
        this.fieldMappingPanel = new FieldsMappingPanel(connectorTo.getDescriptor().getAvailableFieldsProvider(), connectorTo.getConfig());
        layout.addComponent(fieldMappingPanel);
    }

    public boolean needToSaveConfig() {
        return !oldFieldsMapping.equals(fieldMappingPanel.getResult());
    }

    public List<GTask> getSelectedRootLevelTasks() {
        return connectorTree.getSelectedRootLevelTasks();
    }

    public Map<FIELD, Mapping> getConnectorToFieldMappings() {
        return fieldMappingPanel.getResult();
    }
}

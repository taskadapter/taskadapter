package com.taskadapter.webui.action;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.FileBasedConnector;
import com.taskadapter.connector.definition.Mapping;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.web.configeditor.FieldsMappingPanel;
import com.taskadapter.webui.service.Services;
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
    private Services services;
    private FieldsMappingPanel fieldMappingPanel;
    private Map<FIELD, Mapping> oldFieldsMapping;
    private MyTree connectorTree;

    public ConfirmExportPage(List<GTask> rootLevelTasks, Connector destinationConnector, Button.ClickListener goListener, Services services) {
        this.rootLevelTasks = rootLevelTasks;
        this.connectorTo = destinationConnector;
        this.goListener = goListener;
        this.services = services;
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
        // save the mapping before calling the main button listener.
        // TODO auto-save the mapping fields to disk as soon as user changes something
        go.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // TODO validate
//                fieldMappingPanel.validate();
                Map<FIELD, Mapping> newMappings = fieldMappingPanel.getResult();
                if (!newMappings.equals(oldFieldsMapping)) {
                    connectorTo.getConfig().setFieldsMapping(newMappings);
                    // TODO http://www.hostedredmine.com/issues/64437
//                    services.getConfigStorage().saveConfig(file);
                }
            }
        });
        go.addListener(goListener);
        layout.addComponent(go);
        setCompositionRoot(layout);
        this.fieldMappingPanel = new FieldsMappingPanel(connectorTo.getDescriptor().getAvailableFieldsProvider(), oldFieldsMapping);
        layout.addComponent(fieldMappingPanel);
    }

    public boolean needToSaveConfig() {
        Map<FIELD, Mapping> newMappings = fieldMappingPanel.getResult();

        if (!newMappings.equals(oldFieldsMapping)) {
            connectorTo.getConfig().setFieldsMapping(newMappings);
            return true;
        }
        else {
            return false;
        }
    }

    public List<GTask> getSelectedRootLevelTasks() {
        return connectorTree.getSelectedRootLevelTasks();
    }
}

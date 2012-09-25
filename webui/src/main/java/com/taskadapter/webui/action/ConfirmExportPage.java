package com.taskadapter.webui.action;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.model.GTask;
import com.taskadapter.web.configeditor.FieldsMappingPanel;
import com.taskadapter.webui.Navigator;
import com.taskadapter.webui.PageUtil;
import com.vaadin.ui.*;

import java.util.List;

public class ConfirmExportPage extends CustomComponent {
    private final Navigator navigator;
    private final List<GTask> rootLevelTasks;
    private final ConnectorConfig destinationConfig;
    private final AvailableFields fieldsSupportedByDestination;
    private final Button.ClickListener goListener;
    private FieldsMappingPanel fieldMappingPanel;
    private MyTree connectorTree;

    public ConfirmExportPage(Navigator navigator, List<GTask> rootLevelTasks, ConnectorConfig destinationConfig,
                             AvailableFields fieldsSupportedByDestination, Button.ClickListener goListener) {
        this.navigator = navigator;
        this.rootLevelTasks = rootLevelTasks;
        this.destinationConfig = destinationConfig;
        this.fieldsSupportedByDestination = fieldsSupportedByDestination;
        this.goListener = goListener;
        buildUI();
    }

    private void buildUI() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);

        Label text1 = new Label("Please confirm export to " + destinationConfig.getTargetLocation());
        layout.addComponent(text1);

        connectorTree = new MyTree();
        connectorTree.setSizeFull();
        connectorTree.setTasks(rootLevelTasks);
        layout.addComponent(connectorTree);

        Button goButton = new Button("Go");
        goButton.addListener(goListener);

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.addComponent(goButton);
        buttonsLayout.addComponent(PageUtil.createButton(navigator, "Cancel", Navigator.HOME));
        layout.addComponent(buttonsLayout);

        this.fieldMappingPanel = new FieldsMappingPanel(fieldsSupportedByDestination, destinationConfig.getFieldMappings());
        layout.addComponent(fieldMappingPanel);

        setCompositionRoot(layout);
    }

    public boolean needToSaveConfig() {
        return fieldMappingPanel.haveChanges();
    }

    public List<GTask> getSelectedRootLevelTasks() {
        return connectorTree.getSelectedRootLevelTasks();
    }
}

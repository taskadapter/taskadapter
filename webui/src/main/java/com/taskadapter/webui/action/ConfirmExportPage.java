package com.taskadapter.webui.action;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.NewMappings;
import com.taskadapter.model.GTask;
import com.taskadapter.webui.Navigator;
import com.taskadapter.webui.OnePageMappingPanel;
import com.taskadapter.webui.PageUtil;
import com.vaadin.ui.*;

import java.util.List;

public class ConfirmExportPage extends CustomComponent {
    private final Navigator navigator;
    private final List<GTask> rootLevelTasks;
    private String sourceConnectorId;
    private AvailableFields fieldsSupportedBySource;
    private String destinationConnectorId;
    private final ConnectorConfig destinationConfig;
    private final AvailableFields fieldsSupportedByDestination;
    private final Button.ClickListener goListener;
    private NewMappings mappings;
    private OnePageMappingPanel onePageMappingPanel;
    private MyTree connectorTree;

    public ConfirmExportPage(Navigator navigator, List<GTask> rootLevelTasks,
                             String sourceConnectorId,
                             AvailableFields fieldsSupportedBySource,
                             String destinationConnectorId,
                             ConnectorConfig destinationConfig,
                             AvailableFields fieldsSupportedByDestination,
                             Button.ClickListener goListener,
                             NewMappings mappings) {
        this.navigator = navigator;
        this.rootLevelTasks = rootLevelTasks;
        this.sourceConnectorId = sourceConnectorId;
        this.fieldsSupportedBySource = fieldsSupportedBySource;
        this.destinationConnectorId = destinationConnectorId;
        this.destinationConfig = destinationConfig;
        this.fieldsSupportedByDestination = fieldsSupportedByDestination;
        this.goListener = goListener;
        this.mappings = mappings;
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

        onePageMappingPanel = new OnePageMappingPanel(sourceConnectorId, fieldsSupportedBySource,
                destinationConnectorId, fieldsSupportedByDestination, mappings);
        layout.addComponent(onePageMappingPanel);

        setCompositionRoot(layout);
    }

    // TODO !!! fix this
//    public boolean needToSaveConfig() {
//        return onePageMappingPanel.haveChanges();
//    }

    public List<GTask> getSelectedRootLevelTasks() {
        return connectorTree.getSelectedRootLevelTasks();
    }
}

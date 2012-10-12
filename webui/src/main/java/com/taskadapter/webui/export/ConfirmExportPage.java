package com.taskadapter.webui.export;

import com.taskadapter.model.GTask;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.Navigator;
import com.taskadapter.webui.OnePageMappingPanel;
import com.taskadapter.webui.PageUtil;
import com.taskadapter.webui.action.MyTree;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import java.util.List;

public class ConfirmExportPage extends CustomComponent {
    private final Navigator navigator;
    private final List<GTask> rootLevelTasks;
    private final Button.ClickListener goListener;
    private OnePageMappingPanel onePageMappingPanel;
    private MyTree connectorTree;
    private final UISyncConfig config;

    public ConfirmExportPage(Navigator navigator, List<GTask> rootLevelTasks,
                             UISyncConfig config,
                             Button.ClickListener goListener) {
        this.config = config;

        this.navigator = navigator;
        this.rootLevelTasks = rootLevelTasks;
        this.goListener = goListener;
        buildUI();
    }

    private void buildUI() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);

        // TODO !!! change ID to target location to allow working with two connectors of the same kind (redmine-redmine)
        Label text1 = new Label("Please confirm export to " + config.getConnector2().getConnectorTypeId());
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

        onePageMappingPanel = new OnePageMappingPanel(config.getConnector1(),
                config.getConnector2(), config.getNewMappings());
        layout.addComponent(onePageMappingPanel);

        setCompositionRoot(layout);
    }

    // TODO !!! config changed on the confirmation page is not saved
//    public boolean needToSaveConfig() {
//        return onePageMappingPanel.haveChanges();
//    }

    public List<GTask> getSelectedRootLevelTasks() {
        return connectorTree.getSelectedRootLevelTasks();
    }
}

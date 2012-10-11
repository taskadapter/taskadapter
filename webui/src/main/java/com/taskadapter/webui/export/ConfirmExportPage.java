package com.taskadapter.webui.export;

import com.taskadapter.config.TAFile;
import com.taskadapter.connector.definition.MappingSide;
import com.taskadapter.model.GTask;
import com.taskadapter.web.service.Services;
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
    private final Services services;
    private OnePageMappingPanel onePageMappingPanel;
    private MyTree connectorTree;
    private final DirectionResolver resolver;

    public ConfirmExportPage(Services services, Navigator navigator, List<GTask> rootLevelTasks,
                             TAFile file, MappingSide exportDirection,
                             Button.ClickListener goListener) {
        this.services = services;
        resolver = new DirectionResolver(file, exportDirection);

        this.navigator = navigator;
        this.rootLevelTasks = rootLevelTasks;
        this.goListener = goListener;
        buildUI();
    }

    private void buildUI() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);

        // TODO !!! change ID to target location to allow working with two connectors of the same kind (redmine-redmine)
        Label text1 = new Label("Please confirm export to " + resolver.getDestinationConnectorId());
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

        onePageMappingPanel = new OnePageMappingPanel(services, resolver);
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

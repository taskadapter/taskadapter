package com.taskadapter.webui.export;

import com.taskadapter.model.GTask;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.ConfigsPage;
import com.taskadapter.webui.Navigator;
import com.taskadapter.webui.config.TaskFieldsMappingFragment;
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
    private TaskFieldsMappingFragment taskFieldsMappingFragment;
    private MyTree connectorTree;
    private Messages messages;
    private final UISyncConfig config;

    public ConfirmExportPage(Messages messages, Navigator navigator, List<GTask> rootLevelTasks,
                             UISyncConfig config,
                             Button.ClickListener goListener) {
        this.messages = messages;
        this.config = config;

        this.navigator = navigator;
        this.rootLevelTasks = rootLevelTasks;
        this.goListener = goListener;
        buildUI();
    }

    private void buildUI() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);

        String destination = config.getConnector2().getDestinationLocation() + " (" + config.getConnector2().getConnectorTypeId() + ")";
        Label text1 = new Label("Please confirm export to " + destination);
        layout.addComponent(text1);

        connectorTree = new MyTree();
        connectorTree.setSizeFull();
        connectorTree.setTasks(rootLevelTasks);
        layout.addComponent(connectorTree);

        Button goButton = new Button("Go");
        goButton.addListener(goListener);

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.addComponent(goButton);
        buttonsLayout.addComponent(PageUtil.createButton(navigator, "Cancel", new ConfigsPage()));
        layout.addComponent(buttonsLayout);

        taskFieldsMappingFragment = new TaskFieldsMappingFragment(messages, config.getConnector1(),
                config.getConnector2(), config.getNewMappings());
        layout.addComponent(taskFieldsMappingFragment);

        setCompositionRoot(layout);
    }

    public boolean needToSaveConfig() {
        return taskFieldsMappingFragment.hasChanges();
    }

    public List<GTask> getSelectedRootLevelTasks() {
        return connectorTree.getSelectedRootLevelTasks();
    }
}

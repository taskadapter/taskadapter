package com.taskadapter.webui.export;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.model.GTask;
import com.taskadapter.web.PluginEditorFactory;
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
    private final ExportConfig<?, ?> exportConfig;

    public ConfirmExportPage(Services services, Navigator navigator, List<GTask> rootLevelTasks,
                             ExportConfig<?, ?> exportConfig,
                             Button.ClickListener goListener) {
        this.services = services;
        this.exportConfig = exportConfig;

        this.navigator = navigator;
        this.rootLevelTasks = rootLevelTasks;
        this.goListener = goListener;
        buildUI();
    }

    private void buildUI() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);

        // TODO !!! change ID to target location to allow working with two connectors of the same kind (redmine-redmine)
        Label text1 = new Label("Please confirm export to " + exportConfig.getTargetConfig().getType());
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

        PluginEditorFactory<?> targetFactory = services.getEditorManager().getEditorFactory(exportConfig.getTargetConfig().getType());
        AvailableFields fieldsSupportedByDestination = targetFactory.getAvailableFields();
        PluginEditorFactory<?> sourceFactory = services.getEditorManager().getEditorFactory(exportConfig.getSourceConfig().getType());
        AvailableFields fieldsSupportedBySource = sourceFactory.getAvailableFields();
        onePageMappingPanel = new OnePageMappingPanel(exportConfig
                .getSourceConfig().getType(), fieldsSupportedBySource,
                exportConfig.getTargetConfig().getType(),
                fieldsSupportedByDestination, exportConfig.getMappings());
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

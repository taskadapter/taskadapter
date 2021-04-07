package com.taskadapter.webui.export;

import com.taskadapter.config.StorageException;
import com.taskadapter.model.GTask;
import com.taskadapter.web.ui.HtmlLabel;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.action.MyTree;
import com.taskadapter.webui.config.TaskFieldsMappingFragment;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ConfirmExportFragment {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmExportFragment.class);

    public interface Callback {
        /**
         * Notifies about selected tasks.
         *
         * @param selectedTasks selected tasks.
         */
        void onTasks(List<GTask> selectedTasks);

        /**
         * Notifies about process cancellation.
         */
        void onCancel();
    }


    /**
     * Renders export confirmation fragment.
     *
     * @return confirmation dialog.
     */
    public static Component render(ConfigOperations configOps,
                                   UISyncConfig config,
                                   List<GTask> initialTasks,
                                   Callback callback) {
        var resolver = config.getPreviouslyCreatedTasksResolver();
        var layout = new VerticalLayout();
        layout.setSpacing(true);

        var loadedTasksLabel = new HtmlLabel(Page.message("exportConfirmation.loadedTasks",
                initialTasks.size() + "",
                config.getConnector1().getLabel(),
                config.getConnector1().getSourceLocation()));
        var destinationLocation = config.getConnector2().getDestinationLocation();
        var destinationWithDecoration = destinationLocation + " (" + config.getConnector2().getConnectorTypeId() + ")";
        var text1 = new Label(Page.message("exportConfirmation.pleaseConfirm", destinationWithDecoration));

        layout.add(loadedTasksLabel,
                text1);

        var connectorTree = new MyTree(resolver, initialTasks, destinationLocation);
        layout.add(connectorTree.getTree());
        var buttonsLayout = new HorizontalLayout();
        var goButton = new Button(Page.message("button.go"));
        buttonsLayout.add(goButton);
        var backButton = new Button(Page.message("button.cancel"),
                e -> callback.onCancel());
        buttonsLayout.add(backButton);
        layout.add(buttonsLayout);
        var taskFieldsMappingFragment = new TaskFieldsMappingFragment(Page.MESSAGES,
                config.getConnector1().getAllFields(), config.getConnector1().fieldNames(), config.getConnector1().getLabel(),
                config.getConnector2().getAllFields(), config.getConnector2().fieldNames(), config.getConnector2().getLabel(),
                config.getNewMappings());


        layout.add(taskFieldsMappingFragment.getComponent());
        goButton.addClickListener(event -> {
            try {
                var newFieldMappings = taskFieldsMappingFragment.getElements();
                var possiblyUpdatedConfig = new UISyncConfig(config.getTaskKeeperLocationStorage(),
                        config.getConfigId(),
                        config.getLabel(),
                        config.getConnector1(),
                        config.getConnector2(),
                        newFieldMappings,
                        config.isReversed());
                configOps.saveConfig(possiblyUpdatedConfig);
            } catch (StorageException e) {
                LOGGER.error(Page.message("action.cantSaveUpdatedConfig", e.getMessage()), e);
            }
            callback.onTasks(connectorTree.getSelectedRootLevelTasks());
        });
        return layout;
    }
}

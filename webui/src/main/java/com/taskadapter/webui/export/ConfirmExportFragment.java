package com.taskadapter.webui.export;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taskadapter.config.StorageException;
import com.taskadapter.model.GTask;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.action.MyTree;
import com.taskadapter.webui.config.TaskFieldsMappingFragment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public final class ConfirmExportFragment {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ConfirmExportFragment.class);

    /**
     * Confirm export fragment callbacks.
     */
    public static interface Callback {
        /**
         * Notifies about selected tasks.
         * 
         * @param selectedTasks
         *            selected tasks.
         */
        public void onTasks(List<GTask> selectedTasks);

        /**
         * Notifies about process cancellation.
         */
        public void onCancel();
    }

    /**
     * Renders an export confirmation fragment.
     * 
     * @param config
     *            export config.
     * @param initalTasks
     *            intial tasks.
     * @param callback
     *            result callback.
     * @return confirmation dialog.
     */
    public static Component render(final ConfigOperations configOps,
            final UISyncConfig config, List<GTask> initalTasks,
            final Callback callback) {

        final VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);

        final String destination = config.getConnector2()
                .getDestinationLocation()
                + " ("
                + config.getConnector2().getConnectorTypeId() + ")";
        Label text1 = new Label(Page.MESSAGES.format(
                "exportConfirmation.pleaseConfirm", destination));
        layout.addComponent(text1);

        final MyTree connectorTree = new MyTree();
        connectorTree.setSizeFull();
        connectorTree.setTasks(initalTasks);
        layout.addComponent(connectorTree);

        final HorizontalLayout buttonsLayout = new HorizontalLayout();
        final Button goButton = new Button(Page.MESSAGES.get("button.go"));
        buttonsLayout.addComponent(goButton);
        Button backButton = new Button(Page.MESSAGES.get("button.cancel"));
        backButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                callback.onCancel();
            }
        });
        buttonsLayout.addComponent(backButton);
        layout.addComponent(buttonsLayout);

        final TaskFieldsMappingFragment taskFieldsMappingFragment = new TaskFieldsMappingFragment(
                Page.MESSAGES, config.getConnector1(), config.getConnector2(),
                config.getNewMappings());
        layout.addComponent(taskFieldsMappingFragment.getUI());

        goButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    configOps.saveConfig(config);
                } catch (StorageException e) {
                    LOGGER.error(
                            Page.MESSAGES.format("action.cantSaveUpdatedConfig",
                                    e.getMessage()), e);
                }

                callback.onTasks(connectorTree.getSelectedRootLevelTasks());
            }
        });

        return layout;
    }
}

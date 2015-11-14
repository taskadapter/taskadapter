package com.taskadapter.webui;

import com.taskadapter.config.StorageException;
import com.taskadapter.web.MessageDialog;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * UI Component containing Clone and Delete buttons. Shown in "Edit Config"
 * page.
 */
public final class CloneDeletePanel {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CloneDeletePanel.class);

    private static final String YES = "Yes";
    private static final String CANCEL = "Cancel";

    private final UISyncConfig config;
    private final ConfigOperations configOps;
    private final Runnable onExit;

    private final HorizontalLayout layout;

    private CloneDeletePanel(UISyncConfig config, ConfigOperations configOps,
            Runnable onExit) {
        this.config = config;
        this.configOps = configOps;
        this.onExit = onExit;

        this.layout = new HorizontalLayout();

        final Button cloneButton = new Button("Clone");
        cloneButton.setDescription("Clone this config");
        cloneButton.addClickListener((Button.ClickListener) event -> showConfirmClonePage());
        layout.addComponent(cloneButton);

        final Button deleteButton = new Button("Delete");
        deleteButton.setDescription("Delete this config from Task Adapter");
        deleteButton.addClickListener((Button.ClickListener) clickEvent -> showDeleteFilePage());
        layout.addComponent(deleteButton);
    }

    private void showDeleteFilePage() {
        final MessageDialog messageDialog = new MessageDialog("Confirmation",
                "Delete this config?", Arrays.asList(YES, CANCEL),
                answer -> {
                    if (YES.equals(answer)) {
                        configOps.deleteConfig(config);
                        onExit.run();
                    }
                });
        messageDialog.setWidth("175px");
        layout.getUI().addWindow(messageDialog);
    }

    public void showConfirmClonePage() {
        MessageDialog messageDialog = new MessageDialog("Confirmation",
                "Clone this config?", Arrays.asList(YES, CANCEL),
                answer -> {
                    if (YES.equals(answer)) {
                        try {
                            configOps.cloneConfig(config);
                            onExit.run();
                        } catch (StorageException e) {
                            String message = "There were some troubles cloning the config:<BR>"
                                    + e.getMessage();
                            LOGGER.error(message, e);
                            Notification.show(message,
                                    Notification.Type.ERROR_MESSAGE);
                        }
                    }
                });
        messageDialog.setWidth("175px");
        layout.getUI().addWindow(messageDialog);
    }

    /**
     * Renders a clone/delete panel.
     * 
     * @param config
     *            current config.
     * @param configOps
     *            config operations.
     * @param onExit
     *            exit request handler.
     * @return clone/delete UI.
     */
    public static Component render(UISyncConfig config,
            ConfigOperations configOps, Runnable onExit) {
        return new CloneDeletePanel(config, configOps, onExit).layout;
    }
}

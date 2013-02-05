package com.taskadapter.webui;

import com.taskadapter.config.StorageException;
import com.taskadapter.web.MessageDialog;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.service.Services;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * UI Component containing Clone and Delete buttons. Shown in "Edit Config" page.
 */
public class CloneDeletePanel extends HorizontalLayout {
    private final Logger logger = LoggerFactory.getLogger(CloneDeletePanel.class);

    private static final String YES = "Yes";
    private static final String CANCEL = "Cancel";

    private final Navigator navigator;
    private final UISyncConfig config;
    private final Services services;

    public CloneDeletePanel(Services services, Navigator navigator, UISyncConfig config) {
        this.services = services;
        this.navigator = navigator;
        this.config = config;
        buildUI();
    }

    private void buildUI() {
        Button cloneButton = new Button("Clone");
        cloneButton.setDescription("Clone this config");
        cloneButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                showConfirmClonePage();
            }
        });
        addComponent(cloneButton);

        Button deleteButton = new Button("Delete");
        deleteButton.setDescription("Delete this config from Task Adapter");
        deleteButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                showDeleteFilePage();
            }
        });
        addComponent(deleteButton);
    }

    private void showDeleteFilePage() {
        MessageDialog messageDialog = new MessageDialog(
                "Confirmation", "Delete this config?",
                Arrays.asList(YES, CANCEL),
                new MessageDialog.Callback() {
                    public void onDialogResult(String answer) {
                        if (YES.equals(answer)) {
                            services.getUIConfigStore().deleteConfig(config);
                            navigator.show(new ConfigsPage());
                        }
                    }
                }
        );
        messageDialog.setWidth("175px");
        getUI().addWindow(messageDialog);
    }

    public void showConfirmClonePage() {
        MessageDialog messageDialog = new MessageDialog(
                "Confirmation", "Clone this config?",
                Arrays.asList(YES, CANCEL),
                new MessageDialog.Callback() {
                    public void onDialogResult(String answer) {
                        if (YES.equals(answer)) {
                            final String userLoginName = services.getCurrentUserInfo().getUserName();
                            try {
                                services.getUIConfigStore().cloneConfig(userLoginName, config);
                                navigator.show(new ConfigsPage());
                            } catch (StorageException e) {
                                String message = "There were some troubles cloning the config:<BR>" + e.getMessage();
                                logger.error(message, e);
                                Notification.show(message, Notification.Type.ERROR_MESSAGE);
                            }
                        }
                    }
                }
        );
        messageDialog.setWidth("175px");
        getUI().addWindow(messageDialog);
    }
}

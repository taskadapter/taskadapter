package com.taskadapter.webui;

import com.taskadapter.config.StorageException;
import com.taskadapter.web.MessageDialog;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

import java.util.Arrays;

public class CloneDeletePanel extends HorizontalLayout {
    private static final String YES = "Yes";
    private static final String CANCEL = "Cancel";

    private final Navigator navigator;
    private final UISyncConfig config;
    private Callback callback;
    private final Services services;

    public CloneDeletePanel(Services services, Navigator navigator, UISyncConfig config,
                            Callback callback) {
        this.services = services;
        this.navigator = navigator;
        this.config = config;
        this.callback = callback;

        buildUI();
    }

    private void buildUI() {
        Button cloneButton = new Button("Clone");
        cloneButton.setDescription("Clone this config");
        cloneButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (callback.onCloneConfig()) {
                    showConfirmClonePage();
                }
            }
        });
        addComponent(cloneButton);

        Button deleteButton = new Button("Delete");
        deleteButton.setDescription("Delete this config from Task Adapter");
        deleteButton.addListener(new Button.ClickListener() {
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
                            navigator.show(Navigator.HOME);
                        }
                    }
                }
        );
        messageDialog.setWidth("175px");
        getWindow().addWindow(messageDialog);
    }

    public void showConfirmClonePage() {
        MessageDialog messageDialog = new MessageDialog(
                "Confirmation", "Clone this config?",
                Arrays.asList(YES, CANCEL),
                new MessageDialog.Callback() {
                    public void onDialogResult(String answer) {
                        if (YES.equals(answer)) {
                            final String userLoginName = services.getAuthenticator().getUserName();
                            try {
                                services.getUIConfigStore().cloneConfig(userLoginName, config);
                            } catch (StorageException e) {
                                //TODO !!! Add an error handler.
                                e.printStackTrace();
                            }
                            navigator.show(Navigator.HOME);
                        }
                    }
                }
        );
        messageDialog.setWidth("175px");
        getWindow().addWindow(messageDialog);
    }

    public interface Callback {
        boolean onCloneConfig();
    }
}

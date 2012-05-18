package com.taskadapter.webui;

import com.taskadapter.config.TAFile;
import com.taskadapter.web.MessageDialog;
import com.taskadapter.web.service.Services;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

import java.util.Arrays;

public class ConfigToolbarPanel extends HorizontalLayout {
    // TODO i18n
    private static final String YES = "Yes";
    private static final String CANCEL = "Cancel";

    private final Navigator navigator;
    private final TAFile file;

    public ConfigToolbarPanel(Navigator navigator, TAFile file) {
        this.navigator = navigator;
        this.file = file;
        buildUI();
    }

    private void buildUI() {
        setSpacing(true);
        Button cloneButton = new Button("Clone");
        cloneButton.setDescription("Clone this config");
        cloneButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                showConfirmClonePage(file);
            }
        });
        addComponent(cloneButton);

        Button deleteButton = new Button("Delete");
        deleteButton.setDescription("Delete this config from Task Adapter");
        deleteButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                showDeleteFilePage(file);
            }
        });
        addComponent(deleteButton);
    }

    private void showDeleteFilePage(final TAFile file) {
        MessageDialog messageDialog = new MessageDialog(
                "Confirmation", "Delete this config?",
                Arrays.asList(YES, CANCEL),
                new MessageDialog.Callback() {
                    public void onDialogResult(String answer) {
                        if (YES.equals(answer)) {
                            navigator.getServices().getConfigStorage().delete(file);
                            navigator.show(Navigator.CONFIGS);
                        }
                    }
                }
        );
        messageDialog.setWidth("175px");
        getWindow().addWindow(messageDialog);
    }

    public void showConfirmClonePage(final TAFile file) {
        MessageDialog messageDialog = new MessageDialog(
                "Confirmation", "Clone this config?",
                Arrays.asList(YES, CANCEL),
                new MessageDialog.Callback() {
                    public void onDialogResult(String answer) {
                        if (YES.equals(answer)) {
                            Services services = navigator.getServices();
                            String userLoginName = services.getAuthenticator().getUserName();
                            services.getConfigStorage().cloneConfig(userLoginName, file);
                            navigator.show(Navigator.CONFIGS);
                        }
                    }
                }
        );
        messageDialog.setWidth("175px");
        getWindow().addWindow(messageDialog);
    }

}

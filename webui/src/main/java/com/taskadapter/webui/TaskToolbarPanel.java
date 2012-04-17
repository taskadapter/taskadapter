package com.taskadapter.webui;

import com.taskadapter.config.TAFile;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.BaseTheme;

import java.util.Arrays;

public class TaskToolbarPanel extends HorizontalLayout {
    private Button cloneButton = new Button("Clone config");
    private final Navigator navigator;
    private final TAFile file;
    private Services services;

    public TaskToolbarPanel(Navigator navigator, TAFile file, Services services) {
        this.navigator = navigator;
        this.file = file;
        this.services = services;


        buildUI();
    }

    private void buildUI() {
        setSpacing(true);
        Button configureButton = new Button("Configure");
        configureButton.setStyleName(BaseTheme.BUTTON_LINK);
        configureButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                navigator.showConfigureTaskPage(file);
            }
        });
        addComponent(configureButton);

        cloneButton.setStyleName(BaseTheme.BUTTON_LINK);
        cloneButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                final String YES = "Yes";
                String NO = "No";

                getWindow().addWindow(new MessageDialog("Confirm Clone", "Clone the selected config?", Arrays.asList(YES, NO),
                        new MessageDialog.Callback() {
                            public void onDialogResult(String answer) {
                                if (answer.equals(YES)) {
                                    services.getConfigStorage().cloneConfig(file);
                                    navigator.show(Navigator.TASKS);
                                }
                            }
                        }
                ));
            }
        });
        addComponent(cloneButton);

        Button deleteButton = new Button("Delete config");
        deleteButton.setStyleName(BaseTheme.BUTTON_LINK);
        deleteButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                showDeletePage();
            }
        });
        addComponent(deleteButton);

    }

    private void showDeletePage() {
        navigator.showDeleteFilePage(file);
    }
}

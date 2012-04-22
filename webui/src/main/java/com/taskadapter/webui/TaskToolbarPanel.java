package com.taskadapter.webui;

import com.taskadapter.config.TAFile;
import com.taskadapter.webui.service.Services;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.BaseTheme;

import java.util.Arrays;

public class TaskToolbarPanel extends HorizontalLayout {
    private Button cloneButton = new Button("Clone config");
    private final Navigator navigator;
    private final TAFile file;

    public TaskToolbarPanel(Navigator navigator, TAFile file) {
        this.navigator = navigator;
        this.file = file;
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
                navigator.showConfirmClonePage(file);
            }
        });
        addComponent(cloneButton);

        Button deleteButton = new Button("Delete config");
        deleteButton.setStyleName(BaseTheme.BUTTON_LINK);
        deleteButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                navigator.showDeleteFilePage(file);
            }
        });
        addComponent(deleteButton);

    }
}

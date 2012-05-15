package com.taskadapter.webui;

import com.taskadapter.config.TAFile;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

public class ConfigToolbarPanel extends HorizontalLayout {
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
                navigator.showConfirmClonePage(file);
            }
        });
        addComponent(cloneButton);

        Button deleteButton = new Button("Delete");
        deleteButton.setDescription("Delete this config from Task Adapter");
        deleteButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                navigator.showDeleteFilePage(file);
            }
        });
        addComponent(deleteButton);
    }
}

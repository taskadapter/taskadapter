package com.taskadapter.webui;

import com.taskadapter.config.TAFile;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

public class ConfigToolbarPanel extends VerticalLayout {
    private Button cloneButton = new Button("Clone config");
    private final Navigator navigator;
    private final TAFile file;

    public ConfigToolbarPanel(Navigator navigator, TAFile file) {
        this.navigator = navigator;
        this.file = file;
        buildUI();
    }

    private void buildUI() {
        setSpacing(true);
        cloneButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.showConfirmClonePage(file);
            }
        });
        addComponent(cloneButton);

        Button deleteButton = new Button("Delete config");
        deleteButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                navigator.showDeleteFilePage(file);
            }
        });
        addComponent(deleteButton);
    }
}

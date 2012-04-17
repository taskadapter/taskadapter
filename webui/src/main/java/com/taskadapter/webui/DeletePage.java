package com.taskadapter.webui;

import com.taskadapter.config.TAFile;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class DeletePage extends Page {
    private VerticalLayout layout = new VerticalLayout();
    private TAFile file;

    private void buildUI() {
        layout.removeAllComponents();
        layout.addComponent(new Label("Delete config '" + file.getName() + "' ?"));
        Button deleteButton = new Button("Yes");
        deleteButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                services.getConfigStorage().delete(file);
                navigator.show(Navigator.TASKS);
            }
        });
        layout.addComponent(deleteButton);
    }

    public void setFile(TAFile file) {
        this.file = file;
    }

    @Override
    public String getPageTitle() {
        return file.getName();
    }

    @Override
    public Component getUI() {
        buildUI();
        return layout;
    }
}

package com.taskadapter.webui;

import com.taskadapter.config.ConfigStorage;
import com.taskadapter.config.TAFile;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class DeletePage extends Page {
    private PageManager pageManager;
    private ConfigStorage storage;
    private TAFile file;

    public DeletePage(PageManager pageManager, ConfigStorage storage, TAFile file) {
        this.pageManager = pageManager;
        this.storage = storage;
        this.file = file;
        buildUI();
    }

    private void buildUI() {
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(new Label("Delete config '" + file.getName() + "' ?"));
        Button deleteButton = new Button("Yes");
        deleteButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                storage.delete(file);
                pageManager.show(PageManager.TASKS);
            }
        });
        layout.addComponent(deleteButton);
        setCompositionRoot(layout);
    }

    @Override
    public String getPageTitle() {
        return file.getName();
    }
}

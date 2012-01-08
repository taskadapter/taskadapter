package com.taskadapter.webui;

import com.taskadapter.config.ConfigStorage;
import com.taskadapter.config.TAConfig;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class DeletePage extends Page {
    private PageManager pageManager;
    private ConfigStorage storage;
    private TAConfig config;

    public DeletePage(PageManager pageManager, ConfigStorage storage, TAConfig config) {
        this.pageManager = pageManager;
        this.storage = storage;
        this.config = config;
        buildUI();
    }

    private void buildUI() {
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(new Label("Delete config '" + config.getName() + "' ?"));
        Button deleteButton = new Button("Yes");
        deleteButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                storage.delete(config);
                pageManager.show(PageManager.TASKS);
            }
        });
        layout.addComponent(deleteButton);
        setCompositionRoot(layout);
    }

    @Override
    public String getNavigationPanelTitle() {
        return config.getName();
    }
}

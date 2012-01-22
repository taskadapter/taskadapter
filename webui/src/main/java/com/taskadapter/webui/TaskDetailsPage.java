package com.taskadapter.webui;

import com.taskadapter.PluginManager;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.config.TAFile;
import com.taskadapter.web.SettingsManager;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class TaskDetailsPage extends Page {
    private TAFile file;
    private PageManager pageManager;
    private ConfigStorage storage;
    private PluginManager pluginManager;
    private EditorManager editorManager;
    private SettingsManager settingsManager;
    private Label name;
    private VerticalLayout layout = new VerticalLayout();

    // TODO refactor this huge list of parameters!
    public TaskDetailsPage(TAFile file, PageManager pageManager, ConfigStorage storage, PluginManager pluginManager, EditorManager editorManager, SettingsManager settingsManager) {
        this.file = file;
        this.pageManager = pageManager;
        this.storage = storage;
        this.pluginManager = pluginManager;
        this.editorManager = editorManager;
        this.settingsManager = settingsManager;
        buildUI();
        setTask();
    }

    private void buildUI() {
        layout.setSpacing(true);
        name = new Label();
        layout.addComponent(name);
        layout.addComponent(new TaskToolbarPanel(pageManager, storage, file, pluginManager, editorManager, settingsManager));
        layout.addComponent(new TaskButtonsPanel(pluginManager, pageManager, file));
        setCompositionRoot(layout);
    }

    private void setTask() {
        name.setValue("Name : " + file.getName());
    }

    @Override
    public String getNavigationPanelTitle() {
        return file.getName();
    }
}

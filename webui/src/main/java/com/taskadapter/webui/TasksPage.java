package com.taskadapter.webui;

import com.taskadapter.PluginManager;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.config.StorageListener;
import com.taskadapter.config.TAFile;
import com.taskadapter.web.SettingsManager;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

/**
 * @author Alexey Skorokhodov
 */
public class TasksPage extends Page {
    public static final String ID = "tasks_list";

    private PageManager pageManager;
    private ConfigStorage configStorage;
    private PluginManager pluginManager;
    private EditorManager editorManager;
    private SettingsManager settingsManager;
    private Table table;

    // TODO refactor all these parameters into a factory!
    public TasksPage(PageManager pageManager, ConfigStorage configStorage, PluginManager pluginManager, EditorManager editorManager, SettingsManager settingsManager) {
        this.pageManager = pageManager;
        this.configStorage = configStorage;
        this.pluginManager = pluginManager;
        this.editorManager = editorManager;
        this.settingsManager = settingsManager;
        configStorage.setListener(new StorageListener() {
            @Override
            public void notifySomethingChanged() {
                reloadConfigs();
            }
        });
        buildUI();
        reloadConfigs();
    }

    private void buildUI() {
        VerticalLayout layout = new VerticalLayout();
        table = new Table();
        table.addStyleName("taskstable");
        table.addContainerProperty("Name", Button.class, null);
        layout.addComponent(table);
        setCompositionRoot(layout);
    }

    private void reloadConfigs() {
        table.removeAllItems();
        // for some reasons items are not added to the table without
        // specifying index.
        int i = 0;
        for (TAFile file : configStorage.getAllConfigs()) {
            addTask(file, i++);
        }
        table.setPageLength(table.size() + 1);
    }

    private void addTask(final TAFile file, int i) {
        final TaskDetailsPage page = new TaskDetailsPage(file, pageManager, configStorage, pluginManager, editorManager, settingsManager);
        pageManager.registerPage(PageManager.TASK_DETAILS_PAGE_ID_PREFFIX + file.getName(), page);

        Button button = new Button(file.getName());
        button.setStyleName(BaseTheme.BUTTON_LINK);
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                pageManager.show(page);
            }
        });
        table.addItem(new Object[]{button}, i);
    }

    @Override
    public String getPageTitle() {
        return "Tasks";
    }
}

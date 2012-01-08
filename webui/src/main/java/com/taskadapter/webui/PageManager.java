package com.taskadapter.webui;

import com.taskadapter.PluginManager;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.web.SettingsManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexey Skorokhodov
 */
public class PageManager {
    public static final String TASKS = "tasks_list";

    private Map<String, Page> pages = new HashMap<String, Page>();
    private AppManager appManager;

    public PageManager(ConfigStorage configStorage, AppManager appManager, PluginManager pluginManager, EditorManager editorManager, SettingsManager settingsManager) {
        this.appManager = appManager;
        registerPage(TASKS, new TasksPage(this, configStorage, pluginManager, editorManager, settingsManager));
    }

    public void registerPage(String id, Page page) {
        pages.put(id, page);
    }

    public Page getPage(String id) {
        return pages.get(id);
    }

    public void show(String pageId) {
        appManager.show(pages.get(pageId));
    }

    public void show(Page page) {
        appManager.show(page);
    }
}

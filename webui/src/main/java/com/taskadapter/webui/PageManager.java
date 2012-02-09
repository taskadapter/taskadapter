package com.taskadapter.webui;

import com.taskadapter.PluginManager;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.web.SettingsManager;
import com.vaadin.ui.Window;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexey Skorokhodov
 */
public class PageManager {
    public static final String TASKS = "tasks_list";
    private static final String LOGIN_PAGE = "login_page";

    private Map<String, Page> pages = new HashMap<String, Page>();
    private TAApplication application;
    private Authenticator authenticator;

    public PageManager(ConfigStorage configStorage, TAApplication application, PluginManager pluginManager, EditorManager editorManager, SettingsManager settingsManager) {
        this.application = application;
        this.authenticator = application.getAuthenticator();
        registerPage(TASKS, new TasksPage(this, configStorage, pluginManager, editorManager, settingsManager));
        // TODO refactor? maybe no need to pass "this"
        registerPage(LOGIN_PAGE, new LoginPage(authenticator, this));
    }

    public void registerPage(String id, Page page) {
        pages.put(id, page);
    }

    public Page getPage(String id) {
        return pages.get(id);
    }

    public void show(String pageId) {
        show(pages.get(pageId));
    }

    public void show(Page page) {
        if (!authenticator.isLoggedIn()) {
            application.show(pages.get(LOGIN_PAGE));
        } else {
            application.show(page);
        }
    }

    public Window getMainWindow() {
        return application.getMainWindow();
    }
}

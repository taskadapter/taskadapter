package com.taskadapter.webui;

import com.taskadapter.PluginManager;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.web.configeditor.EditorUtil;
import com.vaadin.ui.Window;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexey Skorokhodov
 */
public class PageManager {
    public static final String TASKS = "tasks_list";
    public static final String CONFIGURE_TASK_PAGE_ID_PREFFIX = "configure_task_page_";

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
        Page page = pages.get(pageId);
        if (page != null) {
            show(page);
        } else {
            EditorUtil.showError(getMainWindow(), "Internal error!", "Page \"" + pageId + "\" is not registered");
        }
    }

    public void show(Page page) {
/*
// TODO: uncomment for production
        if (!authenticator.isLoggedIn()) {
            application.show(pages.get(LOGIN_PAGE));
        } else {
            application.show(page);
        }
*/
        application.show(page);
    }

    public Window getMainWindow() {
        return application.getMainWindow();
    }
}

package com.taskadapter.webui;

import com.taskadapter.PluginManager;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.webui.license.LicensePage;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class LeftMenu extends VerticalLayout {
    private Resource ICON_TASKS = new ThemeResource("../../icons/tasks.png");
    private Resource ICON_ADD = new ThemeResource("../../icons/add.png");
    private Resource ICON_INFO = new ThemeResource("../../icons/info.png");

    private ConfigStorage configStorage;
    private PluginManager pluginManager;
    private EditorManager editorManager;
    private SettingsManager settingsManager;
    private PageManager pageManager;
    private MenuLinkBuilder builder;

    // TODO refactor this list of params!
    public LeftMenu(PageManager pageManager, ConfigStorage configStorage, PluginManager pluginManager, EditorManager editorManager, SettingsManager settingsManager) {
        this.pageManager = pageManager;
        this.configStorage = configStorage;
        this.pluginManager = pluginManager;
        this.editorManager = editorManager;
        this.settingsManager = settingsManager;
        builder = new MenuLinkBuilder(pageManager);
        buildUI();
    }

    private void buildUI() {
        setSpacing(true);

        addMenu(ICON_ADD, "New Task", new NewConfigPage(pageManager, configStorage, pluginManager, editorManager, settingsManager));
        addMenu(ICON_TASKS, "Tasks", pageManager.getPage(PageManager.TASKS));
        addMenu(ICON_INFO, "Info", new LicensePage());
    }

    private void addMenu(Resource icon, String caption, final Page page) {
        addComponent(builder.render(caption, page, icon));
    }
}

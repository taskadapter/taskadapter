package com.taskadapter.webui;

import com.taskadapter.PluginManager;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.web.SettingsManager;
import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;


/**
 * This is the web application entry point.
 *
 * @author Alexey Skorokhodov
 */
public class TAApplication extends Application {

    private HorizontalLayout navigationPanel;
    private Label updateMessage;

    private final Window mainWindow = new Window("Task Adapter");

    private HorizontalLayout currentComponentArea = new HorizontalLayout();
    private HorizontalLayout mainArea = new HorizontalLayout();
    private UpdateManager updateManager = new UpdateManager();
    private PluginManager pluginManager = new PluginManager();
    private EditorManager editorManager = new EditorManager();
    private ConfigStorage configStorage = new ConfigStorage(pluginManager);
    private MenuLinkBuilder menuLinkBuilder;
    private Page homePage = new HomePage();

    @Override
    public String getVersion() {
        return updateManager.getCurrentVersion();
    }

    @Override
    public void init() {
        setTheme("mytheme");

        SettingsManager settingsManager = new SettingsManager();

        PageManager pageManager = new PageManager(configStorage, this, pluginManager, editorManager, settingsManager);
        menuLinkBuilder = new MenuLinkBuilder(pageManager);

        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");

        Header header = new Header();
        header.setHeight("50px");
        header.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        layout.addComponent(header);

        navigationPanel = new HorizontalLayout();
        navigationPanel.setHeight("30px");
        navigationPanel.setWidth("100%");
        navigationPanel.setSpacing(true);
        layout.addComponent(navigationPanel);

        updateMessage = new Label();
        layout.addComponent(updateMessage);
        layout.setComponentAlignment(updateMessage, Alignment.MIDDLE_CENTER);

        LeftMenu leftMenu = new LeftMenu(pageManager, configStorage, pluginManager, editorManager, settingsManager);
        leftMenu.setWidth("120px");
        mainArea.addComponent(leftMenu);

        currentComponentArea.setSizeFull();

        mainArea.addComponent(currentComponentArea);
        mainArea.setExpandRatio(currentComponentArea, 1.0f);
        mainArea.setSizeFull();
        layout.addComponent(mainArea);
        layout.setComponentAlignment(mainArea, Alignment.TOP_LEFT);
        layout.setExpandRatio(mainArea, 1.0f);      // use all available space

        mainWindow.setContent(layout);
        setMainWindow(mainWindow);

        checkLastAvailableVersion();
        show(homePage);
    }

    public void show(Page page) {
        currentComponentArea.removeAllComponents();
        currentComponentArea.addComponent(page);
        currentComponentArea.setComponentAlignment(page, Alignment.TOP_LEFT);

        navigationPanel.removeAllComponents();

        navigationPanel.addComponent(menuLinkBuilder.createButtonLink("Home", homePage));
        navigationPanel.addComponent(new Label(page.getPageTitle()));
    }

    private void checkLastAvailableVersion() {
        UpdateManager updateManager = new UpdateManager();

        if (updateManager.isCurrentVersionOutdated()) {
            updateMessage.setCaption("There's a newer version of Task Adapter available for download. Your version: "
                    + updateManager.getCurrentVersion() + ". Last available version: " + updateManager.getLatestAvailableVersion());
        }
    }
}

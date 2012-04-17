package com.taskadapter.webui;

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

    private MenuLinkBuilder menuLinkBuilder;

    public LeftMenu(Navigator navigator) {
        menuLinkBuilder = new MenuLinkBuilder(navigator);
        buildUI();
    }

    private void buildUI() {
        setSpacing(true);

        addMenu(ICON_ADD, "New Task", Navigator.NEW_CONFIG_PAGE);
        addMenu(ICON_TASKS, "Tasks", Navigator.TASKS);
        addMenu(ICON_INFO, "Info", Navigator.LICENSE_PAGE);
        addMenu(ICON_INFO, "Feedback", Navigator.FEEDBACK_PAGE);
    }

    private void addMenu(Resource icon, String caption, final String pageId) {
        addComponent(menuLinkBuilder.createButtonLink(caption, pageId, icon));
    }
}

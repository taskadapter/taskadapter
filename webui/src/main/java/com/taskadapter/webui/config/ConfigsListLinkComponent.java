package com.taskadapter.webui.config;

import com.taskadapter.webui.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;

public class ConfigsListLinkComponent {

    public static Component render(Button.ClickListener listener) {
        final HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth(100, PERCENTAGE);

        final Button button = new Button(Page.message("editConfig.goToConfigsList"));
        button.setDescription(Page.message("editConfig.goToConfigsList.tooltip"));
        button.addClickListener(listener);

        layout.addComponent(button);
        layout.setComponentAlignment(button, Alignment.MIDDLE_CENTER);
        return layout;
    }
}

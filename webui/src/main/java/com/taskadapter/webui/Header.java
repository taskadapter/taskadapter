package com.taskadapter.webui;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * @author Alexey Skorokhodov
 */
public class Header extends HorizontalLayout {

    public Header() {
        buildMainLayout();
    }

    private void buildMainLayout() {
        setSpacing(true);
        addStyleName("header_panel");

        Label label = new Label("Task Adapter");
        label.addStyleName("header_logo_label");
        addComponent(label);
    }
}

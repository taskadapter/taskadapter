package com.taskadapter.webui;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

/**
 * @author Alexey Skorokhodov
 */
public class Header extends GridLayout {

    public Header() {
        buildMainLayout();
    }

    private void buildMainLayout() {
        setColumns(3);
        setRows(1);
        setSpacing(true);
        addStyleName("header_panel");

        Label label = new Label("Task Adapter");
        label.addStyleName("header_logo_label");
        addComponent(label);
    }
}

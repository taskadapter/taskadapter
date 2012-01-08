package com.taskadapter.webui;

import com.vaadin.ui.Alignment;
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

        Label label = new Label();
        label.setImmediate(false);
        label.setContentMode(Label.CONTENT_XHTML);
        label.setValue("<h2>Task Adapter</h2>");
        addComponent(label);
        setComponentAlignment(label, Alignment.MIDDLE_LEFT);
    }
}

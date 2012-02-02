package com.taskadapter.webui;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class HomePage extends Page {
    public HomePage() {
        buildUI();
    }

    private void buildUI() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.addComponent(new Label("Home page"));
        setCompositionRoot(layout);
    }

    @Override
    public String getPageTitle() {
        return "";
    }
}

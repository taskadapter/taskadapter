package com.taskadapter.webui;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import java.util.Arrays;
import java.util.List;

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
    public String getNavigationPanelTitle() {
        return "";
    }
}

package com.taskadapter.webui;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class HomePage extends Page {
    private VerticalLayout layout = new VerticalLayout();

    public HomePage() {
        buildUI();
    }

    private void buildUI() {
        layout.setWidth("100%");
        layout.addComponent(new Label("Home page"));
    }

    @Override
    public String getPageTitle() {
        return "";
    }

    @Override
    public Component getUI() {
        // TODO init
//        if (services.getAuthenticator().isLoggedIn()) {
//            layout.addComponent(new Label("Welcome, " + services.getAuthenticator().getUserName()));
//        }
        return layout;
    }
}

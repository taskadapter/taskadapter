package com.taskadapter.webui;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class HomePage extends Page {
    private Authenticator authenticator;

    public HomePage(Authenticator authenticator) {
        this.authenticator = authenticator;
        buildUI();
    }

    private void buildUI() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.addComponent(new Label("Home page"));
        if (authenticator.isLoggedIn()) {
            layout.addComponent(new Label("Welcome, " + authenticator.getUserName()));
        }
        setCompositionRoot(layout);
    }

    @Override
    public String getPageTitle() {
        return "";
    }
}

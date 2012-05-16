package com.taskadapter.webui;

import com.taskadapter.web.service.Services;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;

/**
 * @author Alexey Skorokhodov
 */
public abstract class Page {

    abstract public String getPageTitle();

    abstract public Component getUI();

    protected Navigator navigator;
    protected Services services;

    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    public void setServices(Services services) {
        this.services = services;
    }

    /**
     * Button which return to previous page
     * @param title button caption
     * @return instance
     */
    protected Button createBackButton(String title)
    {
        Button button = new Button(title);
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (navigator != null) {
                    navigator.back();
                }
            }
        });
        return button;
    }

}

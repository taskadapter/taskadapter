package com.taskadapter.webui;

import com.vaadin.terminal.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.BaseTheme;

/**
 * @author Alexey Skorokhodov
 */
// TODO delete this class
public class MenuLinkBuilder {
    private Navigator navigator;

    public MenuLinkBuilder(Navigator navigator) {
        this.navigator = navigator;
    }

    public Button createButtonLink(String caption, final String pageId) {
        return createButtonLink(caption, pageId, null);
    }

    public Button createButtonLink(String caption, final String pageId, Resource icon) {
        Button button = new Button(caption);
        button.setStyleName(BaseTheme.BUTTON_LINK);
        button.setIcon(icon);
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.show(pageId);
            }
        });

        return button;
    }
}

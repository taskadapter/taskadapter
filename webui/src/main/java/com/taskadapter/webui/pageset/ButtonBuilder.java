package com.taskadapter.webui.pageset;

import com.vaadin.ui.Button;
import com.vaadin.ui.themes.BaseTheme;

import static com.taskadapter.webui.Page.message;

public class ButtonBuilder {
    static Button createSupportButton() {
        final Button supportButton = new Button(message("headerMenu.support"));
        supportButton.setStyleName(BaseTheme.BUTTON_LINK);
        supportButton.addStyleName("menu");
        return supportButton;
    }
}

package com.taskadapter.webui;

import com.vaadin.ui.Button;

public class ButtonBuilder {
    public static Button createBackButton(final Navigator navigator, String buttonLabel) {
        Button button = new Button(buttonLabel);
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.back();
            }
        });
        return button;
    }
}

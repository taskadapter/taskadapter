package com.taskadapter.webui.config;

import com.taskadapter.web.data.Messages;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Runo;

public final class ExportButtonsFragment {
    private static Button createButton(Messages messages, String imageFile,
            final Runnable handler) {

        final Button button = new Button();
        button.setIcon(new ThemeResource(imageFile));
        button.setStyleName(Runo.BUTTON_SMALL);
        button.addStyleName("exportLeftRightButton");
        button.setDescription(messages.get("export.exportButtonTooltip"));
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                handler.run();
            }
        });
        return button;
    }

    public static Component render(Messages messages, Runnable exportToLeft,
            Runnable exportToRight) {
        final HorizontalLayout res = new HorizontalLayout();
        res.setSpacing(true);
        res.addComponent(createButton(messages, "img/arrow_left.png",
                exportToLeft));
        res.addComponent(createButton(messages, "img/arrow_right.png",
                exportToRight));

        return res;
    }
}

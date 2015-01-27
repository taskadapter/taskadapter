package com.taskadapter.webui;

import com.taskadapter.data.DataCallback;
import com.taskadapter.data.State;
import com.taskadapter.data.States;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

import static com.taskadapter.webui.Page.message;

/**
 * Top-level header shown in the web UI. Includes logo, "configure", "logout"
 * and other buttons.
 */
public final class Header  {
    private static final String MAIN_WIDTH = "900px";

    /**
     * Renders a navigator using provided menu components.
     * 
     * @param home
     *            home click handler.
     * @param menu1
     *            first menu.
     * @param menu2
     *            second menu.
     * @return rendered component.
     */
    public static Component render(Runnable home, Component menu1,
            Component menu2, State<Boolean> licensed) {

        final HorizontalLayout res = new HorizontalLayout();

        final HorizontalLayout internalLayout = new HorizontalLayout();
        internalLayout.setWidth(MAIN_WIDTH);
        internalLayout.setSpacing(true);
        res.addComponent(internalLayout);
        res.setComponentAlignment(internalLayout, Alignment.MIDDLE_CENTER);
        res.setSpacing(true);
        res.addStyleName("header-panel");

        /* Logo. */
        final Button logo = createButtonLink("Task Adapter", home, "logo");
        internalLayout.addComponent(logo);
        internalLayout.setExpandRatio(logo, 2f);

        /* First menu. */
        internalLayout.addComponent(menu1);
        internalLayout.setExpandRatio(menu1, 1f);
        internalLayout.setComponentAlignment(menu1, Alignment.MIDDLE_CENTER);

        /* Second menu. */
        internalLayout.addComponent(menu2);
        internalLayout.setComponentAlignment(menu2, Alignment.MIDDLE_RIGHT);

        /* Trial display. */
        final VerticalLayout trialLayout = new VerticalLayout();
        trialLayout.setSizeFull();
        Label trialLabel = new Label(message("header.trialMode"));
        trialLabel.setDescription(message("header.trialModeWillOnlyTransfer"));
        trialLabel.setSizeUndefined();
        trialLabel.addStyleName("trial-mode-label");
        trialLayout.addComponent(trialLabel);
        trialLayout.setComponentAlignment(trialLabel, Alignment.MIDDLE_CENTER);

        States.onValue(licensed, new DataCallback<Boolean>() {
            @Override
            public void callBack(Boolean data) {
                trialLayout.setVisible(!data);
            }
        });

        internalLayout.addComponent(trialLayout);
        internalLayout.setExpandRatio(trialLayout, 1f);

        return res;
    }

    private static Button createButtonLink(String caption, final Runnable page,
            String additionalStyle) {
        final Button.ClickListener handler = new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                page.run();
            }
        };
        return createButtonLink(caption, additionalStyle, handler);
    }

    private static Button createButtonLink(String caption,
            String additionalStyle, final Button.ClickListener handler) {
        Button button = new Button(caption);
        button.setStyleName(BaseTheme.BUTTON_LINK);
        button.addStyleName(additionalStyle);
        button.addClickListener(handler);
        return button;
    }
}

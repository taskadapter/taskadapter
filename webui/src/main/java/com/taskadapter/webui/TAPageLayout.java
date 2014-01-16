package com.taskadapter.webui;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.server.Sizeable.Unit.PIXELS;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

/**
 * Task-adapter page layout.
 * 
 */
public final class TAPageLayout {

    private static final String MAIN_WIDTH = "900px";

    /**
     * Layouts a page.
     * 
     * @param header
     *            header component.
     * @param content
     *            content component.
     */
    public static Component layoutPage(Component header, Component content) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setWidth(100, Unit.PERCENTAGE);

        header.setHeight(50, PIXELS);
        header.setWidth(100, PERCENTAGE);
        layout.addComponent(header);

        final HorizontalLayout navigationPanel = new HorizontalLayout();
        navigationPanel.setHeight(30, PIXELS);
        navigationPanel.setSpacing(true);
        layout.addComponent(navigationPanel);
        layout.setComponentAlignment(navigationPanel, Alignment.MIDDLE_CENTER);

        final Layout mainArea = new CssLayout();
        mainArea.setStyleName("no-shadow");
        mainArea.setWidth(MAIN_WIDTH);

        // container for currentComponentArea to be aligned in mainArea
        // correctly
        final VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        verticalLayout.addComponent(content);

        mainArea.addComponent(verticalLayout);
        verticalLayout.setComponentAlignment(content, Alignment.MIDDLE_CENTER);

        layout.addComponent(mainArea);
        layout.setComponentAlignment(mainArea, Alignment.MIDDLE_CENTER);

        return layout;

    }
}

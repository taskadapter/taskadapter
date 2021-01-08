package com.taskadapter.webui;

import com.taskadapter.webui.pages.AppUpdateNotificationComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public final class TAPageLayout {

    public static Component layoutPage(Component header, AppUpdateNotificationComponent updateNotificationComponent, Component content) {
        VerticalLayout layout = new VerticalLayout();
//        layout.setWidth(100, Unit.PERCENTAGE);

//        header.setHeight(50, PIXELS);
//        header.setWidth(100, PERCENTAGE);
        layout.add(header);

        HorizontalLayout navigationPanel = new HorizontalLayout();
//        navigationPanel.setHeight(30, PIXELS);
        navigationPanel.setSpacing(true);
        layout.add(navigationPanel);
//        layout.setComponentAlignment(navigationPanel, Alignment.MIDDLE_CENTER);

        Component updaterUi = updateNotificationComponent.ui();
        layout.add(updaterUi);
//        layout.setComponentAlignment(updaterUi, Alignment.TOP_CENTER);

//        Layout mainArea = new CssLayout();
//        mainArea.setClassName("no-shadow");
//        mainArea.setWidth(Sizes.mainWidth());

        // container for currentComponentArea to be aligned in mainArea correctly
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(content);

//        mainArea.add(verticalLayout);
//        verticalLayout.setComponentAlignment(content, Alignment.MIDDLE_CENTER);

//        layout.add(mainArea);
//        layout.setComponentAlignment(mainArea, Alignment.MIDDLE_CENTER);

        return layout;

    }
}

package com.taskadapter.webui.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

public class LayoutsUtil {
    /**
     * create a horizontal layout with the provided element in the center.
     */
    public static HorizontalLayout centeredLayout(VerticalLayout component, String componentWidth) {
        var layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        // DEBUG - Visually display the  bounds of this layout.
        // layout.getStyle().set("border", "2px dotted DarkOrange");

        component.setWidth(componentWidth);
        layout.add(component);
        return layout;
    }

    /**
     * create a horizontal layout with the provided element in the center.
     */
    public static HorizontalLayout centered(String width, List<Component> components) {
        return centered(width, components.toArray(Component[]::new));
    }

    /**
     * create a horizontal layout with the provided element in the center.
     */
    public static HorizontalLayout centered(String width, Component... components) {
        var verticalLayout = new VerticalLayout(components);
        verticalLayout.addClassName("grayBackgroundPanel");
        return centeredLayout(verticalLayout, width);
    }
}

package com.taskadapter.web.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

public final class Grids {
    /**
     * Adds a component into a layout with a specified alignment.
     *
     * @return <code>layout</code> to allow chaining calls.
     */
    public static FormLayout addTo(FormLayout layout, //Alignment alignment,
                                   Component comp) {
//        layout.add(comp);
//        layout.setComponentAlignment(comp, alignment);
        return layout;
    }

    /**
     * Adds a component into a layout with a specified alignment and position.
     * 
     * @param layout
     *            grid layout.
     * @param col
     *            component coumn.
     * @param row
     *            component row.
     * @param alignment
     *            aligmnent.
     * @param comp
     *            component to add.
     * @return <code>layout</code> to allow chaining calls.
     */
    public static FormLayout addTo(FormLayout layout, int col, int row,
                                   FlexComponent.Alignment alignment, Component comp) {
//        layout.add(comp, col, row);
//        layout.setComponentAlignment(comp, alignment);
        return layout;
    }

}

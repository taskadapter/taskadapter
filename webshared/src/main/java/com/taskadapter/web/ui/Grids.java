package com.taskadapter.web.ui;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;

/**
 * Grid layout utilities.
 * 
 */
public final class Grids {
    /**
     * Adds a component into a layout with a specified alignment.
     * 
     * @param layout
     *            grid layout.
     * @param alignment
     *            aligmnent.
     * @param comp
     *            component to add.
     * @return <code>layout</code> to allow chaining calls.
     */
    public static GridLayout addTo(GridLayout layout, Alignment alignment,
            Component comp) {
        layout.addComponent(comp);
        layout.setComponentAlignment(comp, alignment);
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
    public static GridLayout addTo(GridLayout layout, int col, int row,
            Alignment alignment, Component comp) {
        layout.addComponent(comp, col, row);
        layout.setComponentAlignment(comp, alignment);
        return layout;
    }

}

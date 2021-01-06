package com.taskadapter.web.ui;

import com.taskadapter.vaadin14shim.GridLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;

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
        layout.add(comp);
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
        layout.add(comp, col, row);
        layout.setComponentAlignment(comp, alignment);
        return layout;
    }

}

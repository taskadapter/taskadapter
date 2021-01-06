package com.taskadapter.vaadin14shim;

import com.vaadin.ui.Component;

public class GridLayout extends com.vaadin.ui.GridLayout {
    public GridLayout() {
        super();
    }

    public GridLayout(int columns, int rows) {
        super(columns, rows);
    }

    public void add(Component c) {
        addComponent(c);
    }

    public void add(Component component, int column, int row) {
        addComponent(component, column, row);
    }

    public void add(Component component, int column1, int row1,
                             int column2, int row2) {
        addComponent(component, column1, row1, column2, row2);
    }

    public void setClassName(String c) {
        setStyleName(c);
    }

    public void removeAll() {
        removeAllComponents();
    }
}

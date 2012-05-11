package com.taskadapter.connector.msp;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class MSPInfoPanel extends Panel {
    private Label durationText;
    private Label workText;

    public MSPInfoPanel() {
        super("MSP Text Fields to store some internal stuff");
        buildUI();
    }

    private void buildUI() {
        addStyleName("panelexample");
        GridLayout layout = new GridLayout();
        layout.setWidth(100, UNITS_PERCENTAGE);
        layout.setColumns(2);
        layout.setMargin(true);
        layout.setSpacing(true);

        layout.addComponent(new Label("Store 'Duration undefined' flag as:"));
        durationText = new Label();
        layout.addComponent(durationText);
        layout.setComponentAlignment(durationText, Alignment.MIDDLE_RIGHT);

        layout.addComponent(new Label("Store 'Work undefined' flag as:"));
        workText = new Label();
        layout.addComponent(workText);
        addComponent(layout);
    }

    public void setDurationValue(String value) {
        durationText.setValue(value);
    }

    public void setWorkValue(String value) {
        workText.setValue(value);
    }
}

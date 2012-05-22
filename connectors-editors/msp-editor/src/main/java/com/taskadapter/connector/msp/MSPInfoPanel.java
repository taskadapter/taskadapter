package com.taskadapter.connector.msp;

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
        GridLayout layout = new GridLayout();
        //layout.setColumnExpandRatio(0, 0.7f);
        layout.setWidth(100, UNITS_PERCENTAGE);
        layout.setColumns(2);
        layout.setRows(2);
        layout.setMargin(true);
        layout.setSpacing(true);

        Label label = new Label("Store 'Duration undefined' flag as:");
        label.setWidth("200px");
        layout.addComponent(label, 0, 0);
        durationText = new Label();
        layout.addComponent(durationText, 1, 0);
        //layout.setComponentAlignment(durationText, Alignment.MIDDLE_RIGHT);

        label = new Label("Store 'Work undefined' flag as:");
        label.setWidth("200px");
        layout.addComponent(label, 0, 1);
        workText = new Label();
        layout.addComponent(workText, 1, 1);
        addComponent(layout);
    }

    public void setDurationValue(String value) {
        durationText.setValue(value);
    }

    public void setWorkValue(String value) {
        workText.setValue(value);
    }
}

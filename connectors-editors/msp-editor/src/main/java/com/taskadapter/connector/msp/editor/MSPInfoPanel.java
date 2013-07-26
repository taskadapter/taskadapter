package com.taskadapter.connector.msp.editor;

import com.taskadapter.connector.msp.write.MSPDefaultFields;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;

public class MSPInfoPanel extends Panel {
	private static final long serialVersionUID = 1L;

	public MSPInfoPanel() {
        super("MSP Text Fields to store some internal stuff");
        buildUI();
    }

    private void buildUI() {
        GridLayout layout = new GridLayout();
        layout.setWidth(100, PERCENTAGE);
        layout.setColumns(2);
        layout.setRows(2);
        layout.setMargin(true);
        layout.setSpacing(true);

        Label label = new Label("Store 'Duration undefined' flag as:");
        label.setWidth("200px");
        layout.addComponent(label, 0, 0);
        final Label durationText = new Label();
        durationText.setValue(MSPDefaultFields.FIELD_DURATION_UNDEFINED.toString());
        layout.addComponent(durationText, 1, 0);

        label = new Label("Store 'Work undefined' flag as:");
        label.setWidth("200px");
        layout.addComponent(label, 0, 1);
        final Label workText = new Label();
        workText.setValue(MSPDefaultFields.FIELD_WORK_UNDEFINED.toString());
        layout.addComponent(workText, 1, 1);
        setContent(layout);
    }
}

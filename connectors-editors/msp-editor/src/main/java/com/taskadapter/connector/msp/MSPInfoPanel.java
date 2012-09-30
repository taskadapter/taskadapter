package com.taskadapter.connector.msp;

import com.taskadapter.connector.msp.write.MSXMLFileWriter;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class MSPInfoPanel extends Panel {
	private static final long serialVersionUID = 1L;

	public MSPInfoPanel(MSPConfig config) {
        super("MSP Text Fields to store some internal stuff");
        buildUI(config);
    }

    private void buildUI(MSPConfig config) {
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
        final Label durationText = new Label();
        durationText.setValue(MSXMLFileWriter.FIELD_DURATION_UNDEFINED.toString());
        layout.addComponent(durationText, 1, 0);
        //layout.setComponentAlignment(durationText, Alignment.MIDDLE_RIGHT);

        label = new Label("Store 'Work undefined' flag as:");
        label.setWidth("200px");
        layout.addComponent(label, 0, 1);
        final Label workText = new Label();
        workText.setValue(MSXMLFileWriter.FIELD_WORK_UNDEFINED.toString());
        layout.addComponent(workText, 1, 1);
        addComponent(layout);
    }
}

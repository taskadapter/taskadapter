package com.taskadapter.connector.msp.editor;

import com.taskadapter.connector.msp.write.MSPDefaultFields;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class MSPInfoPanel extends VerticalLayout {
	private static final long serialVersionUID = 1L;

	public MSPInfoPanel() {
//        super("MSP Text Fields to store some internal stuff");
        buildUI();
    }

    private void buildUI() {
       /* GridLayout layout = new GridLayout();
        layout.setWidth(100, PERCENTAGE);
        layout.setColumns(2);
        layout.setRows(2);
        layout.setMargin(true);
        layout.setSpacing(true);

        Label label = new Label("Store 'Duration undefined' flag as:");
        label.setWidth("200px");
        layout.add(label, 0, 0);
        final Label durationText = new Label();
        durationText.setText(MSPDefaultFields.FIELD_DURATION_UNDEFINED.toString());
        layout.add(durationText, 1, 0);

        label = new Label("Store 'Work undefined' flag as:");
        label.setWidth("200px");
        layout.add(label, 0, 1);
        final Label workText = new Label();
        workText.setText(MSPDefaultFields.FIELD_WORK_UNDEFINED.toString());
        layout.add(workText, 1, 1);
        setContent(layout);*/
    }
}

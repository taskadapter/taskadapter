package com.taskadapter.connector.msp.editor;

import com.taskadapter.connector.msp.write.MSPDefaultFields;
import com.taskadapter.web.configeditor.EditorUtil;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class MSPInfoPanel extends VerticalLayout {

    public MSPInfoPanel() {
        buildUI();
    }

    private void buildUI() {
        var caption = EditorUtil.createCaption("MSP Text Fields to store some internal stuff");
        var layout = new FormLayout();

        var label = new Label("Store 'Duration undefined' flag as:");
        var durationText = new Label(MSPDefaultFields.FIELD_DURATION_UNDEFINED.toString());

        layout.add(label, durationText);
        layout.add(new Label("Store 'Work undefined' flag as:"),
                new Label(MSPDefaultFields.FIELD_WORK_UNDEFINED.toString()));

        add(caption, layout);
    }
}

package com.taskadapter.connector.mantis.editor;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * Author: Alexander Kulik
 * Date: 21.05.12 23:10
 */
public class OtherMantisFieldsPanel extends Panel {
    private static final String DEFAULT_PANEL_CAPTION = "Additional Info";
    private final VerticalLayout verticalLayout = new VerticalLayout();
    private final MantisEditor editor;

    public OtherMantisFieldsPanel(MantisEditor editor) {
        this.editor = editor;
        buildUI();
    }

    private void buildUI() {
        setCaption(DEFAULT_PANEL_CAPTION);
        addComponent(verticalLayout);
        verticalLayout.setSpacing(true);

        verticalLayout.addComponent(editor.createFindUsersElementIfNeeded());
        final CheckBox checkBox = new CheckBox("Save issue relations (follows/precedes)"); //TODO check because unused
        checkBox.setEnabled(false);
        verticalLayout.addComponent(checkBox);
    }
}

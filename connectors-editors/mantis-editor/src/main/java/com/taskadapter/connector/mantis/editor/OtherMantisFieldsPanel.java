package com.taskadapter.connector.mantis.editor;

import com.taskadapter.connector.mantis.MantisConfig;
import com.taskadapter.web.configeditor.Editors;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class OtherMantisFieldsPanel extends Panel {
    private static final String DEFAULT_PANEL_CAPTION = "Additional Info";

    public OtherMantisFieldsPanel(MantisConfig config) {
        final VerticalLayout verticalLayout = new VerticalLayout();
        setCaption(DEFAULT_PANEL_CAPTION);
        addComponent(verticalLayout);
        verticalLayout.setSpacing(true);

        verticalLayout.addComponent(Editors.createFindUsersElement(new MethodProperty<Boolean>(config, "findUserByName")));
    }
}

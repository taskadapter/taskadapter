package com.taskadapter.connector.mantis.editor;

import com.taskadapter.connector.mantis.MantisConfig;
import com.taskadapter.web.configeditor.EditorUtil;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;

public class OtherMantisFieldsPanel extends VerticalLayout {
    private static String DEFAULT_PANEL_CAPTION = "Additional Info";

    private final Binder<MantisConfig> binder;

    public OtherMantisFieldsPanel(Binder<MantisConfig> binder) {
        this.binder = binder;
        buildUi();
    }

    private void buildUi() {
        setSpacing(true);

        Checkbox findUserByName = EditorUtil.checkbox("Find users based on assignee's name",
                "This option can be useful when you need to export a new MSP project file to Redmine/JIRA/MantisBT/....\n"
                        + "Task Adapter can load the system's users by resource names specified in the MSP file\n"
                        + "and assign the new tasks to them.\n"
                        + "Note: this operation usually requires 'Admin' permission in the system.",
                binder, "findUserByName");


        add(
                new Label(DEFAULT_PANEL_CAPTION),
                findUserByName
        );
    }

}

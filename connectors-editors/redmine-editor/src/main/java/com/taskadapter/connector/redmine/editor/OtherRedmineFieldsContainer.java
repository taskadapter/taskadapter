package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.web.configeditor.DefaultPanel;
import com.taskadapter.web.configeditor.Editors;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Panel;

public class OtherRedmineFieldsContainer extends Panel {
    private static final String OTHER_PANEL_CAPTION = "Additional Info";
    private static final String SAVE_ISSUE_LABEL = "Save issue relations (follows/precedes)";

    public OtherRedmineFieldsContainer(RedmineConfig config) {
        super(OTHER_PANEL_CAPTION);
        buildUI(config);
    }

    private void buildUI(final RedmineConfig config) {
        setWidth(DefaultPanel.WIDE_PANEL_WIDTH);
        setHeight("157px");
        addComponent(Editors.createFindUsersElement(new MethodProperty<Boolean>(config, "findUserByName")));
        final CheckBox saveRelations = new CheckBox(SAVE_ISSUE_LABEL);
        saveRelations.setPropertyDataSource(new MethodProperty<Boolean>(config, "saveIssueRelations"));
        addComponent(saveRelations);
    }
}
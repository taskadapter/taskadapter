package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.redmineapi.NotFoundException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
import com.taskadapter.web.configeditor.*;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Panel;

import java.util.List;

public class RedmineEditor extends TwoColumnsConfigEditor {

    public RedmineEditor(ConnectorConfig config, Services services) {
        super(config, services);

        buildUI();
    }

    @SuppressWarnings("unchecked")
    private void buildUI() {
        OtherRedmineFieldsPanel otherPanel = new OtherRedmineFieldsPanel((RedmineConfig) config);
        addToLeftColumn(otherPanel);
    }

    class OtherRedmineFieldsPanel extends Panel {
        private static final String OTHER_PANEL_CAPTION = "Additional Info";
        private static final String SAVE_ISSUE_LABEL = "Save issue relations (follows/precedes)";

        public OtherRedmineFieldsPanel(RedmineConfig config) {
            super(OTHER_PANEL_CAPTION);
            buildUI(config);
        }

        private void buildUI(final RedmineConfig config) {
            setWidth(DefaultPanel.WIDE_PANEL_WIDTH);
            setHeight("157px");

            setSpacing(true);
            setMargin(true);

            addComponent(Editors
                    .createFindUsersElement(new MethodProperty<Boolean>(config,
                            "findUserByName")));

            final CheckBox saveRelations = new CheckBox(SAVE_ISSUE_LABEL);
            saveRelations.setPropertyDataSource(new MethodProperty<Boolean>(
                    config, "saveIssueRelations"));
            addComponent(saveRelations);
        }
    }
}

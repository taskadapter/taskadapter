package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.configeditor.EditorUtil;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import java.util.List;

/**
 * Panel with title: "Set when exporting to Jira"
 */
class OtherJiraFieldsPanel extends Panel {
    private static final String SAVE_GROUP_LABEL = "Set when exporting to Jira";

    private final JiraConfig config;
    private final WindowProvider windowProvider;
    private final ExceptionFormatter exceptionFormatter;

    public OtherJiraFieldsPanel(WindowProvider windowProvider, JiraConfig config, ExceptionFormatter exceptionFormatter) {
        this.windowProvider = windowProvider;
        this.config = config;
        this.exceptionFormatter = exceptionFormatter;
        buildUI();
    }

    private void buildUI() {
        setCaption(SAVE_GROUP_LABEL);

        GridLayout lookupButtonsLayout = new GridLayout(3, 4);
        lookupButtonsLayout.setMargin(true);
        lookupButtonsLayout.setSpacing(true);
        addLookupButtonsAndTextEdit(lookupButtonsLayout);

        addComponent(lookupButtonsLayout);
    }

    private void addLookupButtonsAndTextEdit(GridLayout grid) {
        final TextField jiraComponent = EditorUtil.addLabeledText(
                grid, "Project Component:",
                "Component inside the Jira project");
        final MethodProperty<String> componentProperty = new MethodProperty<String>(config, "component");
        jiraComponent.setPropertyDataSource(componentProperty);
        Button showComponentsButton = EditorUtil.createLookupButton(
                windowProvider,
                "...",
                "Show list of available components on the given server.",
                "Select component",
                "List of available components on the server",
                new DataProvider<List<? extends NamedKeyedObject>>() {
                    @Override
                    public List<? extends NamedKeyedObject> loadData() throws ConnectorException {
                        return new JiraConnector(config).getComponents();
                    }
                },
                componentProperty,
                true, exceptionFormatter
        );
        grid.addComponent(showComponentsButton);
        grid.setComponentAlignment(showComponentsButton, Alignment.MIDDLE_CENTER);


        final TextField affectedVersion = EditorUtil
                .addLabeledText(grid,
                        "Set 'Affected version' to:",
                        "Set this 'affected version' value when submitting issues to Jira.");
        final MethodProperty<String> affectedVersionProperty = new MethodProperty<String>(config, "affectedVersion");
        affectedVersion.setPropertyDataSource(affectedVersionProperty);
        final DataProvider<List<? extends NamedKeyedObject>> versionProvider = new DataProvider<List<? extends NamedKeyedObject>>() {
            @Override
            public List<? extends NamedKeyedObject> loadData()
                    throws ConnectorException {
                return new JiraConnector(config).getVersions();
            }
        };
        Button showAffectedVersion = EditorUtil.createLookupButton(
                windowProvider,
                "...",
                "Show list of available versions",
                "Select version",
                "List of available versions",
                versionProvider,
                affectedVersionProperty,
                true,
                exceptionFormatter
        );
        grid.addComponent(showAffectedVersion);

        final TextField fixForVersion = EditorUtil
                .addLabeledText(grid,
                        "Set 'Fix for version' to:",
                        "Set this 'fix for version' value when submitting issues to Jira.");
        final MethodProperty<String> fixForProperty = new MethodProperty<String>(config, "fixForVersion");
        fixForVersion.setPropertyDataSource(fixForProperty);
        Button showFixForVersion = EditorUtil.createLookupButton(
                windowProvider,
                "...",
                "Show list of available versions",
                "Select version",
                "List of available versions",
                versionProvider,
                fixForProperty,
                true, exceptionFormatter
        );
        grid.addComponent(showFixForVersion);


        final TextField defaultTaskType = EditorUtil
                .addLabeledText(grid, "Default issue type:",
                        "New issues will be created with this 'issue type' (bug/improvement/task...)");
        final MethodProperty<String> defaultTaskTypeProperty = new MethodProperty<String>(config, "defaultTaskType");
        defaultTaskType.setPropertyDataSource(defaultTaskTypeProperty);
        Button showDefaultTaskType = EditorUtil.createLookupButton(
                windowProvider,
                "...",
                "Show list of available issue types on the Jira server",
                "Select issue type",
                "List of available issue types on the Jira server",
                new DataProvider<List<? extends NamedKeyedObject>>() {
                    @Override
                    public List<? extends NamedKeyedObject> loadData() throws ConnectorException {
                        return new JiraConnector(config).getIssueTypes();
                    }
                },
                defaultTaskTypeProperty,
                true,
                exceptionFormatter
        );
        grid.addComponent(showDefaultTaskType);
    }
}

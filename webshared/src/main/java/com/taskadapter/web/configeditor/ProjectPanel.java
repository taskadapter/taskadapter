package com.taskadapter.web.configeditor;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.ProjectInfo;
import com.taskadapter.connector.definition.ValidationException;
import com.vaadin.ui.*;

import java.util.Collection;

/**
 * "Project info" panel with Project Key, Query Id.
 *
 * @author Alexey Skorokhodov
 */
public class ProjectPanel extends Panel implements Validatable {
    private static final String PANEL_CAPTION = "Project Info";
    private static final int COLUMNS_NUMBER = 4;

    private final ConfigEditor editor;
    private TextField projectKey;
    private TextField queryId;

    private final ProjectProcessor projectProcessor;

    /**
     * Config Editors should NOT create this object directly, use ConfigEditor.addProjectPanel() method instead.
     *
     * @see ConfigEditor#addProjectPanel(ConfigEditor, ProjectProcessor)
     */
    public ProjectPanel(ConfigEditor editor, ProjectProcessor projectProcessor) {
        super(PANEL_CAPTION);
        this.editor = editor;
        this.projectProcessor = projectProcessor;
        init();
    }

    private void init() {
        addStyleName("panelexample");
        setWidth(DefaultPanel.NARROW_PANEL_WIDTH);
        GridLayout layout = new GridLayout();
        addComponent(layout);
        layout.setColumns(COLUMNS_NUMBER);
        layout.setMargin(true);
        layout.setSpacing(true);

        Label projectKeyLabel = new Label("Project key:");
        layout.addComponent(projectKeyLabel);
        layout.setComponentAlignment(projectKeyLabel, Alignment.MIDDLE_LEFT);

        projectKey = new TextField();
        layout.addComponent(projectKey);

        Collection<ProjectProcessor.EditorFeature> features = projectProcessor.getSupportedFeatures();

        Button infoButton = EditorUtil.createButton("Info", "View the project info",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        loadProject();
                    }
                }
        );
        infoButton.setEnabled(features.contains(ProjectProcessor.EditorFeature.LOAD_PROJECT_INFO));
        layout.addComponent(infoButton);

        LookupOperation loadProjectsOperation = new LoadProjectsOperation(editor, projectProcessor.getDescriptor().getPluginFactory());
        Button projectKeyButton = EditorUtil.createLookupButton(
                editor,
                "...",
                "Show list of available projects on the server.",
                "Select project",
                "List of projects on the server",
                loadProjectsOperation,
                projectKey,
                false
        );
        projectKeyButton.setEnabled(features.contains(ProjectProcessor.EditorFeature.LOAD_PROJECTS));
        layout.addComponent(projectKeyButton);

        Label queryIdLabel = new Label("Query ID:");
        layout.addComponent(queryIdLabel);
        layout.setComponentAlignment(queryIdLabel, Alignment.MIDDLE_LEFT);

        queryId = new TextField();
        queryId.setDescription("Custom query/filter ID (number). You need to create a query on the server before accessing it from here.\n"
                + "Read help for more details.");
        layout.addComponent(queryId);

        LookupOperation loadSavedQueriesOperation = projectProcessor.getLoadSavedQueriesOperation(editor);

        Button showQueriesButton = EditorUtil.createLookupButton(
                editor,
                "...",
                "Show available saved queries on the server.",
                "Select Query",
                "List of saved queries on the server",
                loadSavedQueriesOperation,
                queryId,
                false
        );
        // TODO maybe set "enabled" basing on whether or not loadSavedQueriesOperation is NULL?
        // then can delete the whole "features" mechanism
        showQueriesButton.setEnabled(features.contains(ProjectProcessor.EditorFeature.LOAD_SAVED_QUERIES));
        layout.addComponent(showQueriesButton);
    }

    private void loadProject() {
        try {
            if (getProjectKey().isEmpty()) {
                // TODO need to use the field value and
                // disable the button
                // instead of this simple check
                throw new ValidationException("Please, provide the project key first");
            }

            projectProcessor.loadProject(getProjectKey());

        } catch (ValidationException e) {
            editor.getWindow().showNotification("Please, update the settings", e.getMessage());
        }
    }

    public ProjectInfo getProjectInfo() {
        ProjectInfo info = new ProjectInfo();
        info.setProjectKey(getProjectKey());

        String queryIdString = getQueryId();

        if (!queryIdString.isEmpty()) {
            info.setQueryId(Integer.parseInt(queryIdString));
        }
        return info;
    }

    private String getProjectKey() {
        return (String) projectKey.getValue();
    }

    private String getQueryId() {
        return (String) queryId.getValue();
    }

    public void setProjectInfo(ProjectInfo info) {
        EditorUtil.setNullSafe(projectKey, info.getProjectKey());
        EditorUtil.setNullSafe(queryId, info.getQueryId());
    }

    @Override
    public void validate() throws ValidationException {
        if (!Strings.isNullOrEmpty(getQueryId())) {
            try {
                Integer.parseInt(getQueryId());
            } catch (NumberFormatException e) {
                throw new ValidationException("Query Id must be a number");
            }
        }

        if (getProjectKey().trim().isEmpty()) {
            throw new ValidationException("Project Key is required");
        }
    }
}

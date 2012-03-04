package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.ProjectInfo;
import com.taskadapter.connector.definition.ValidationException;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import java.util.Collection;

/**
 * @author Alexey Skorokhodov
 */
public class ProjectPanel extends GridLayout implements Validatable {
    private static final String GROUP_LABEL = "Project Info";
    private static final int COLUMNS_NUMBER = 4;

    private final ConfigEditor editor;
    private TextField projectKey;
    private TextField queryId;

    private final ProjectProcessor projectProcessor;

    // TODO use or delete projectKeyRequired field
    private boolean projectKeyRequired;

    /**
     * Config Editors should NOT create this object directly, use ConfigEditor.addProjectPanel() method instead.
     *
     * @see ConfigEditor#addProjectPanel(ConfigEditor, ProjectProcessor)
     */
    ProjectPanel(ConfigEditor editor, ProjectProcessor projectProcessor) {
        this.editor = editor;
        this.projectProcessor = projectProcessor;
        init();
    }

    private void init() {
        addStyleName("bordered_panel");
        setCaption(GROUP_LABEL);
        setColumns(COLUMNS_NUMBER);
        setMargin(true);
        setSpacing(true);

        addComponent(new Label("Project key:"));
        this.projectKey = new TextField();
        addComponent(projectKey);

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
        addComponent(infoButton);

        LookupOperation loadProjectsOperation = new LoadProjectsOperation(editor, projectProcessor.getDescriptor().getPluginFactory());
        Button projectKeyButton = EditorUtil.createLookupButton(editor, "...", "Show list of available projects on the server.",
                loadProjectsOperation, projectKey, false);
        projectKeyButton.setEnabled(features.contains(ProjectProcessor.EditorFeature.LOAD_PROJECTS));
        addComponent(projectKeyButton);

        addComponent(new Label("Query ID:"));
        this.queryId = new TextField();
        queryId.setDescription("Custom query/filter ID (number). You need to create a query on the server before accessing it from here.\n"
                + "Read help for more details.");
        addComponent(queryId);
        LookupOperation loadSavedQueriesOperation = projectProcessor.getLoadSavedQueriesOperation(editor);

        Button showQueriesButton = EditorUtil.createLookupButton(editor, "...", "Show available saved queries on the server.",
                loadSavedQueriesOperation, queryId, false);
        showQueriesButton.setEnabled(features.contains(ProjectProcessor.EditorFeature.LOAD_SAVED_QUERIES));
        addComponent(showQueriesButton);
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
        if (isQueryIdSet()) {
            try {
                Integer.parseInt(getQueryId());
            } catch (NumberFormatException e) {
                throw new ValidationException("'Query Id' must be a number if provided");
            }
        }

        if (projectKeyRequired && getProjectKey().trim().isEmpty()) {
            throw new ValidationException("Project Key is required");
        }
    }

    private boolean isQueryIdSet() {
        return getQueryId() != null && !getQueryId().trim().isEmpty();
    }
}

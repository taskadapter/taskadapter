package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.ProjectInfo;
import com.taskadapter.connector.definition.ValidationException;
import com.vaadin.ui.*;

import java.util.Collection;
import java.util.Map;

/**
 * @author Alexey Skorokhodov
 */
public class ProjectPanel extends Panel implements LoadProjectsJobResultListener, Validatable {
    private static final String GROUP_LABEL = "Project Info";

    private final ConfigEditor editor;
    private TextField projectKey;
    private TextField queryID;

    private final ProjectProcessor projectProcessor;

    private boolean projectKeyRequired;

    /**
     * Config Editors should NOT create this object directly, use ConfigEditor.addProjectPanel() method instead.
     * @see ConfigEditor#addProjectPanel(ConfigEditor, ProjectProcessor)
     */
    ProjectPanel(ConfigEditor editor, ProjectProcessor projectProcessor) {
        this.editor = editor;
        this.projectProcessor = projectProcessor;
        init();
    }

    private void init() {
        setCaption(GROUP_LABEL);
        setSizeUndefined();
        this.projectKey = new TextField("Project key:");

        GridLayout layout = new GridLayout(3,2);
        layout.setSizeUndefined();
        layout.addComponent(projectKey);

        Collection<ProjectProcessor.EditorFeature> features = projectProcessor.getSupportedFeatures();

        Button button1 = EditorUtil.createButton("Info", "View the project info",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        loadProject();
                    }
                });
        button1.setEnabled(features.contains(ProjectProcessor.EditorFeature.LOAD_PROJECT_INFO));
        layout.addComponent(button1);

        LookupOperation loadProjectsOperation = new LoadProjectsOperation(
                editor, projectProcessor.getDescriptor());
        Button button2 = EditorUtil.createLookupButton(editor.getWindow(), "...",
                "Show list of available projects on the server.",
                loadProjectsOperation, projectKey, false);
        button2.setEnabled(features.contains(ProjectProcessor.EditorFeature.LOAD_PROJECTS));
        layout.addComponent(button2);

        this.queryID = EditorUtil.createLabeledTextWidth(
                "Query ID:",
                "Custom query/filter ID (number). You need to create a query on the server before accessing it from here."
                        + "\nRead help for more details.",
                EditorUtil.NARROW_WIDTH);
        layout.addComponent(queryID);
        LookupOperation loadSavedQueriesOperation = projectProcessor
                .getLoadSavedQueriesOperation(editor);

        Button showQueriesButton = EditorUtil.createLookupButton(editor.getWindow(),
                "...", "Show available saved queries on the server.",
                loadSavedQueriesOperation, queryID, false);
        showQueriesButton.setEnabled(features
                .contains(ProjectProcessor.EditorFeature.LOAD_SAVED_QUERIES));
        layout.addComponent(showQueriesButton);
        setContent(layout);
    }

    private void loadProject() {
        try {
            if (getProjectKey().isEmpty()) {
                // TODO need to use the field value and
                // disable the button
                // instead of this simple check
                throw new ValidationException(
                        "Please, provide the project key first");
            }

            projectProcessor.loadProject(getProjectKey());
        } catch (ValidationException e) {
            editor.getWindow().showNotification("Please, update the settings", e.getMessage());
        }
    }

    public ProjectInfo getProjectInfo() {
        ProjectInfo info = new ProjectInfo();
        String projectKeyValue = getProjectKey();
        info.setProjectKey(projectKeyValue);
        String queryIDString = getQueryID();
        if (!queryIDString.isEmpty()) {
            info.setQueryId(Integer.parseInt(queryIDString));
        }
        return info;
    }

    private String getProjectKey() {
        return (String) projectKey.getValue();
    }

    private String getQueryID() {
        return (String) queryID.getValue();
    }

    public void setProjectInfo(ProjectInfo info) {
        EditorUtil.setNullSafe(projectKey, info.getProjectKey());
        EditorUtil.setNullSafe(queryID, info.getQueryId());
    }

    @Override
    public void validate() throws ValidationException {
        if (!isQueryIdEmpty()) {
            try {
                Integer.parseInt(getQueryID());
            } catch (NumberFormatException e) {
                throw new ValidationException("'Query Id' must be a number if provided");
            }
        }

        if (projectKeyRequired && getProjectKey().trim().isEmpty()) {
            throw new ValidationException("Project Key is required");
        }
    }

    private boolean isQueryIdEmpty() {
        return (getQueryID() == null) || (getQueryID().trim().isEmpty());
    }

    @Override
    public void notifyProjectsLoaded(Map<String, String> namesToKeysMap) {
        String value = EditorUtil.showList(namesToKeysMap.keySet().toArray());
        if (value != null) {
            String key = namesToKeysMap.get(value);
            projectKey.setValue(key);
        }
    }

    public void setProjectKeyRequired(boolean required) {
        this.projectKeyRequired = required;
    }

}

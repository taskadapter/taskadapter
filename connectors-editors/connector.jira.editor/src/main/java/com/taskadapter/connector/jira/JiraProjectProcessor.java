package com.taskadapter.connector.jira;

/**
 * @author Alexey Skorokhodov
 */

import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebConfig;
import com.taskadapter.model.GProject;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.LookupOperation;
import com.taskadapter.web.configeditor.ProjectProcessor;

import java.util.Arrays;
import java.util.Collection;

public class JiraProjectProcessor implements ProjectProcessor {

    private final JiraEditor editor;

    public JiraProjectProcessor(JiraEditor editor) {
        this.editor = editor;
    }

    @Override
    public void loadProject(String projectKey) {
        try {
//			editor.validateServerInfo();
            WebConfig webConfig = (WebConfig) editor.getConfig();
            if (!webConfig.getServerInfo().isHostSet()) {
                throw new ValidationException("Host URL is not set");
            }

            JiraConfig config = (JiraConfig) webConfig;
            GProject project = new JiraProjectLoader().getProject(
                    config.getServerInfo(), projectKey);
            showProjectInfo(project);

//		} catch (ValidationException e) {
//			MessageDialog.openInformation(editor.getComposite().getShell(),
//					"Please, update the settings", e.getMessage());
//			e.getSource2().setFocus();
        } catch (Exception e) {
            EditorUtil.show(editor.getWindow(), "Can't load project", e);
        }
    }

    @Override
    public Descriptor getDescriptor() {
        return JiraDescriptor.instance;
    }

    @Override
    public LookupOperation getLoadSavedQueriesOperation(ConfigEditor editor) {
        return new LoadFiltersOperation(editor, JiraDescriptor.instance);
    }

    @Override
    public Collection<EditorFeature> getSupportedFeatures() {
        return Arrays.asList(EditorFeature.LOAD_PROJECTS,
                EditorFeature.LOAD_PROJECT_INFO,
                EditorFeature.LOAD_SAVED_QUERIES);
    }

    private void showProjectInfo(GProject project) {
        String msg = "Id: " + project.getId() + "\nKey:  "
                + project.getKey() + "\nName: "
                + project.getName();
        // + "\nLead: " + project.getLead()
        // + "\nURL: " + project.getProjectUrl()
        msg += addNullSafe("Homepage", project.getHomepage());
        msg += addNullSafe("Description", project.getDescription());

        EditorUtil.show(editor.getWindow(), "Project Info", msg);
    }

    private String addNullSafe(String label, String fieldValue) {
        String msg = "\n" + label + ": ";
        if (fieldValue != null) {
            msg += fieldValue;
        }
        return msg;
    }

}

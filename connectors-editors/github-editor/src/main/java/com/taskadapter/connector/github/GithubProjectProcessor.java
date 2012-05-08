package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.configeditor.LookupOperation;
import com.taskadapter.web.configeditor.ProjectProcessor;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Alexey Skorokhodov
 */

public class GithubProjectProcessor implements ProjectProcessor {

    public GithubProjectProcessor(GithubEditor editor) {
    }

    @Override
    public void loadProject(String projectKey) {
        // TODO Auto-generated method stub

    }

    @Override
    public Descriptor getDescriptor() {
        return GithubDescriptor.instance;
    }

    @Override
    public LookupOperation getLoadSavedQueriesOperation(ConfigEditor editor) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<EditorFeature> getSupportedFeatures() {
        return Arrays.asList(EditorFeature.LOAD_PROJECTS);
    }

}

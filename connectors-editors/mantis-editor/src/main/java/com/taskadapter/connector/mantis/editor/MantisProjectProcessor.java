package com.taskadapter.connector.mantis.editor;

import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.mantis.MantisDescriptor;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.configeditor.LookupOperation;
import com.taskadapter.web.configeditor.ProjectProcessor;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Alexey Skorokhodov
 */

public class MantisProjectProcessor implements ProjectProcessor {

    public MantisProjectProcessor(MantisEditor editor) {
    }

    @Override
    public void loadProject(String projectKey) {
        // TODO Auto-generated method stub

    }

    @Override
    public Descriptor getDescriptor() {
        return MantisDescriptor.instance;
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

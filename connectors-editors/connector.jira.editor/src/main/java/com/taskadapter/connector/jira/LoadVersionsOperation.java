package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.configeditor.LookupOperation;

import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public class LoadVersionsOperation extends LookupOperation {

    public LoadVersionsOperation(ConfigEditor editor, Descriptor descriptor) {
        super(editor, descriptor);
    }

    @Override
    public List<? extends NamedKeyedObject> loadData()
            throws Exception {
        JiraConnector jira = (JiraConnector) connector;
        return jira.getVersions();
    }
}

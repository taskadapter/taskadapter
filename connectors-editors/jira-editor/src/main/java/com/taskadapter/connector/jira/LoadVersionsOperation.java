package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.configeditor.LookupOperation;

import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public class LoadVersionsOperation extends LookupOperation {

    public LoadVersionsOperation(ConfigEditor editor, PluginFactory factory) {
        super(editor, factory);
    }

    @Override
    public List<? extends NamedKeyedObject> loadData()
            throws Exception {
        JiraConnector jira = (JiraConnector) connector;
        return jira.getVersions();
    }
}

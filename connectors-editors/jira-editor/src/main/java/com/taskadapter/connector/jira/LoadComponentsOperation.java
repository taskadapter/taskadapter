package com.taskadapter.connector.jira;

/**
 * @author Alexey Skorokhodov
 */

import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.configeditor.LookupOperation;

import java.util.List;

public class LoadComponentsOperation extends LookupOperation {

    public LoadComponentsOperation(ConfigEditor editor, PluginFactory factory) {
        super(editor, factory);
    }

    @Override
    public List<? extends NamedKeyedObject> loadData() throws Exception {
        JiraConnector jira = (JiraConnector) connector;
        return jira.getComponents();
    }
}
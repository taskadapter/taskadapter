package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.jira.exceptions.BadHostException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.service.Services;

/**
 * @author Alexey Skorokhodov
 */
public class JiraEditorFactory implements PluginEditorFactory {

    @Override
    public Descriptor getDescriptor() {
        return JiraDescriptor.instance;
    }

    @Override
    public ConfigEditor createEditor(ConnectorConfig config, Services services) {
        return new JiraEditor(config, services);
    }

    @Override
    public String formatError(Throwable e) {
        if (e instanceof BadHostException) {
            return "Illegal jira host name : " + e.getCause();
        } else if (e instanceof UnsupportedOperationException) {
            final UnsupportedOperationException uop = (UnsupportedOperationException) e;
            if ("updateRemoteIDs".equals(uop.getMessage()))
                return "Remoted IDs are not supported by Jira";
            else if ("saveRelations".equals(uop.getMessage()))
                return "Issue relations are not supported by Jira";
        }
        return null;
    }

}

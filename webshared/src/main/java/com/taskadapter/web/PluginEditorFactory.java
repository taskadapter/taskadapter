package com.taskadapter.web;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.service.Services;

public interface PluginEditorFactory {
    String getId();

    ConfigEditor createEditor(ConnectorConfig config, Services services);
    
    /**
     * Requests to format a plugin error. If error is not supported (not a 
     * custom error), this method may safelly return <code>null</code>.
     * @param e error to format.
     * @return formatted error.
     */
    String formatError(Throwable e);

    AvailableFields getAvailableFields();
}

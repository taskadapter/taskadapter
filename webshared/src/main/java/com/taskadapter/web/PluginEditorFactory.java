package com.taskadapter.web;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.service.Services;
import com.vaadin.ui.ComponentContainer;

public interface PluginEditorFactory {
    String getId();

    /**
     * Requests to format a plugin error. If error is not supported (not a 
     * custom error), this method may safelly return <code>null</code>.
     * @param e error to format.
     * @return formatted error.
     */
    String formatError(Throwable e);

    AvailableFields getAvailableFields();

    ComponentContainer getMiniPanelContents(WindowProvider windowProvider, Services services, ConnectorConfig config);
}

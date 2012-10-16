package com.taskadapter.web;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.web.service.Services;
import com.vaadin.ui.ComponentContainer;

public interface PluginEditorFactory<C extends ConnectorConfig> {
    /**
     * Requests to format a plugin error. If error is not supported (not a 
     * custom error), this method may safelly return <code>null</code>.
     * @param e error to format.
     * @return formatted error.
     */
    String formatError(Throwable e);

    AvailableFields getAvailableFields();

    ComponentContainer getMiniPanelContents(WindowProvider windowProvider, Services services, C config);
    
    /**
     * Validates a connector config for save mode. If validation fails, plugin
     * editor factory should provide appropriate user-friendly message. 
     * @param config config to check.
     * @throws ValidationException if validation fails.
     */
    void validateForSave(C config) throws ValidationException;

    /**
     * Validates a connector config for load mode. If validation fails, plugin
     * editor factory should provide appropriate user-friendly message. 
     * @param config config to check.
     * @throws ValidationException if validation fails.
     */
    void validateForLoad(C config) throws ValidationException;
    
    /**
     * Describes source location in a user-friendly manner.
     * @param config config.
     * @return user-friendly description of a source location.
     */
    String describeSourceLocation(C config);

    /**
     * Describes destination location in a user-friendly manner.
     * @param config config.
     * @return user-friendly description of a detination location.
     */
    String describeDestinationLocation(C config);
}

package com.taskadapter.web;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.web.service.Sandbox;
import com.vaadin.ui.ComponentContainer;

public interface PluginEditorFactory<C extends ConnectorConfig> extends ExceptionFormatter {
    ComponentContainer getMiniPanelContents(Sandbox sandbox, C config);
    
    /**
     * Validates a connector config for save mode. If validation fails, plugin
     * editor factory should provide appropriate user-friendly message. 
     * @param config config to check.
     * @throws BadConfigException if validation fails.
     */
    void validateForSave(C config) throws BadConfigException;

    /**
     * Validates a connector config for load mode. If validation fails, plugin
     * editor factory should provide appropriate user-friendly message. 
     * @param config config to check.
     * @throws BadConfigException if validation fails.
     */
    void validateForLoad(C config) throws BadConfigException;
    
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

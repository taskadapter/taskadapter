package com.taskadapter.web;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.web.service.Sandbox;
import com.vaadin.ui.ComponentContainer;

public interface PluginEditorFactory<C extends ConnectorConfig> extends
        ExceptionFormatter {
    ComponentContainer getMiniPanelContents(Sandbox sandbox, C config);

    /**
     * Validates a connector config for save mode. If validation fails, plugin
     * editor factory should provide appropriate user-friendly message.
     * 
     * @param config
     *            config to check.
     * @throws BadConfigException
     *             if validation fails.
     */
    void validateForSave(C config) throws BadConfigException;

    /**
     * Validates a connector config for load mode. If validation fails, plugin
     * editor factory should provide appropriate user-friendly message.
     * 
     * @param config
     *            config to check.
     * @throws BadConfigException
     *             if validation fails.
     */
    void validateForLoad(C config) throws BadConfigException;

    /**
     * Updates config for save (if it is possible). Primary purpose of this
     * method is to give a plugin editor last chance to update configuration
     * before save (for example, create a file). However, non-file plugins also
     * may update config if it is feasible.
     * 
     * @param config
     *            config to update.
     * @param sandbox
     *            local filesystem sandbox.
     * @return true iff config was updated.
     * @throws BadConfigException
     *             if config cannot be automatically updated to a
     *             "valid for save" state.
     */
    public boolean updateForSave(C config, Sandbox sandbox)
            throws BadConfigException;

    /**
     * Describes source location in a user-friendly manner.
     * 
     * @param config
     *            config.
     * @return user-friendly description of a source location.
     */
    String describeSourceLocation(C config);

    /**
     * Describes destination location in a user-friendly manner.
     * 
     * @param config
     *            config.
     * @return user-friendly description of a detination location.
     */
    String describeDestinationLocation(C config);
}

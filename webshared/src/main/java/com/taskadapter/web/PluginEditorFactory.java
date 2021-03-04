package com.taskadapter.web;

import com.taskadapter.common.ui.FieldMapping;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ConnectorSetup;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.web.uiapi.SavableComponent;

import java.util.List;

public interface PluginEditorFactory<C extends ConnectorConfig, S extends ConnectorSetup> extends ExceptionFormatter {
    SavableComponent getMiniPanelContents(Sandbox sandbox, C config, S setup);

    boolean isWebConnector();

    ConnectorSetupPanel getEditSetupPanel(Sandbox sandbox, S setup);

    /**
     * @param sandbox this may used by file-based connectors, e.g. to generate a new file name inside user data folder
     */
    S createDefaultSetup(Sandbox sandbox);

    /**
     * Validates a connector config for save mode. If validation fails, plugin
     * editor factory should provide appropriate user-friendly message.
     *
     * @param config config to check.
     */
    List<BadConfigException> validateForSave(C config, S setup, List<FieldMapping<?>> fieldMappings);

    /**
     * Validates a connector config for load mode. If validation fails, plugin
     * editor factory should provide appropriate user-friendly message.
     *
     * @param config config to check.
     */
    List<BadConfigException> validateForLoad(C config, S setup);

    /**
     * Validates config for "drop-in" loading.
     *
     * @param config config to validate.
     * @throws BadConfigException            if config cannot accept a drop for some reason.
     * @throws DroppingNotSupportedException if dropping is not supported either by this plugin or by
     *                                       "config type" (i.e. it's not a "configuration
     *                                       mistake", it is a definitely an "unsupported configuration").
     */

    void validateForDropInLoad(C config) throws BadConfigException, DroppingNotSupportedException;

    /**
     * Describes source location in a user-friendly manner.
     *
     * @return user-friendly description of a source location.
     */
    String describeSourceLocation(C config, S setup);

    /**
     * Describes destination location in a user-friendly manner.
     *
     * @return user-friendly description of destination location.
     */
    String describeDestinationLocation(C config, S setup);

    /**
     * Describes what labels to show for fields. e.g. "Status" field can be shown as "List Name" for Trello.
     */
    Messages fieldNames();
}

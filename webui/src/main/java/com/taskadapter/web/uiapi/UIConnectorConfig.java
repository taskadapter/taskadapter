package com.taskadapter.web.uiapi;

import com.taskadapter.common.ui.FieldMapping;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.definition.ConnectorSetup;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.model.Field;
import com.taskadapter.web.DroppingNotSupportedException;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Sandbox;

import java.util.List;

/**
 * Rich connector configuration item. Provides useful utility methods to create
 * UI elements, deserialize configs, etc... Simplifies PluginEditorFactory access
 * by tightly binding it to config (no more "resolve" operations). Also hides
 * "connector configuration type" inside implementation and removes need to
 * specify source/target types via all pages/controllers/etc... (Yes, it is a
 * java-specific way to implement "existential types").
 */
public interface UIConnectorConfig {

    /**
     * Returns a "connector type" id.
     *
     * @return connector type id.
     */
    String getConnectorTypeId();

    /**
     * Returns a connector configuration in a "string" format. This
     * configuration along with {@link #getConnectorTypeId()} may be passed into
     * {@link UIConfigService#createRichConfig(String, String)} to create a
     * clone of this config.
     *
     * @return string representation of UI connector config.
     */
    String getConfigString();

    /**
     * Calculates and returns connector label. This label may change when
     * connector changes a configuration.
     *
     * @return connector user-friendly label.
     */
    String getLabel();

    ConnectorSetup getConnectorSetup();

    void setConnectorSetup(ConnectorSetup setup);

    /**
     * Validates config for load.
     *
     * @return list of config errors. empty is no errors found. never null
     */
    List<BadConfigException> validateForLoad();

    /**
     * Validates config for save. Does not update it in any way.
     */
    List<BadConfigException> validateForSave(List<FieldMapping<?>> fieldMappings);

    /**
     * Validates config for drop-in operation.
     */
    void validateForDropIn() throws BadConfigException, DroppingNotSupportedException;

    /**
     * Creates a new connector instance with a current config. Note, that
     * current config will be shared among all created connectors.
     *
     * @return new connector instance, which shares config with this UIConfig
     * and all other connectors created via this method.
     */
    NewConnector createConnectorInstance();

    /**
     * Creates a "mini" informational panel.
     *
     * @param sandbox user operations sandbox.
     * @return connector configuration panel.
     */
    SavableComponent createMiniPanel(Sandbox sandbox);

    java.util.List<Field<?>> getAllFields();

    java.util.List<Field<?>> getDefaultFieldsForNewConfig();

    /**
     * Returns a source location name. This name is just a string for a user.
     *
     * @return source location name.
     */
    String getSourceLocation();

    /**
     * Returns a destination location name. This name is just a string for a user.
     *
     * @return destination location name.
     */
    String getDestinationLocation();

    Messages fieldNames();

    /**
     * Decodes a connector exception into a user-friendly (possibly localized) message.
     *
     * @param e exception to decode.
     * @return user-friendly error description.
     */
    String decodeException(Throwable e);

}

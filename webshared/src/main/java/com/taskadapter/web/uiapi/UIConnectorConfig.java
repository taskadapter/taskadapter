package com.taskadapter.web.uiapi;

import com.taskadapter.config.ConnectorDataHolder;
import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.service.Services;
import com.vaadin.ui.ComponentContainer;

/**
 * Rich connector configuration item. Provides usefull utility methods to create
 * UI elements, deserialize configs, etc... Simplifies PluginEditorFactory acces
 * by tightly binding it to config (no more "resolve" operations). Also hides
 * "connector configuration type" inside implementation and removes need to
 * specify source/target types via all pages/controllers/etc... (Yes, it's a
 * java-specific way to implement "existential types").
 * 
 */
public abstract class UIConnectorConfig {
    /**
     * Returns a "connector type" id.
     * 
     * @return connector type id.
     */
    public abstract String getConnectorTypeId();

    /**
     * Returns a connector configuration in a "string" format. This
     * configuration along with {@link #getConnectorTypeId()} may be passed into
     * {@link UIConfigService#createRichConfig(String, String)} to create a
     * clone of this config.
     * 
     * @return string representation of UI connector config.
     */
    public abstract String getConfigString();

    /**
     * Calculates and returns connector label. This label may change when
     * connector changes a configuration.
     * 
     * @return connector user-friendly label.
     */
    public abstract String getLabel();

    @Deprecated
    abstract ConnectorDataHolder holderize();

    /**
     * Validates config for load.
     * 
     * @throws ValidationException
     *             if config is invalid.
     */
    public abstract void validateForLoad() throws ValidationException;

    /**
     * Validates current config for load.
     * 
     * @throws ValidationException
     *             if config is invalid.
     */
    public abstract void validateForSave() throws ValidationException;

    @Deprecated
    public abstract ConnectorConfig getRawConfig();

    /**
     * Creates a new connector instance with a current config. Note, that
     * current config will be shared among all created connectors.
     * 
     * @return new connector instance, which shares config with this UIConfig
     *         and all other connectors created via this method.
     */
    public abstract Connector<?> createConnectorInstance();

    /**
     * Creates a "mini" informational panel.
     * 
     * @param windowProvider
     *            window provider.
     * @param services
     *            services - should not be used.
     * @return informational panel.
     */
    public abstract ComponentContainer createMiniPanel(
            WindowProvider windowProvider, Services services);

    /**
     * Returns a list of connector available fields in current configuration.
     * 
     * @return list of connector avaialable fields (in current confiruation
     *         only).
     */
    public abstract AvailableFields getAvailableFields();

    /**
     * Returns a source location name. This name is just a string for a user.
     * 
     * @return source location name.
     */
    public abstract String getSourceLocation();

    /**
     * Returns a destination location name. This name is just a string for a
     * user.
     * 
     * @return destination location name.
     */
    public abstract String getDestinationLocation();
}

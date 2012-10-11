package com.taskadapter.web.uiapi;

import com.taskadapter.config.ConnectorDataHolder;

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
     * @return connector user-friendly label.
     */
    public abstract String getLabel();
    
    @Deprecated
    abstract ConnectorDataHolder holderize();

}

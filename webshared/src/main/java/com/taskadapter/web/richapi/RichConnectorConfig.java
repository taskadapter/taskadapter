package com.taskadapter.web.richapi;

/**
 * Rich connector configuration item. Provides usefull utility methods to create
 * UI elements, deserialize configs, etc... Simplifies PluginEditorFactory acces
 * by tightly binding it to config (no more "resolve" operations). Also hides
 * "connector configuration type" inside implementation and removes need to
 * specify source/target types via all pages/controllers/etc... (Yes, it's a
 * java-specific way to implement "existential types").
 * 
 */
public abstract class RichConnectorConfig {
    /**
     * Returns a "connector type" id.
     * 
     * @return connector type id.
     */
    public abstract String getConnectorTypeId();
}

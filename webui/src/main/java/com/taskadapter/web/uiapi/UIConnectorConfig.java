package com.taskadapter.web.uiapi;

import com.taskadapter.connector.Field;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.model.StandardField;
import com.taskadapter.web.DroppingNotSupportedException;
import com.taskadapter.web.service.Sandbox;
import com.vaadin.ui.ComponentContainer;

import java.util.List;

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
    
    /**
     * Workadound for stupid Vaadin.
     * @deprecated
     */
    public final String getVaalabel() {
        return getLabel();
    }
    
    /**
     * Workadound for stupid Vaadin.
     * @deprecated
     */
    public final void setVaalabel(String label) {
        setLabel(label);
    }
    
    /**
     * Sets a new label.
     * @param label new label.
     */
    public abstract void setLabel(String label);

    /**
     * Validates config for load.
     * 
     * @throws BadConfigException
     *             if config is invalid.
     */
    public abstract void validateForLoad() throws BadConfigException;

    /**
     * Validates config for save. Does not update it in any way.
     */
    public abstract void validateForSave() throws BadConfigException;
    
    /**
     * Validates config for drop-in operation.
     */
    public abstract void validateForDropIn() throws BadConfigException,
            DroppingNotSupportedException;

    /**
     * Validates and updates config for save. This method is used mostly by the
     * file-based connectors. Such connector may create a new file for the
     * export. However, web-based connectors also may perform some action in
     * this method.
     * 
     * @param sandbox local filesystem sandbox.
     * @return true iff config was updated.
     * 
     * @throws BadConfigException
     *             if config is invalid and cannot be fixed automatically.
     */
    public abstract boolean updateForSave(Sandbox sandbox)
            throws BadConfigException;

    /**
     * Creates a new connector instance with a current config. Note, that
     * current config will be shared among all created connectors.
     * 
     * @return new connector instance, which shares config with this UIConfig
     *         and all other connectors created via this method.
     */
    public abstract NewConnector createConnectorInstance();

    /**
     * Creates a "mini" informational panel.
     * 
     * @param sandbox
     *            user operations sandbox.
     * @return connector configuration panel.
     */
    public abstract ComponentContainer createMiniPanel(Sandbox sandbox);

    /**
     * @return list of connector available fields (in current configuration).
     */
    public abstract List<Field> getAvailableFields();

    public abstract scala.collection.immutable.Map<Field, StandardField> getSuggestedCombinations();

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

    /**
     * Decodes a connector exception into a user-friendly (possibly localized)
     * message.
     * 
     * @param e
     *            exception to decode.
     * @return user-friendly error description.
     */
    public abstract String decodeException(Throwable e);

}

package com.taskadapter.web.uiapi;

import com.taskadapter.config.TAFile;
import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.NewMappings;

/**
 * UI model for a mapping config. All fields with complex mutable values are
 * immutable. Simple fields or fields with immutable values (like a String
 * fields) are mutable in this config.
 * <p>
 * There may be several instances of {@link UISyncConfig} for a same
 * "hard-copy". Moreover, that instances may differs from each other. Users of
 * this class should be aware of this behavior.
 * 
 */
public final class UISyncConfig {

    /**
     * Config identity. Unique "config-storage" id to distinguish between
     * configs. May be <code>null</code> for a new (non-saved) config.
     */
    private String identity;

    /**
     * Config label
     */
    private final String label;

    /**
     * First connector config.
     */
    private final UIConnectorConfig connector1;

    /**
     * Second connector config.
     */
    private final UIConnectorConfig connector2;

    /**
     * Field mappings. Left side is connector1, right size is connector2.
     */
    private final NewMappings newMappings;
    
    /**
     * "Config is reversed" flag.
     */
    private boolean reversed;

    UISyncConfig(String identity, String label,
            UIConnectorConfig connector1, UIConnectorConfig connector2,
            NewMappings newMappings, boolean reversed) {
        this.identity = identity;
        this.label = label;
        this.connector1 = connector1;
        this.connector2 = connector2;
        this.newMappings = newMappings;
    }

    public String getLabel() {
        return label;
    }

    public UIConnectorConfig getConnector1() {
        return connector1;
    }

    public UIConnectorConfig getConnector2() {
        return connector2;
    }

    public NewMappings getNewMappings() {
        return newMappings;
    }

    void setIdentity(String identity) {
        this.identity = identity;
    }
    
    String getIdentity() {
        return identity;
    }

    @Deprecated
    public TAFile tafileize() {
        final TAFile result = new TAFile(label, connector1.holderize(), connector2.holderize());
        result.setMappings(newMappings);
        result.setAbsoluteFilePath(identity);
        return result;
    }
    
    /**
     * Creates a "reversed" version of a config. Reversed version shares 
     * connector configurations and config identity with this config, 
     * but have new mapping instance.
     * 
     * @return "reversed" (back-order) configuration.
     */
    public UISyncConfig reverse() {
        return new UISyncConfig(identity, label, connector2, connector1,
                reverse(newMappings), !reversed);
    }
    
    /**
     * Returns a "normalized" (canonical) form of this config.
     * @return normalized (canonical) version of this config.
     */
    UISyncConfig normalized() {
        return reversed ? reverse() : this;
    }
    
    private static NewMappings reverse(NewMappings mappings) {
        final NewMappings result = new NewMappings();
        for (FieldMapping mapping : mappings.getMappings()) {
            result.put(reverse(mapping));
        }
        return result;
    }

    private static FieldMapping reverse(FieldMapping mapping) {
        return new FieldMapping(mapping.getField(), mapping.getConnector2(),
                mapping.getConnector1(), mapping.isSelected());
    }

}

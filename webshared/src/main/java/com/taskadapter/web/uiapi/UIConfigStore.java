package com.taskadapter.web.uiapi;

import com.google.gson.Gson;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.config.StorageException;
import com.taskadapter.config.StoredConnectorConfig;
import com.taskadapter.config.StoredExportConfig;
import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.NewMappings;
import com.taskadapter.model.GTaskDescriptor.FIELD;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * UI-level config manager. Manages UIMappingConfigs instead of low-level
 * {@link StoredConnectorConfig}. All methods of this class creates new fresh
 * instances of UIMappingConfig. Modifications of that instances will not affect
 * other instances for a same config file. See also documentation for
 * {@link UISyncConfig}.
 * 
 */
public final class UIConfigStore {

    /**
     * Service to use with UI configs.
     */
    private final UIConfigService uiConfigService;

    /**
     * Low-level configuration storage.
     */
    private final ConfigStorage configStorage;

    public UIConfigStore(UIConfigService uiConfigService,
            ConfigStorage configStorage) {
        this.uiConfigService = uiConfigService;
        this.configStorage = configStorage;
    }

    /**
     * Lists all user-created configs.
     * 
     * @param userLoginName
     *            login name to load items for.
     * @return collection of user config in no particular order.
     */
    public List<UISyncConfig> getUserConfigs(String userLoginName) {
        final List<StoredExportConfig> storedConfigs = configStorage
                .getUserConfigs(userLoginName);
        final List<UISyncConfig> result = new ArrayList<UISyncConfig>(
                storedConfigs.size());
        for (StoredExportConfig storedConfig : storedConfigs) {
            result.add(uize(storedConfig));
        }
        return result;
    }

    /**
     * Creates a new (fresh) config.
     * 
     * @param userName
     *            user login name (for whom config will be created).
     * @param label
     *            config label (name).
     * @param connector1id
     *            first connector id.
     * @param connector2id
     *            second connector id.
     * @return newly created (and saved) UI mapping config.
     * @throws StorageException
     *             if config storage fails.
     */
    public UISyncConfig createNewConfig(String userName, String label,
            String connector1id, String connector2id) throws StorageException {
        final UIConnectorConfig config1 = uiConfigService
                .createDefaultConfig(connector1id);
        final UIConnectorConfig config2 = uiConfigService
                .createDefaultConfig(connector2id);
        final NewMappings mappings = new NewMappings();
        final String mappingsString = new Gson().toJson(mappings);
        final String identity = configStorage.createNewConfig(userName, label,
                config1.getConnectorTypeId(), config1.getConfigString(),
                config2.getConnectorTypeId(), config2.getConfigString(),
                mappingsString);
        return new UISyncConfig(identity, label, config1, config2, mappings,
                false);
    }

    /**
     * Saves a config.
     * 
     * @param syncConfig
     *            config to save.
     * @throws StorageException
     *             if config cannot be saved.
     */
    public void saveConfig(String userLoginName, UISyncConfig syncConfig)
            throws StorageException {
        syncConfig = syncConfig.normalized();
        final String label = syncConfig.getLabel();
        final UIConnectorConfig config1 = syncConfig.getConnector1();
        final UIConnectorConfig config2 = syncConfig.getConnector2();
        final NewMappings mappings = syncConfig.getNewMappings();
        final String mappingsStr = new Gson().toJson(mappings);
        configStorage.saveConfig(userLoginName, syncConfig.getIdentity(),
                label, config1.getConnectorTypeId(), config1.getConfigString(),
                config2.getConnectorTypeId(), config2.getConfigString(),
                mappingsStr);
    }

    /**
     * Create a new UI config instance for a stored config.
     * 
     * @param storedConfig
     *            stored config to create an instance for.
     * @return new parsed config.
     */
    private UISyncConfig uize(StoredExportConfig storedConfig) {
        final String label = storedConfig.getName();
        final StoredConnectorConfig conn1Config = storedConfig.getConnector1();
        final StoredConnectorConfig conn2Config = storedConfig.getConnector2();
        final UIConnectorConfig config1 = uiConfigService.createRichConfig(
                conn1Config.getConnectorTypeId(),
                conn1Config.getSerializedConfig());
        final UIConnectorConfig config2 = uiConfigService.createRichConfig(
                conn2Config.getConnectorTypeId(),
                conn2Config.getSerializedConfig());
        final NewMappings mappings = storedConfig.getMappings() == null ? new NewMappings()
                : new Gson().fromJson(storedConfig.getMappings(),
                        NewMappings.class);
        final NewMappings fixedMappings = fixMappings(mappings, config1, config2);
        return new UISyncConfig(storedConfig.getId(), label, config1, config2,
                fixedMappings, false);
    }

    /**
     * Fixes mappings. Remove "unsupported" mappings. Add new mappings (in 
     * disabled state).
     * @param mappings mappings to fix.
     * @param config1 first config.
     * @param config2 second config.
     */
    private NewMappings fixMappings(NewMappings mappings, UIConnectorConfig config1,
            UIConnectorConfig config2) {
        final AvailableFields fields1 = config1.getAvailableFields();
        final AvailableFields fields2 = config2.getAvailableFields();
        final Collection<FIELD> firstFields = fields1
                .getSupportedFields();
        final Collection<FIELD> secondFields = fields2
                .getSupportedFields();
        
        final NewMappings result = new NewMappings();

        if (secondFields.contains(FIELD.REMOTE_ID)) {
            final FieldMapping saved = findRemote(mappings, false, true);
            if (saved != null) {
                result.put(saved);
            } else {
                result.put(new FieldMapping(FIELD.REMOTE_ID, null,
                    getDefaultFieldValue(FIELD.REMOTE_ID, fields2), false));
            }
        }
        
        if (firstFields.contains(FIELD.REMOTE_ID)) {
            final FieldMapping saved = findRemote(mappings, true, false);
            if (saved != null) {
                result.put(saved);
            } else {
                result.put(new FieldMapping(FIELD.REMOTE_ID,
                        getDefaultFieldValue(FIELD.REMOTE_ID, fields1), null,
                        false));
            }
        }
        
        for (FIELD field : FIELD.values()) {
            if (field == FIELD.ID || field == FIELD.REMOTE_ID) {
                continue;
            }
            
            if (!firstFields.contains(field) || !secondFields.contains(field)) {
                continue;
            }
            
            final FieldMapping oldMapping = mappings.getMapping(field);
            if (oldMapping != null) {
                result.put(oldMapping);
                continue;
            }
            
            final FieldMapping newMapping = new FieldMapping(field,
                    getDefaultFieldValue(field, fields1), getDefaultFieldValue(
                            field, fields2), false);
            result.put(newMapping);
        }
        
        return result;        
    }
    
    private static FieldMapping findRemote(NewMappings mappings,
            boolean remoteLeft, boolean remoteRight) {
        for (FieldMapping mapping : mappings.getMappings()) {
            if (
                    mapping.getField() == FIELD.ID && (
                    ((mapping.getConnector1() == null) != remoteLeft) ||
                    ((mapping.getConnector2() == null) != remoteRight))) {
                return mapping;
            }
        }
        return null;
    }

    private String getDefaultFieldValue(FIELD field, AvailableFields fields1) {
        final String[] values = fields1.getAllowedValues(field);
        if (values == null || values.length < 1) {
            return null;
        }
        return values[0];
    }

    /**
     * Deletes a config.
     * @param config config to delete.
     */
    public void deleteConfig(UISyncConfig config) {
        configStorage.delete(config.getIdentity());
    }

    /**
     * Clones a config.
     * @param userLoginName user login name.
     * @param syncConfig config to clone.
     * @throws StorageException if an error occured.
     */
    public void cloneConfig(String userLoginName, UISyncConfig syncConfig) throws StorageException {
        final String label = syncConfig.getLabel();
        final UIConnectorConfig config1 = syncConfig.getConnector1();
        final UIConnectorConfig config2 = syncConfig.getConnector2();
        final NewMappings mappings = syncConfig.getNewMappings();
        final String mappingsStr = new Gson().toJson(mappings);
        configStorage.createNewConfig(userLoginName, label,
                config1.getConnectorTypeId(), config1.getConfigString(),
                config2.getConnectorTypeId(), config2.getConfigString(),
                mappingsStr);
    }

}

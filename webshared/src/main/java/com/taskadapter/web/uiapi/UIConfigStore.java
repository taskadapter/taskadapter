package com.taskadapter.web.uiapi;

import com.google.gson.Gson;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.config.StorageException;
import com.taskadapter.config.StoredConnectorConfig;
import com.taskadapter.config.StoredExportConfig;
import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.NewMappings;

import java.util.ArrayList;
import java.util.List;

/**
 * UI-level config manager. Manages UIMappingConfigs instead of low-level
 * {@link StoredConnectorConfig}. All methods of this class creates new fresh
 * instances of UIMappingConfig. Modifications of that instances will not affect
 * other instances for a same config file. See also documentation for
 * {@link UISyncConfig}.
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

    /**
     * Config builder.
     */
    private final UISyncConfigBuilder syncConfigBuilder;

    public UIConfigStore(UIConfigService uiConfigService,
                         ConfigStorage configStorage) {
        this.uiConfigService = uiConfigService;
        this.configStorage = configStorage;
        this.syncConfigBuilder = new UISyncConfigBuilder(uiConfigService);
    }

    /**
     * Lists all user-created configs.
     *
     * @param userLoginName login name to load items for.
     * @return collection of user config in no particular order.
     */
    public List<UISyncConfig> getUserConfigs(String userLoginName) {
        final List<StoredExportConfig> storedConfigs = configStorage.getUserConfigs(userLoginName);
        final List<UISyncConfig> result = new ArrayList<UISyncConfig>(storedConfigs.size());
        for (StoredExportConfig storedConfig : storedConfigs) {
            result.add(syncConfigBuilder.uize(storedConfig));
        }
        return result;
    }

    /**
     * Creates a new (fresh) config.
     *
     * @param userName     user login name (for whom config will be created).
     * @param label        config label (name).
     * @param connector1id first connector id.
     * @param connector2id second connector id.
     * @return newly created (and saved) UI mapping config.
     * @throws StorageException if config storage fails.
     */
    public UISyncConfig createNewConfig(String userName, String label,
                                        String connector1id, String connector2id) throws StorageException {
        final UIConnectorConfig config1 = uiConfigService.createDefaultConfig(connector1id);
        final UIConnectorConfig config2 = uiConfigService.createDefaultConfig(connector2id);

        final NewMappings newMappings = NewMappingBuilder.createNewMappings(
                config1.getAvailableFields(), config2.getAvailableFields());

        final String mappingsString = new Gson().toJson(newMappings);
        final String identity = configStorage.createNewConfig(userName, label,
                config1.getConnectorTypeId(), config1.getConfigString(),
                config2.getConnectorTypeId(), config2.getConfigString(),
                mappingsString);
        return new UISyncConfig(identity, label, config1, config2, newMappings, false);
    }

    /**
     * Saves a config.
     *
     * @param syncConfig config to save.
     * @throws StorageException if config cannot be saved.
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
     * Deletes a config.
     *
     * @param config config to delete.
     */
    public void deleteConfig(UISyncConfig config) {
        configStorage.delete(config.getIdentity());
    }

    /**
     * Clones a config.
     *
     * @param userLoginName user login name.
     * @param syncConfig    config to clone.
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

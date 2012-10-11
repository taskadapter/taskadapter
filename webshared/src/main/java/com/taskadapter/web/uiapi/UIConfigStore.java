package com.taskadapter.web.uiapi;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.config.StorageException;
import com.taskadapter.config.StoredConnectorConfig;
import com.taskadapter.config.StoredExportConfig;
import com.taskadapter.connector.definition.NewMappings;

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
                config2.getConnectorTypeId(), config2.getConnectorTypeId(),
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
        final NewMappings mappings = new Gson().fromJson(
                storedConfig.getMappings(), NewMappings.class);
        return new UISyncConfig(storedConfig.getId(), label, config1, config2,
                mappings, false);
    }

}

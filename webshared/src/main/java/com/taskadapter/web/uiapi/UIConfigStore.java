package com.taskadapter.web.uiapi;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.config.StoredConnectorConfig;
import com.taskadapter.config.StoredExportConfig;
import com.taskadapter.connector.definition.NewMappings;

/**
 * UI-level config manager. Manages UIMappingConfigs instead of low-level
 * {@link StoredConnectorConfig}. All methods of this class creates new fresh
 * instances of UIMappingConfig. Modifications of that instances will not affect
 * other instances for a same config file. See also documentation for
 * {@link UIMappingConfig}.
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

    public List<UIMappingConfig> getUserConfigs(String userLoginName) {
        final List<StoredExportConfig> storedConfigs = configStorage
                .getUserConfigs(userLoginName);
        final List<UIMappingConfig> result = new ArrayList<UIMappingConfig>(
                storedConfigs.size());
        for (StoredExportConfig storedConfig : storedConfigs) {
            result.add(uize(storedConfig));
        }
        return result;
    }

    /**
     * Create a new UI config instance for a stored config.
     * 
     * @param storedConfig
     *            stored config to create an instance for.
     * @return new parsed config.
     */
    private UIMappingConfig uize(StoredExportConfig storedConfig) {
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
        return new UIMappingConfig(storedConfig.getId(), label, config1,
                config2, mappings);
    }

}

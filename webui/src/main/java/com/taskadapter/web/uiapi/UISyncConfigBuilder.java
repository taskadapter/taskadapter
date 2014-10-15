package com.taskadapter.web.uiapi;

import com.google.gson.Gson;
import com.taskadapter.config.StoredConnectorConfig;
import com.taskadapter.config.StoredExportConfig;
import com.taskadapter.connector.definition.NewMappings;

//TODO: This class should really be package-visible.
// Need to move test to an appropriate package and use it!
public final class UISyncConfigBuilder {

    private final UIConfigService uiConfigService;

    public UISyncConfigBuilder(UIConfigService uiConfigService) {
        this.uiConfigService = uiConfigService;
    }

    /**
     * Create a new UI config instance for a stored config.
     *
     * @param ownerName name of config owner.
     * @param storedConfig stored config to create an instance for.
     * @return new parsed config.
     */
    public UISyncConfig uize(String ownerName, StoredExportConfig storedConfig) {
        final String label = storedConfig.getName();
        final StoredConnectorConfig conn1Config = storedConfig.getConnector1();
        final StoredConnectorConfig conn2Config = storedConfig.getConnector2();
        final UIConnectorConfig config1 = uiConfigService.createRichConfig(conn1Config.getConnectorTypeId(), conn1Config.getSerializedConfig());
        final UIConnectorConfig config2 = uiConfigService.createRichConfig(conn2Config.getConnectorTypeId(), conn2Config.getSerializedConfig());
        final NewMappings mappings = storedConfig.getMappings() == null ? MappingGuesser.guessNewMappings(storedConfig)
                : new Gson().fromJson(storedConfig.getMappings(),
                NewMappings.class);

        // fixing the mappings is important to load config files created by Task Adapter v. 2.2 or older.
        // feel free to delete MappingFixer.fixMappings() call if it's 2013.
//        final NewMappings fixedMappings = MappingFixer.fixMappings(mappings,
//                config1.getAvailableFields(), config2.getAvailableFields(),
//                false);
//        return new UISyncConfig(storedConfig.getId(), ownerName, label,
//                config1, config2, fixedMappings, false);
        return new UISyncConfig(storedConfig.getId(), ownerName, label,
                config1, config2, mappings, false);
    }

}

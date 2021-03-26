package com.taskadapter.web.uiapi;

import com.taskadapter.auth.cred.CredentialsStore;
import com.taskadapter.common.JsonUtil;
import com.taskadapter.common.ui.NewConfigSuggester;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.config.JsonFactory;
import com.taskadapter.config.StorageException;
import com.taskadapter.config.StoredExportConfig;
import com.taskadapter.connector.common.FileNameGenerator;
import com.taskadapter.connector.common.XorEncryptor;
import com.taskadapter.connector.definition.ConnectorSetup;
import com.taskadapter.connector.definition.FileSetup;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.web.TaskKeeperLocationStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UI-level config manager. Manages UIMappingConfigs instead of low-level
 * [[com.taskadapter.config.StoredConnectorConfig]]. All methods of this class creates new fresh
 * instances of UIMappingConfig. Modifications of that instances will not affect
 * other instances for a same config file. See also documentation for
 * [[UISyncConfig]].
 */
public class UIConfigStore {
    private static final Logger logger = LoggerFactory.getLogger(ConfigStorage.class);

    private  final UIConfigService uiConfigService;
    private  final ConfigStorage configStorage;
    private final CredentialsStore usersStorage;

    private static final XorEncryptor encryptor = new XorEncryptor();

    public UIConfigStore(UIConfigService uiConfigService, ConfigStorage configStorage, CredentialsStore usersStorage) {
        this.uiConfigService = uiConfigService;
        this.configStorage = configStorage;
        this.usersStorage = usersStorage;
    }

    public List<UISyncConfig> getConfigs() {
        return usersStorage.listUsers().stream()
                .map(this::getUserConfigs)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Lists all user-created configs.
     *
     * @param userLoginName login name to load items for.
     * @return collection of the user's config in no particular order.
     */
    public List<UISyncConfig> getUserConfigs(String userLoginName) {
        var storedConfigs = configStorage.getUserConfigs(userLoginName);
        return storedConfigs.stream().map(storedConfig -> {
            try {
                return Optional.of(uize(userLoginName, storedConfig));
            } catch (Exception e) {
                logger.error("Error parsing config " + storedConfig.getId()
                        + " for user " + userLoginName + ". Skipping this config. " + e.toString());
                return Optional.<UISyncConfig>empty();
            }
        }).filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public File getSavedSetupsFolder(String loginName){
        return configStorage.getUserFolder(loginName);
    }

    /**
     * Create a new UI config instance for a stored config.
     *
     * @param ownerName    name of config owner.
     * @param storedConfig stored config to create an instance for.
     * @return new parsed config.
     */
    public UISyncConfig uize(String ownerName, StoredExportConfig storedConfig) throws IOException {
        var label = storedConfig.getName();
        var conn1Config = storedConfig.getConnector1();
        var conn2Config = storedConfig.getConnector2();

        var config1 = uiConfigService.createRichConfig(conn1Config.getConnectorTypeId(),
                conn1Config.getSerializedConfig());
        var config2 = uiConfigService.createRichConfig(conn2Config.getConnectorTypeId(),
                conn2Config.getSerializedConfig());
        var jsonString = storedConfig.getMappingsString();

        var connector1Setup = getSetup(ownerName, conn1Config.getConnectorSavedSetupId());
        var connector2Setup = getSetup(ownerName, conn2Config.getConnectorSavedSetupId());

        config1.setConnectorSetup(connector1Setup);
        config2.setConnectorSetup(connector2Setup);

        try {
            var newMappings = JsonFactory.fromJsonString(jsonString);
            return new UISyncConfig(
                    new TaskKeeperLocationStorage(configStorage.getRootDir()),
                    new ConfigId(ownerName, storedConfig.getId()),
                    label, config1, config2, newMappings, false);
        } catch (Exception e) {
                throw new RuntimeException("cannot parse mappings from config " + storedConfig + ": $e");
        }
    }

    /**
     * Creates a new (fresh) config.
     *
     * @param userName     user login name (for whom config will be created).
     * @param label        config label (name).
     * @param connector1Id first connector id.
     * @param connector2Id second connector id.
     * @return newly created (and saved) UI mapping config.
     */
    public ConfigId createNewConfig(String userName, String label,
                                    String connector1Id, SetupId connector1SetupId,
                                    String connector2Id, SetupId connector2SetupId) throws StorageException {
        var config1 = uiConfigService.createDefaultConfig(connector1Id);
        var config2 = uiConfigService.createDefaultConfig(connector2Id);

        var newMappings = NewConfigSuggester.suggestedFieldMappingsForNewConfig(
                config1.getDefaultFieldsForNewConfig(),
                config2.getDefaultFieldsForNewConfig());
        var mappingsString = JsonFactory.toString(newMappings);
        var configId = configStorage.createNewConfig(userName, label,
                config1.getConnectorTypeId(), connector1SetupId, config1.getConfigString(),
                config2.getConnectorTypeId(), connector2SetupId, config2.getConfigString(),
                mappingsString);

        return configId;
    }

    public Optional<UISyncConfig> getConfig(ConfigId configId) {
        return getUserConfigs(configId.getOwnerName())
                .stream()
                .filter(c -> c.getConfigId().equals(configId))
                .findFirst();
    }

    public SetupId saveNewSetup(String userName, ConnectorSetup setup) throws StorageException {
        var newFile = FileNameGenerator.findSafeAvailableFileName(getSavedSetupsFolder(userName), setup.getConnectorId() + "_%d.json");
        var setupId = new SetupId(newFile.getName());
        saveSetup(userName, setup, setupId);
        return setupId;
    }

    public void saveSetup(String userName, ConnectorSetup setup, SetupId setupId) throws StorageException {
        var jsonString = setup instanceof WebConnectorSetup ?
                getWebSetupJson((WebConnectorSetup) setup, setupId)
                : getFileSetupJson((FileSetup) setup, setupId);
        configStorage.saveConnectorSetup(userName, setupId, jsonString);
    }

    private static String getWebSetupJson(WebConnectorSetup setup, SetupId setupId) {
        setup.setId(setupId.getId());
        setup.setPassword(encryptor.encrypt(setup.getPassword()));
        setup.setApiKey(encryptor.encrypt(setup.getApiKey()));
        return JsonUtil.toJsonString(setup);
    }

    private static String getFileSetupJson(FileSetup setup, SetupId setupId) {
        setup.setId(setupId.getId());
        return JsonUtil.toJsonString(setup);
    }

    public ConnectorSetup getSetup(String userName, SetupId setupId) throws IOException {
        var string = configStorage.loadConnectorSetupAsString(userName, setupId);
        return parseSetupStringToDecryptedSetup(string);
    }

    public List<ConnectorSetup> getAllConnectorSetups(String userLoginName) {
        return configStorage.getAllConnectorSetupsAsStrings(userLoginName)
                .stream()
                .map(this::parseSetupStringToDecryptedSetup)
                .collect(Collectors.toList());
    }

    public List<ConnectorSetup> getAllConnectorSetups(String userLoginName, String connectorId){
        var allForUser = getAllConnectorSetups(userLoginName);
        return allForUser.stream().filter(setup ->
                setup.getConnectorId().equals(connectorId))
                .collect(Collectors.toList());
    }

    private ConnectorSetup parseSetupStringToDecryptedSetup(String string) {
        var hostElement = JsonUtil.toJsonElement(string).getAsJsonObject().get("host");
        // web config
        if (hostElement != null) {
            var encryptedSetup = JsonUtil.parseJsonString(string, WebConnectorSetup.class);
            var decryptedSetup = encryptedSetup;
            encryptedSetup.setPassword(encryptor.decrypt(encryptedSetup.getPassword()));
            encryptedSetup.setApiKey(encryptor.decrypt(encryptedSetup.getApiKey()));
            return decryptedSetup;
        } else {
            return JsonUtil.parseJsonString(string, FileSetup.class);
        }
    }

    /**
     * Saves a config.
     *
     * @param syncConfig config to save.
     * @throws StorageException if config cannot be saved.
     */
    public void saveConfig(UISyncConfig syncConfig) throws  StorageException{
        var normalizedSyncConfig = syncConfig.normalized();
        var label = normalizedSyncConfig.getLabel();
        var config1 = normalizedSyncConfig.getConnector1();
        var config2 = normalizedSyncConfig.getConnector2();
        var mappings = normalizedSyncConfig.getNewMappings();
        var mappingsStr = JsonFactory.toString(mappings);
        configStorage.saveConfig(normalizedSyncConfig.getOwnerName(), normalizedSyncConfig.getConfigId().getId(),
                label,
                config1.getConnectorTypeId(),
                new SetupId(config1.getConnectorSetup().getId()),
                    config1.getConfigString(),
                config2.getConnectorTypeId(),
                new SetupId(config2.getConnectorSetup().getId()),
                config2.getConfigString(),
                mappingsStr);
    }

    public void deleteConfig(ConfigId configId){
        configStorage.deleteConfig(configId);
    }

    public void deleteSetup(String userName, SetupId id){
        configStorage.deleteSetup(userName, id);
    }

    /**
     * @param userLoginName name of the new config owner.
     * @param configId      unique identifier for config to clone
     */
    public void cloneConfig(String userLoginName, ConfigId configId) throws StorageException{
        var savedConfig = configStorage.getConfig(configId);
        if (savedConfig.isPresent()) {
            var config = savedConfig.get();
            var connector1 = config.getConnector1();
            var connector2 = config.getConnector2();
            configStorage.createNewConfig(userLoginName, config.getName(),
                    connector1.getConnectorTypeId(), connector1.getConnectorSavedSetupId(), connector1.getSerializedConfig(),
                    connector2.getConnectorTypeId(), connector2.getConnectorSavedSetupId(), connector2.getSerializedConfig(),
                    config.getMappingsString());
        } else {
            throw new StorageException("Cannot find config with id " + configId + " to clone");
        }
    }

    public List<ConfigId> getConfigIdsUsingThisSetup(String userName, SetupId id) {
        return configStorage.getUserConfigs(userName)
                .stream()
                .filter(c -> c.getConnector1().getConnectorSavedSetupId().equals(id)
                        || c.getConnector2().getConnectorSavedSetupId().equals(id))
                .map(c -> new ConfigId(userName, c.getId())).collect(Collectors.toList());
    }
}


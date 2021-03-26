package com.taskadapter.webui;

import com.taskadapter.auth.AuthorizedOperations;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.config.StorageException;
import com.taskadapter.connector.definition.ConnectorSetup;
import com.taskadapter.web.event.EventTracker;
import com.taskadapter.web.uiapi.ConfigId;
import com.taskadapter.web.uiapi.SetupId;
import com.taskadapter.web.uiapi.UIConfigStore;
import com.taskadapter.web.uiapi.UISyncConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.taskadapter.web.event.EventCategory.ConfigCategory;

public class ConfigOperations {
    /**
     * Name of the owner.
     */
    private final String userName;

    /**
     * User permissions.
     */
    private final AuthorizedOperations authorizedOps;

    /**
     * Credentials manager.
     */
    private final CredentialsManager credManager;

    /**
     * Config accessor.
     */
    private final UIConfigStore uiConfigStore;

    /**
     * Synchronization sandbox.
     */
    private final File syncSandbox;

    public ConfigOperations(String userName, AuthorizedOperations authorizedOps,
                            CredentialsManager credManager, UIConfigStore uiConfigStore, File syncSandbox) {
        this.userName = userName;
        this.authorizedOps = authorizedOps;
        this.credManager = credManager;
        this.uiConfigStore = uiConfigStore;
        this.syncSandbox = syncSandbox;
    }

    public String getUserName() {
        return userName;
    }

    public AuthorizedOperations getAuthorizedOps() {
        return authorizedOps;
    }

    public CredentialsManager getCredManager() {
        return credManager;
    }

    public UIConfigStore getUiConfigStore() {
        return uiConfigStore;
    }

    public File getSyncSandbox() {
        return syncSandbox;
    }

    /**
     * @return list of configs that user owns.
     */
    public List<UISyncConfig> getOwnedConfigs() {
        return uiConfigStore.getUserConfigs(userName);
    }

    private File getSavedSetupsFolder() {
        return uiConfigStore.getSavedSetupsFolder(userName);
    }

    public Optional<UISyncConfig> getConfig(ConfigId configId) {
        return uiConfigStore.getConfig(configId);
    }

    /**
     * @return list of configs that user can manage.
     */
    public List<UISyncConfig> getManageableConfigs() {
        if (!authorizedOps.canManagerPeerConfigs()) {
            return getOwnedConfigs();
        }
        var res = new ArrayList<UISyncConfig>();
        credManager.listUsers().forEach(user ->
                res.addAll(uiConfigStore.getUserConfigs(user))
        );
        return res;
    }

    /**
     * Creates a new config and returns it.
     *
     * @param descriptionString config description.
     * @param connector1Id      first connector id.
     * @param connector2Id      second connector id.
     * @return new config
     * @throws StorageException if config cannot be created.
     */
    public ConfigId createNewConfig(String descriptionString, String connector1Id, SetupId connector1SetupId,
                                    String connector2Id, SetupId connector2SetupId) throws StorageException {
        var configId = uiConfigStore.createNewConfig(userName, descriptionString, connector1Id, connector1SetupId,
                connector2Id, connector2SetupId);
        notifyNewConfig(configId);
        return configId;
    }

    private void notifyNewConfig(ConfigId configId) {
        var maybeConfig = getConfig(configId);
        if (maybeConfig.isEmpty()) {
            throw new RuntimeException("The newly created config with id " + configId + " cannot be found. This is weird.");
        }
        var config = maybeConfig.get();
        EventTracker.trackEvent(ConfigCategory, "created",
                config.getConnector1().getConnectorTypeId() + " - " + config.getConnector2().getConnectorTypeId());
    }

    /**
     * Delete a config.
     *
     * @param configIdentity a unique id for the config in the store
     */
    public void deleteConfig(ConfigId configIdentity) {
        uiConfigStore.deleteConfig(configIdentity);
        // tracker.trackEvent(ConfigCategory, "deleted", "")
    }

    /**
     * Clones config. Current user became the owner of the clone.
     *
     * @param configId a unique id the config in the store
     * @throws StorageException if config cannot be cloned.
     */
    public void cloneConfig(ConfigId configId) throws StorageException {
        uiConfigStore.cloneConfig(userName, configId);
    }

    public void saveConfig(UISyncConfig config) throws StorageException {
        uiConfigStore.saveConfig(config);
        // tracker.trackEvent(ConfigCategory, "saved", "")
    }

    public SetupId saveNewSetup(ConnectorSetup setup) throws StorageException {
        return uiConfigStore.saveNewSetup(userName, setup);
    }

    public void saveSetup(ConnectorSetup setup, SetupId id) throws StorageException {
        uiConfigStore.saveSetup(userName, setup, id);
    }

    public List<ConnectorSetup> getAllConnectorSetups(String connectorId) {
        return uiConfigStore.getAllConnectorSetups(userName, connectorId);
    }

    public List<ConnectorSetup> getConnectorSetups() {
        return uiConfigStore.getAllConnectorSetups(userName);
    }

    public ConnectorSetup getSetup(SetupId setupId) throws IOException {
        return uiConfigStore.getSetup(userName, setupId);
    }

    public void deleteConnectorSetup(SetupId id) {
        uiConfigStore.deleteSetup(userName, id);
    }

    public List<ConfigId> getConfigIdsUsingThisSetup(SetupId id) {
        return uiConfigStore.getConfigIdsUsingThisSetup(userName, id);
    }
}

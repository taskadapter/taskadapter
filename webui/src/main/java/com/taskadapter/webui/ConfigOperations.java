package com.taskadapter.webui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.taskadapter.auth.AuthorizedOperations;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.config.ConnectorSetup;
import com.taskadapter.config.StorageException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.web.uiapi.UIConfigStore;
import com.taskadapter.web.uiapi.UISyncConfig;

/**
 * Configuration operations.
 */
public final class ConfigOperations {

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
    public final File syncSandbox;

    public ConfigOperations(String userName,
            AuthorizedOperations authorizedOps, CredentialsManager credManager,
            UIConfigStore uiConfigStore, File syncSandbox) {
        this.userName = userName;
        this.authorizedOps = authorizedOps;
        this.credManager = credManager;
        this.uiConfigStore = uiConfigStore;
        this.syncSandbox = syncSandbox;
    }

    /**
     * Returns list of all configs which user owns.
     * 
     * @return list of configs which user owns.
     */
    public final List<UISyncConfig> getOwnedConfigs() {
        return uiConfigStore.getUserConfigs(userName);
    }

    /**
     * Returns list of configs which user can manage.
     * 
     * @return list of configs which user can manage.
     */
    public final List<UISyncConfig> getManageableConfigs() {
        if (!authorizedOps.canManagerPeerConfigs())
            return getOwnedConfigs();

        final List<UISyncConfig> res = new ArrayList<>();
        for (String user : credManager.listUsers())
            res.addAll(uiConfigStore.getUserConfigs(user));
        return res;
    }

    /**
     * Creates a new config and returns it.
     * 
     * @param descriptionString
     *            config description.
     * @param id1
     *            first connector id.
     * @param id2
     *            second connector id.
     * @return new config.
     * @throws StorageException
     *             if config cannot be created.
     */
    public UISyncConfig createNewConfig(String descriptionString, String id1, String id2,
                                        WebServerInfo connector1Info, WebServerInfo connector2Info) throws StorageException {
        return uiConfigStore.createNewConfig(userName, descriptionString, id1, id2, connector1Info, connector2Info);
    }

    /**
     * Deletes a config.
     * 
     * @param config
     *            config to delete.
     */
    public void deleteConfig(UISyncConfig config) {
        uiConfigStore.deleteConfig(config);
    }

    /**
     * Clones config. Current user became the owner of the clone.
     * 
     * @param config
     *            config to clone.
     * @throws StorageException
     *             if config cannot be cloned.
     */
    public void cloneConfig(UISyncConfig config) throws StorageException {
        uiConfigStore.cloneConfig(userName, config);
    }

    /**
     * Saves a config.
     * 
     * @param config
     *            config to save.
     * @throws StorageException
     *             if config cannot be saved.
     */
    public void saveConfig(UISyncConfig config) throws StorageException {
        uiConfigStore.saveConfig(config);

    }

    public scala.collection.Seq<ConnectorSetup> getAllConnectorSetups(String connectorId) {
        return uiConfigStore.getAllConnectorSetups(userName, connectorId);
    }
}

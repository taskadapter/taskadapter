package com.taskadapter.webui.config;

import java.util.ArrayList;
import java.util.List;

import com.taskadapter.auth.AuthorizedOperations;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.web.uiapi.UIConfigStore;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.service.CurrentUserInfo;

/** Provider of config accessor. */
public final class ConfigAccessorProvider {

    /** Current user info. */
    private final CurrentUserInfo currentUser;
    /** Operations authorized for the current user. */
    private final AuthorizedOperations authorizedOps;
    /** Configuration store accessor. */
    private final UIConfigStore configStore;
    /** User list accessor. */
    private final CredentialsManager usersManager;

    public ConfigAccessorProvider(CurrentUserInfo currentUser,
            AuthorizedOperations authorizedOps, UIConfigStore configStore,
            CredentialsManager usersManager) {
        super();
        this.currentUser = currentUser;
        this.authorizedOps = authorizedOps;
        this.configStore = configStore;
        this.usersManager = usersManager;
    }

    /**
     * Creates a default config accessor. This accessor provides configs only
     * for a given user. Also this accessor displays only a simple name of the
     * config.
     * 
     * @return configuration accessor.
     */
    private ConfigAccessor defaultAccessor() {
        return new ConfigAccessor() {
            @Override
            public String nameOf(UISyncConfig config) {
                return config.getLabel();
            }

            @Override
            public List<UISyncConfig> getConfigs() {
                return configStore.getUserConfigs(currentUser.getUserName());
            }
        };
    }

    /**
     * Creates a config accessor, which can access all configs in the system.
     * Displays user name along with the config label as a config name.
     * 
     * @return accessor for all configs in the system.
     */
    private ConfigAccessor allConfigAccessor() {
        return new ConfigAccessor() {
            @Override
            public String nameOf(UISyncConfig config) {
                return config.getOwnerName() + " : " + config.getLabel();
            }

            @Override
            public List<UISyncConfig> getConfigs() {
                /* userManager.listUsers.flatMap(configStore.getUserConfigs); */
                final List<UISyncConfig> res = new ArrayList<UISyncConfig>();
                for (String u : usersManager.listUsers())
                    res.addAll(configStore.getUserConfigs(u));
                return res;
            }
        };
    }

    /** Creates a best-suited accessor for the user. */
    public ConfigAccessor createBestAccessor() {
        return authorizedOps.canManagerPeerConfigs() ? allConfigAccessor()
                : defaultAccessor();
    }
}

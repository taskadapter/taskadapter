package com.taskadapter.config;

import com.taskadapter.web.uiapi.ConfigId;
import com.taskadapter.web.uiapi.SetupId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigStorageTest {
    private final static String login = "autotest";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private ConfigStorage storage;

    @Before
    public void beforeEachTest() {
        storage = new ConfigStorage(folder.getRoot());
    }

    @Test
    public void configCanBeCreatedInStorage() throws StorageException {
        var configId = createConfig(login);
        var config = storage.getConfig(configId);
        assertThat(config).isPresent();
        assertThat(config.get().getMappingsString()).isEqualTo("mappings");
    }

    @Test
    public void configIsDeleted() throws StorageException {
        var configId = createConfig(login);
        storage.deleteConfig(configId);
        assertThat(storage.getConfig(configId)).isNotPresent();
    }

    /**
     * regression test - a bug reported by user: NPE in getUserConfigs() where
     */
    @Test
    public void emptyStorageReturnsEmptyCollectionForGetUserConfigs() throws StorageException {
        createConfig(login);
        assertThat(storage.getUserConfigs(login)).isNotEmpty();
    }

    @Test
    public void configsAreLoadedByGetUserConfigs() throws StorageException {
        assertThat(storage.getUserConfigs(login)).isEmpty();
    }

    @Test
    public void getConfigsInFolderReturnsEmptyListForNullInput() {
        assertThat(storage.getConfigsInFolder(null)).isEmpty();
    }

    @Test
    public void getLegacyConfigsReturnsEmptyListForNullInput() {
        assertThat(storage.getLegacyConfigsInFolder("name", null, 1))
                .isEmpty();
    }

    private ConfigId createConfig(String login) throws StorageException {
        var connector1setupId = "web1";
        var connector2setupId = "web2";
        var configName = "some_config_name";
        return storage.createNewConfig(login, configName, "jira", new SetupId(connector1setupId), "value1",
                "jira", new SetupId(connector2setupId), "value2", "mappings");
    }
}

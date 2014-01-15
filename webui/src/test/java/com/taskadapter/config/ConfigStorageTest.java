package com.taskadapter.config;

import com.taskadapter.FileManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class ConfigStorageTest {
    private static final String ENCRYPTED = "test_encrypted";
    private static final String TEST_USER_LOGIN_NAME = "autotest";

    private ConfigStorage configStorage;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();


    @Before
    public void beforeEachTest() {
        configStorage = new ConfigStorage(new FileManager(tempFolder.getRoot()));
    }

    @Test
    public void checkSavingWorks() throws StorageException {
        configStorage.createNewConfig(TEST_USER_LOGIN_NAME, ENCRYPTED, "jira",
                "value1", "jira", "value2", "mappings?");

        final StoredExportConfig testConfigFile = findTestConfig(ENCRYPTED);
        assertNotNull("Test config file not found (might not be saved)", testConfigFile);

        configStorage.delete(testConfigFile.getId());
    }

    private StoredExportConfig findTestConfig(String taFileName) {
        List<StoredExportConfig> taFilesList = configStorage.getUserConfigs(TEST_USER_LOGIN_NAME);

        for (StoredExportConfig taFile : taFilesList) {
            if (taFile.getName().equals(taFileName)) {
                return taFile;
            }
        }
        return null;
    }
}

package com.taskadapter.config;

import com.taskadapter.PluginManager;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.jira.JiraConfig;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Alexey Skorokhodov
 */
public class ConfigStorageTest {
    private static final String PLAIN = "test_plain";
    private static final String ENCRYPTED = "test_encrypted";

    private static final String PLAIN_PASSWORD = "pLainPaSsW0rd";
    private static final String ENCRYPTED_PASSWORD = "eNcrYpTedPaSsW0rd";

    private ConfigStorage configStorage = new ConfigStorage(new PluginManager());


    @Test
    public void shouldSaveConfigWithEncryptedPassword() {
        //prepare test config with encrypted password
        JiraConfig config = new JiraConfig();
        config.setServerInfo(new WebServerInfo("some.host", "user_name", ENCRYPTED_PASSWORD));

        TAFile encryptedPasswordConfig = new TAFile(ENCRYPTED,
                new ConnectorDataHolder(config.getLabel(), config),
                new ConnectorDataHolder(config.getLabel(), config)
        );

        //save test config with encrypted password
        configStorage.createNewConfig(encryptedPasswordConfig);

        //find test config
        TAFile testConfigFile = findTestConfig(ENCRYPTED);
        assertNotNull("Test config file not found (might not be saved)", testConfigFile);

        //check for encrypted password
        ConnectorDataHolder dataHolder = testConfigFile.getConnectorDataHolder1();
        WebServerInfo serverInfo = ((JiraConfig)dataHolder.getData()).getServerInfo();
        assertEquals("Password is corrupted", ENCRYPTED_PASSWORD, serverInfo.getPassword());

        //cleanup
        configStorage.delete(testConfigFile);
    }

    @Test
    public void shouldReadConfigWithPlainPassword() {
        //prepare test config with plain password
        class TestWebServerInfo extends WebServerInfo {  // For testing we need to override
            public void setPassword(String password) {   // this method
                super.password = password;               // (to set only plain password)
            }
        }
        TestWebServerInfo serverInfo = new TestWebServerInfo();
        serverInfo.setPassword(PLAIN_PASSWORD);

        JiraConfig config = new JiraConfig();
        config.setServerInfo(serverInfo);

        TAFile plainPasswordConfig = new TAFile(PLAIN,
                new ConnectorDataHolder(config.getLabel(), config),
                new ConnectorDataHolder(config.getLabel(), config)
        );

        //save test config with plain password
        configStorage.createNewConfig(plainPasswordConfig);

        //find test config
        TAFile testConfigFile = findTestConfig(PLAIN);
        assertNotNull("Test config file not found (might not be saved)", testConfigFile);

        //check for plain password
        ConnectorDataHolder dataHolder = testConfigFile.getConnectorDataHolder1();
        WebServerInfo webServerInfo = ((JiraConfig)dataHolder.getData()).getServerInfo();
        assertEquals("Password is corrupted", PLAIN_PASSWORD, webServerInfo.getPassword());

        //cleanup
        configStorage.delete(testConfigFile);
    }

    private TAFile findTestConfig(String taFileName) {
        //get all configs
        List<TAFile> taFilesList = configStorage.getAllConfigs();

        TAFile testConfigFile = null;

        //find test config
        for (TAFile taFile : taFilesList) {
            if (taFile.getConfigLabel().equals(taFileName)) {
                testConfigFile = taFile;
                break;
            }
        }
        return testConfigFile;
    }
}

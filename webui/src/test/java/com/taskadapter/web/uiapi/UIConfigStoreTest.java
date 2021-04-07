package com.taskadapter.web.uiapi;

import com.taskadapter.common.ui.FieldMapping;
import com.taskadapter.config.StorageException;
import com.taskadapter.connector.jira.JiraConnector;
import com.taskadapter.connector.redmine.RedmineConnector;
import com.taskadapter.connector.testlib.TestDataLoader;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.Field;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UIConfigStoreTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void beforeEachTest() {
        ConfigFolderTestConfigurer.configure(tempFolder.getRoot());
    }

    @Test
    public void createsRedmineJiraConfigWithDescriptionMapped() throws StorageException {
        var store = TestUIConfigStoreFactory.createStore(tempFolder.getRoot());
        var configId = store.createNewConfig("admin", "label1", RedmineConnector.ID, ConfigFolderTestConfigurer.redmineSetupId,
                JiraConnector.ID, ConfigFolderTestConfigurer.jiraSetupId);
        var config = store.getConfig(configId).get();

        var row = findRow(config.getFieldMappings(), Optional.of(AllFields.description), Optional.of(AllFields.description));
        assertThat(row.isSelected()).isTrue();
        assertThat(row.getDefaultValue()).isNull();
    }

    @Test
    public void clonesConfig() throws StorageException {
        var store = TestUIConfigStoreFactory.createStore(tempFolder.getRoot());
        var config = store.createNewConfig("admin", "label1", RedmineConnector.ID, ConfigFolderTestConfigurer.redmineSetupId,
                JiraConnector.ID, ConfigFolderTestConfigurer.jiraSetupId);
        store.cloneConfig("admin", new ConfigId("admin", config.getId()));

        var configs = store.getUserConfigs("admin");
        assertThat(configs).hasSize(2);
    }

    private static FieldMapping<?> findRow(List<FieldMapping<?>> mappings,
                                           Optional<Field<?>> connector1Field,
                                           Optional<Field<?>> connector2Field) {
        var row = mappings.stream()
                .filter(m -> connector1Field.equals(m.getFieldInConnector1())
                        && connector2Field.equals(m.getFieldInConnector2()))
                .findAny();
        assertThat(row).isNotEmpty();
        return row.get();
    }
}

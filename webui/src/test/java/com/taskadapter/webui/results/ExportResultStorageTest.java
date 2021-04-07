package com.taskadapter.webui.results;

import com.taskadapter.config.StorageException;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.testlib.DateUtils;
import com.taskadapter.web.uiapi.ConfigFolderTestConfigurer;
import com.taskadapter.web.uiapi.ConfigId;
import com.taskadapter.web.uiapi.DecodedTaskError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class ExportResultStorageTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private ExportResultStorage storage;

    @Before
    public void beforeEachTest() {
        ConfigFolderTestConfigurer.configure(folder.getRoot());
        storage = new ExportResultStorage(folder.getRoot(), 10);
    }

    @Test
    public void canSaveAndLoadResultsWithErrors() {
        var result = new ExportResultFormat("1", new ConfigId("admin", 1),
                "label1", "from", "to", "", 1, 1, Arrays.asList("some general error"),
                Arrays.asList(
                        new DecodedTaskError(new TaskId(100L, "KEY100"),
                                "error summary",
                                "detailed error")
                ), DateUtils.getDateRoundedToMinutes(),
                100);
        storage.store(result);

        assertThat(storage.getSaveResults().get(0))
                .isEqualTo(result);
    }

    @Test
    public void emptyStorageReturnsEmptyList() throws StorageException {
        assertThat(storage.getSaveResults()).isEmpty();
    }
}

package com.taskadapter.webui.results;

import com.taskadapter.common.JsonUtil;
import com.taskadapter.connector.testlib.TestDataLoader;
import org.junit.Test;

import java.io.IOException;

public class ExportResultFormatTest {
    @Test
    public void canBeParsedFromPre2021Config() throws IOException {
        var fileContents = TestDataLoader.loadAsString("result/export-result-pre-2021.json");
        JsonUtil.parseJsonString(fileContents, ExportResultFormat.class);
    }
}

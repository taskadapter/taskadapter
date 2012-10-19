package com.taskadapter.connector.testlib;

import com.google.common.io.Files;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.IOException;

public abstract class FileBasedTest {

    protected File tempFolder;

    @Before
    public void beforeEachTest() {
        tempFolder = Files.createTempDir();
    }

    @After
    public void afterEachTest() throws IOException {
        FileDeleter.deleteRecursively(tempFolder);
    }

}

package com.taskadapter.webui;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TAApplicationTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void applicationCreatedOK() {
        new TAApplication(tempFolder.getRoot());
    }
}

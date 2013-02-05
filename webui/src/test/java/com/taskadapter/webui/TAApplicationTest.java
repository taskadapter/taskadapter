package com.taskadapter.webui;

import com.taskadapter.connector.testlib.FileBasedTest;
import org.junit.Test;

public class TAApplicationTest extends FileBasedTest {

    @Test
    public void applicationCreatedOK() {
        new TAApplication(tempFolder);
    }
}

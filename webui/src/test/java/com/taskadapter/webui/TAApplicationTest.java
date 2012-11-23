package com.taskadapter.webui;

import com.taskadapter.connector.testlib.FileBasedTest;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;

public class TAApplicationTest extends FileBasedTest {

    @Test
    public void applicationRespondsToRequestsWithoutInit() {
        TAApplication application = new TAApplication(tempFolder);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        application.onRequestStart(request, response);
    }
}

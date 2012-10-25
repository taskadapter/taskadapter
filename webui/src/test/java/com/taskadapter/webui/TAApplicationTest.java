package com.taskadapter.webui;

import com.taskadapter.config.User;
import com.taskadapter.connector.testlib.FileBasedTest;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.service.UserNotFoundException;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class TAApplicationTest extends FileBasedTest {

    @Test
    public void applicationRespondsToRequestsWithoutInit() {
        TAApplication application = new TAApplication();
        application.setDataRootFolder(tempFolder);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        application.onRequestStart(request, response);
    }

    @Test
    public void adminUserIsCreatedForApplication() throws UserNotFoundException {
        TAApplication application = new TAApplication();
        application.setDataRootFolder(tempFolder);
        Services services = application.getServices();
        User admin = services.getUserManager().getUser("admin");
        assertEquals("admin", admin.getLoginName());
    }

}

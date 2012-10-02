package com.taskadapter.webui;

import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TAApplicationTest {

    @Test
    public void applicationRespondsToRequestsWithoutInit() {
        TAApplication application = new TAApplication();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        application.onRequestStart(request, response);
    }
}

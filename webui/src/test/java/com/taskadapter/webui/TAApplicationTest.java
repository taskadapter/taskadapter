package com.taskadapter.webui;

import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;

public class TAApplicationTest {

    @Test
    public void applicationRespondsToRequestsWithoutInit() {
        TAApplication application = new TAApplication();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        application.onRequestStart(request, response);
    }
}

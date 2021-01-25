package com.taskadapter.app;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TALauncherTest {

    @Test
    public void portParameterFound() {
        String[] args = new String[]{"something", "--port=9090", "something else"};
        assertEquals(9090, TALauncher.findPortNumberInArgs(args));
    }

    @Test
    public void defaultPortReturnedWhenNoParameter() {
        String[] args = new String[]{"something and something else"};
        assertEquals(10842, TALauncher.findPortNumberInArgs(args));
    }

    @Test
    public void openInBrowserArgumentDetected() {
        String[] args = new String[]{TALauncher.PARAMETER_OPEN_TASK_ADAPTER_PAGE_IN_WEB_BROWSER};
        assertTrue(TALauncher.needToOpenBrowser(args));
    }
}

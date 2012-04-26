package com.taskadapter.launcher;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TALauncherTest {
    @Test
    public void portParameterFound() {
        String[] args = {"something", "--port=9090", "something else"};
        assertEquals(9090, TALauncher.findPortNumberInArgs(args));
    }

    @Test
    public void defaultPortReturnedWhenNoParameter() {
        String[] args = {"something and something else"};
        assertEquals(8080, TALauncher.findPortNumberInArgs(args));
    }

}

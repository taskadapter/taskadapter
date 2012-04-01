package com.taskadapter.connector.github;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GithubConfigTest {

    @Test
    public void defaultLabelIsSet() {
        assertEquals(GithubConfig.DEFAULT_LABEL, new GithubConfig().getLabel());
    }
}

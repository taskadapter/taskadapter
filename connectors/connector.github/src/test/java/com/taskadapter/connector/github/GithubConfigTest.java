package com.taskadapter.connector.github;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class GithubConfigTest {
    @Test
    public void labelNotNullByDefault() {
        assertNotNull("The label must be not null even if not provided when creating the config object", new GithubConfig().getLabel());
    }
}

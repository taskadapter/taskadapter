package com.taskadapter.connector.github;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GithubConfigTest {
    @Test
    public void defaultLabelIsSet() {
        assertThat(new GithubConfig().getLabel()).isEqualTo(GithubConfig.DEFAULT_LABEL);
    }
}

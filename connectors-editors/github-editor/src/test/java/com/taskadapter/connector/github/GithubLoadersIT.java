package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.github.editor.GithubLoaders;
import com.taskadapter.model.GProject;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GithubLoadersIT {

    @Test
    public void projectsAreLoaded() throws Exception {
        GithubConfig config = getTestConfig();
        List<GProject> projects = GithubLoaders.getProjects(config.getServerInfo());
        assertNotNull(projects);
        final GProject taProject = projects.get(0);
        assertEquals("tatest", taProject.getKey());
    }

    public GithubConfig getTestConfig() {
        // The Server URL is null: "github.com" will be used by the API by default.
        WebServerInfo wsi = new WebServerInfo("", "tatest", "123qweasd");
        GithubConfig config = new GithubConfig();
        config.getServerInfo().setUserName(wsi.getUserName());
        return config;
    }
}

package com.taskadapter.connector.github;

import com.taskadapter.connector.common.ConnectorUtils;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.github.editor.GithubLoaders;
import com.taskadapter.connector.testlib.TestMappingUtils;
import com.taskadapter.model.GProject;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class GithubTest {

    @Test
    public void projectsAreLoaded() throws Exception {
        GithubConfig config = getTestConfig();

        final GithubConnector connector = new GithubConnector(config);

        List<GProject> projects = GithubLoaders.getProjects(config.getServerInfo());
        assertNotNull(projects);
        for (GProject project : projects) {
            config.setProjectKey(project.getKey());
            final Mappings mappings = TestMappingUtils
                    .fromFields(GithubSupportedFields.SUPPORTED_FIELDS);
            System.out.println("project.getTasks() = " + ConnectorUtils.loadDataOrderedById(connector, mappings));
        }
    }

    public GithubConfig getTestConfig() {
        // The Server URL is null: "github.com" will be used by the API by default.
        WebServerInfo wsi = new WebServerInfo("", "joawl", "");
        GithubConfig config = new GithubConfig();
        config.getServerInfo().setUserName(wsi.getUserName());
        return config;
    }
}

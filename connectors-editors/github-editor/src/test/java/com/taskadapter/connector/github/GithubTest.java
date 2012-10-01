package com.taskadapter.connector.github;

import com.taskadapter.connector.common.ConnectorUtils;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.github.editor.GithubLoaders;
import com.taskadapter.model.GProject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class GithubTest {

    @BeforeClass
    public static void beforeClass() {
        System.out.println("--- Github tests started ---");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("--- Github tests finished ---");
    }

    @Test
    public void testProjectImport() throws Exception {
        GithubConfig config = getTestConfig();
        
        final GithubConnector connector = new GithubConnector(config);

        List<GProject> projects = GithubLoaders.getProjects(config.getServerInfo());
        assertNotNull(projects);
        System.out.println("projects.size() = " + projects.size());
        for (GProject project : projects) {
            System.out.println("project.getName() = " + project.getName());
            config.setProjectKey(project.getKey());
			System.out
					.println("project.getTasks() = "
							+ ConnectorUtils.loadDataOrderedById(connector));
            System.out.println("---");
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

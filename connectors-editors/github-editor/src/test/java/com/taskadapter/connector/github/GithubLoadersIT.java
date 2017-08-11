package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.github.editor.GithubLoaders;
import com.taskadapter.model.GProject;
import org.junit.Test;
import scala.Option;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GithubLoadersIT {

    @Test
    public void projectsAreLoaded() throws Exception {
        List<GProject> projects = GithubLoaders.getProjects(getSetup());
        assertNotNull(projects);
        final GProject taProject = projects.get(0);
        assertEquals("tatest", taProject.getName());
    }

    private WebConnectorSetup getSetup() {
        return new WebConnectorSetup(GithubConnector.ID(), Option.empty(), "my github",
                "https://github.com", "tatest", "123qweasd", false, "");
    }
}

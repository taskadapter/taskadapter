package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.github.editor.GithubProjectsListLoader;
import com.taskadapter.model.NamedKeyedObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GithubLoadersIT {

    @Test
    public void projectsAreLoaded() throws Exception {
        GithubProjectsListLoader loader = new GithubProjectsListLoader(getSetup());
        var list = loader.loadData();
        assertNotNull(list);
        final NamedKeyedObject taProject = list.get(0);
        assertEquals("tatest", taProject.getName());
    }

    private WebConnectorSetup getSetup() {
        return WebConnectorSetup.apply(GithubConnector.ID, "my github",
                "https://github.com", "tatest", "123qweasd", false, "");
    }
}

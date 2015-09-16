package com.taskadapter.connector.msp.write;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class ResourceManagerTest {

    @Test
    public void secondAssigneeWithSameNameIsNotDuplicatedInProjectFile() {
        ProjectFile projectFile = new ProjectFile();
        ResourceManager manager = new ResourceManager(projectFile);
        assertThat(projectFile.getAllResources()).isEmpty();
        final String name = "full name1";
        final Resource resource1 = manager.getOrCreateResource(name);

        assertThat(projectFile.getAllResources()).hasSize(1);

        final Resource resource2 = manager.getOrCreateResource(name);
        assertThat(projectFile.getAllResources()).hasSize(1);

        assertThat(resource1.getUniqueID()).isEqualTo(resource2.getUniqueID());
    }
}
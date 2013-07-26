package com.taskadapter.connector.msp.write;

import com.taskadapter.connector.msp.MSPUtils;
import com.taskadapter.model.GUser;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceType;

public class ResourceManager {

    private ProjectFile project;

    public ResourceManager(ProjectFile project) {
        this.project = project;
    }

    Resource getOrCreateResource(GUser assignee) {
        Resource resource = project.getResourceByUniqueID(assignee.getId());
        if (resource == null) {
            // we assume all resources are already in the 'cache' (map)
            resource = project.addResource();
            resource.setName(assignee.getDisplayName());
            resource.setType(ResourceType.WORK);

            if (assignee.getId() != null) {
                resource.setUniqueID(assignee.getId());
            }
            MSPUtils.markResourceAsOurs(resource);
        }
        return resource;
    }

}

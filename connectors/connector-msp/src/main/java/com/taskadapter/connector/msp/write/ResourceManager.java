package com.taskadapter.connector.msp.write;

import com.google.common.base.Optional;
import com.taskadapter.connector.msp.MSPUtils;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceType;

import java.util.List;

public final class ResourceManager {

    private final ProjectFile project;

    public ResourceManager(ProjectFile project) {
        this.project = project;
    }

    Resource getOrCreateResource(String name) {
        final Optional<Resource> optionalResource = findResourceByName(name);
        if (optionalResource.isPresent()) {
            return optionalResource.get();
        }
        return createResource(name);
    }

    private Resource createResource(String name) {
        final Resource newResource = project.addResource();
        newResource.setName(name);
        newResource.setType(ResourceType.WORK);
        MSPUtils.markResourceAsOurs(newResource);
        return newResource;
    }

    private Optional<Resource> findResourceByName(String name) {
        final List<Resource> allResources = project.getAllResources();
        for (Resource resource : allResources) {
            if (resource.getName().equals(name)) {
                return Optional.of(resource);
            }
        }
        return Optional.absent();
    }
}

package com.taskadapter.connector.github;

import com.taskadapter.connector.common.ProjectConverter;
import com.taskadapter.model.GProject;
import org.eclipse.egit.github.core.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts Github projects to "Generic" projects and back.
 * User: KhodyrevDS
 */
public class GithubProjectConverter implements ProjectConverter<Repository> {

    @Override
    public List<GProject> toGProjects(List<Repository> repositories) {
        List<GProject> projects = new ArrayList<GProject>(repositories.size());

        for (Repository repository : repositories) {
            GProject project = toGProject(repository);
            projects.add(project);
        }

        return projects;
    }

    public static GProject toGProject(Repository repository) {
        GProject project = new GProject();
        project.setName(repository.getName());
        project.setKey(repository.getName());
        project.setDescription(repository.getDescription());
        project.setHomepage(repository.getHomepage());

        if (repository.getName() != null && !"".equals(repository.getName())) {
            try {
                // TODO - think about String id's for GProject
                // (Github repositories doesn't have an integer ID, only string based user/repo name
                project.setId(Integer.valueOf(repository.generateId().hashCode()));
            } catch (NumberFormatException e) {
                throw new RuntimeException("should never happen", e);
            }
        }
        return project;
    }
}

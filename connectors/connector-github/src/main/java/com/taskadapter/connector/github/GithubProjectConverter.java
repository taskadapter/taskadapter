package com.taskadapter.connector.github;

import com.taskadapter.model.GProject;
import org.eclipse.egit.github.core.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts Github projects to "Generic" projects and back.
 */
public class GithubProjectConverter {

    public List<GProject> toGProjects(List<Repository> repositories) {
        List<GProject> projects = new ArrayList<>(repositories.size());

        for (Repository repository : repositories) {
            GProject project = toGProject(repository);
            projects.add(project);
        }

        return projects;
    }

    private static GProject toGProject(Repository repository) {
        return new GProject().setId(repository.getId())
                .setName(repository.getName())
                .setKey(repository.getName())
                .setDescription(repository.getDescription())
                .setHomepage(repository.getHomepage());
    }
}

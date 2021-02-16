package com.taskadapter.connector.jira;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.taskadapter.model.GProject;

public class JiraProjectConverter {

    public List<GProject> toGProjects(Iterable<BasicProject> objects) {
        List<GProject> projects = new ArrayList<>();
        for (BasicProject rmProject : objects) {
            GProject project = toGProject(rmProject);
            projects.add(project);
        }
        return projects;
    }

    public GProject toGProject(BasicProject jiraProject) {
        return new GProject()
                .setId(jiraProject.getId())
                .setName(jiraProject.getName())
                .setKey(jiraProject.getKey())
                .setDescription("")
                .setHomepage("");
    }

    public GProject toGProject(Project jiraProject) {
        URI projectURI = jiraProject.getUri();
        String uri = "";
        if (projectURI != null) {
            uri = projectURI.toString();
        }
        return new GProject()
                .setId(jiraProject.getId())
                .setName(jiraProject.getName())
                .setKey(jiraProject.getKey())
                .setDescription(jiraProject.getDescription())
                .setHomepage(uri);
    }
}

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
        GProject gProject = new GProject();
        gProject.setKey(jiraProject.getKey());
        gProject.setName(jiraProject.getName());
        //gProject.setDescription(jiraProject.getDescription());
        //gProject.setHomepage(jiraProject.get().toString());
        // we know that getId returns a string, which in fact holds an integer,
        // so conversion to Integer is OK here.
        //gProject.setId(Integer.valueOf(jiraProject.getId()));
        return gProject;
    }

    public GProject toGProject(Project jiraProject) {
        GProject gProject = new GProject();
        gProject.setKey(jiraProject.getKey());
        gProject.setName(jiraProject.getName());
        gProject.setDescription(jiraProject.getDescription());
        final URI projectURI = jiraProject.getUri();
        if (projectURI != null) {
            gProject.setHomepage(projectURI.toString());
        }
        // we know that getId returns a string, which in fact holds an integer,
        // so conversion to Integer is OK here.
        //gProject.setId(Integer.valueOf(jiraProject.getId()));
        return gProject;
    }

}

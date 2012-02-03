package com.taskadapter.connector.jira;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.rpc.soap.client.RemoteProject;
import com.taskadapter.connector.common.ProjectConverter;
import com.taskadapter.model.GProject;

public class JiraProjectConverter implements ProjectConverter<RemoteProject> {

    @Override
    public List<GProject> toGProjects(List<RemoteProject> objects) {
        List<GProject> projects = new ArrayList<GProject>();
        for (RemoteProject rmProject : objects) {
            GProject project = toGProject(rmProject);
            projects.add(project);
        }
        return projects;
    }

    public GProject toGProject(RemoteProject jiraProject) {
        GProject gProject = new GProject();
        gProject.setKey(jiraProject.getKey());
        gProject.setName(jiraProject.getName());
        gProject.setDescription(jiraProject.getDescription());
        gProject.setHomepage(jiraProject.getUrl());
        // we know that getId returns a string, which in fact holds an integer,
        // so conversion to Integer is OK here.
        gProject.setId(Integer.valueOf(jiraProject.getId()));
        return gProject;
    }

}

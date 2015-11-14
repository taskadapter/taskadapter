package com.taskadapter.connector.redmine.converter;

import com.taskadapter.model.GProject;
import com.taskadapter.redmineapi.bean.Project;

import java.util.ArrayList;
import java.util.List;

public class RedmineProjectConverter {

    public List<GProject> toGProjects(List<Project> objects) {
        List<GProject> projects = new ArrayList<>();
        for (Project rmProject : objects) {
            GProject project = convertToGProject(rmProject);
            projects.add(project);
        }
        return projects;
    }

    public GProject convertToGProject(Project rmProject) {

        GProject project = new GProject();
        project.setName(rmProject.getName());
        project.setId(rmProject.getId());
        project.setKey(rmProject.getIdentifier());
        project.setDescription(rmProject.getDescription());
        project.setHomepage(rmProject.getHomepage());
        return project;
    }
}

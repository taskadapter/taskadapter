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

    public static GProject convertToGProject(Project rmProject) {
        return new GProject().setId(Long.valueOf(rmProject.getId()))
                .setName(rmProject.getName())
                .setKey(rmProject.getIdentifier())
                .setDescription(rmProject.getDescription())
                .setHomepage(rmProject.getHomepage());
    }
}

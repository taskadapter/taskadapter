package com.taskadapter.connector.mantis;

import biz.futureware.mantis.rpc.soap.client.ProjectData;
import com.taskadapter.model.GProject;

import java.util.ArrayList;
import java.util.List;

public class MantisProjectConverter {

    public List<GProject> toGProjects(List<ProjectData> objects) {
        List<GProject> projects = new ArrayList<GProject>();
        for (ProjectData rmProject : objects) {
            GProject project = toGProject(rmProject);
            projects.add(project);
        }
        return projects;
    }

    public GProject toGProject(ProjectData mantisProject) {
        GProject gProject = new GProject();
        gProject.setKey(String.valueOf(mantisProject.getId()));
        gProject.setName(mantisProject.getName());
        gProject.setDescription(mantisProject.getDescription());
        gProject.setId(mantisProject.getId().intValue());
        return gProject;
    }
}

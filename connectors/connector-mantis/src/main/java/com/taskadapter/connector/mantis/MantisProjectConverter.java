package com.taskadapter.connector.mantis;

import biz.futureware.mantis.rpc.soap.client.ProjectData;
import com.taskadapter.model.GProject;

import java.util.List;
import java.util.stream.Collectors;

public class MantisProjectConverter {
    public static List<GProject> toGProjects(List<ProjectData> objects) {
        return objects.stream().map(MantisProjectConverter::toGProject)
                .collect(Collectors.toList());
    }

    private static GProject toGProject(ProjectData mantisProject) {
        return new GProject()
                .setId(mantisProject.getId().longValue())
                .setName(mantisProject.getName())
                .setKey(String.valueOf(mantisProject.getId()))
                .setDescription(mantisProject.getDescription())
                .setHomepage("");
    }
}

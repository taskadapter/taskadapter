package com.taskadapter.connector.common;

import java.util.List;

import com.taskadapter.model.GProject;

public interface ProjectConverter<T> {
    //	public GProject convertToGProject(T project);
    public List<GProject> toGProjects(List<T> projects);
}

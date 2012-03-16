package com.taskadapter.connector.common;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.model.GProject;

import java.util.List;

public interface ProjectLoader {
    List<GProject> getProjects(WebServerInfo serverInfo) throws Exception;
}

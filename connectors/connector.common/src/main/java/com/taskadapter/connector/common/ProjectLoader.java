package com.taskadapter.connector.common;

import java.util.List;

import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.model.GProject;

public interface ProjectLoader {
	List<GProject> getProjects(WebServerInfo serverInfo) throws ValidationException;
}

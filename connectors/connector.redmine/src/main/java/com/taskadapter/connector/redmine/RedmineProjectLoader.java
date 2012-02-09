package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.ProjectLoader;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.model.GProject;
import org.redmine.ta.RedmineManager;

import java.util.List;

public class RedmineProjectLoader implements ProjectLoader {

    @Override
    public List<GProject> getProjects(WebServerInfo serverInfo) throws ValidationException {
        if ((serverInfo.getHost() == null)
                || (serverInfo.getHost().isEmpty())) {
            throw new ValidationException("Host URL is not set");
        }

        RedmineManager mgr = RedmineManagerFactory.createRedmineManager(serverInfo);
        List<org.redmine.ta.beans.Project> rmProjects;
        try {
            rmProjects = mgr.getProjects();
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }

        return new RedmineProjectConverter().toGProjects(rmProjects);
    }


}

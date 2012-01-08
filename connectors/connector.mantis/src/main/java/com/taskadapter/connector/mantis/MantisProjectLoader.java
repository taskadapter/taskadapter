package com.taskadapter.connector.mantis;

import com.taskadapter.connector.common.ProjectLoader;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.model.GProject;
import org.mantis.ta.MantisManager;

import java.util.List;

public class MantisProjectLoader implements ProjectLoader {

    @Override
    public List<GProject> getProjects(WebServerInfo serverInfo) throws ValidationException {
        if (!serverInfo.isHostSet()) {
            throw new ValidationException("Host URL is not set");
        }

        MantisManager mgr = MantisManagerFactory.createMantisManager(serverInfo);
        List<org.mantis.ta.beans.ProjectData> mntProjects;

        try {
            mntProjects = mgr.getProjects();
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }

        return new MantisProjectConverter().toGProjects(mntProjects);
    }

}

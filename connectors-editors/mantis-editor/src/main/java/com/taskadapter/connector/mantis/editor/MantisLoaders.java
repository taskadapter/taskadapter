package com.taskadapter.connector.mantis.editor;

import java.util.List;

import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import org.mantis.ta.MantisManager;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.mantis.MantisManagerFactory;
import com.taskadapter.connector.mantis.MantisProjectConverter;
import com.taskadapter.model.GProject;

public class MantisLoaders {
    public static List<GProject> getProjects(WebServerInfo serverInfo) throws ServerURLNotSetException {
        validate(serverInfo);

        MantisManager mgr = MantisManagerFactory
                .createMantisManager(serverInfo);
        List<org.mantis.ta.beans.ProjectData> mntProjects;

        try {
            mntProjects = mgr.getProjects();
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }

        return new MantisProjectConverter().toGProjects(mntProjects);
    }

    private static void validate(WebServerInfo serverInfo) throws ServerURLNotSetException {
        if (!serverInfo.isHostSet()) {
            throw new ServerURLNotSetException();
        }
    }

}

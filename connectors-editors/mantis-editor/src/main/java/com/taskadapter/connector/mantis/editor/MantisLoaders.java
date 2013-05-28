package com.taskadapter.connector.mantis.editor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import biz.futureware.mantis.rpc.soap.client.FilterData;
import biz.futureware.mantis.rpc.soap.client.ProjectData;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.mantis.MantisConfig;
import com.taskadapter.connector.mantis.MantisManager;
import com.taskadapter.connector.mantis.MantisManagerFactory;
import com.taskadapter.connector.mantis.MantisProjectConverter;
import com.taskadapter.connector.mantis.MantisUtils;
import com.taskadapter.model.GProject;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.model.NamedKeyedObjectImpl;

public class MantisLoaders {
    public static List<GProject> getProjects(WebServerInfo serverInfo) throws ServerURLNotSetException {
        validate(serverInfo);

        MantisManager mgr = MantisManagerFactory
                .createMantisManager(serverInfo);
        List<ProjectData> mntProjects;

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

    public static List<NamedKeyedObject> getFilters(MantisConfig config)
            throws ConnectorException {
        MantisManager mgr = MantisManagerFactory.createMantisManager(config
                .getServerInfo());
        try {
            final BigInteger pkey = config.getProjectKey() == null ? null
                    : new BigInteger(config.getProjectKey());
            final FilterData[] fis = mgr.getFilters(pkey);
            final List<NamedKeyedObject> res = new ArrayList<NamedKeyedObject>(
                    fis.length);
            for (FilterData fi : fis) {
                res.add(new NamedKeyedObjectImpl(fi.getId().toString(), fi
                        .getName()));
            }
            return res;
        } catch (java.rmi.RemoteException e) {
            throw MantisUtils.convertException(e);
        }
    }
    
}

package com.taskadapter.connector.mantis;

import com.taskadapter.connector.definition.*;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.model.GTask;
import com.taskadapter.mantisapi.MantisManager;
import com.taskadapter.mantisapi.beans.IssueData;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MantisConnector implements Connector<MantisConfig> {

    public static final String ID = "Mantis";

    private MantisConfig config;

    public MantisConnector(MantisConfig config) {
        this.config = config;
    }

    @Override
    public void updateRemoteIDs(Map<Integer, String> res, ProgressMonitor monitor, Mappings mappings) throws UnsupportedConnectorOperation {
        throw new UnsupportedConnectorOperation("updateRemoteIDs");
    }

    @Override
    public GTask loadTaskByKey(String key, Mappings mappings) throws ConnectorException {
            MantisManager mgr = MantisManagerFactory.createMantisManager(config.getServerInfo());

            IssueData issue;
            try {
                issue = mgr.getIssueById(new BigInteger(key));
            } catch (RemoteException e) {
                throw MantisUtils.convertException(e);
            }
            return MantisDataConverter.convertToGenericTask(issue);
    } 
    
    @Override
    public List<GTask> loadData(Mappings mappings, ProgressMonitor monitorIGNORED) throws ConnectorException {
        try {
            MantisManager mgr = MantisManagerFactory.createMantisManager(config.getServerInfo());

            final Long queryId = config.getQueryId();
            final BigInteger pkey = config.getProjectKey() == null ? null
                    : new BigInteger(config.getProjectKey());
            List<IssueData> issues = queryId == null ? mgr
                    .getIssuesByProject(pkey)
                    : mgr.getIssuesByFilter(
                            pkey,
                            BigInteger.valueOf(queryId));
            return convertToGenericTasks(issues);
        } catch (RemoteException e) {
            throw MantisUtils.convertException(e);
        } 
    }
    
    private List<GTask> convertToGenericTasks(List<IssueData> issues) {
        List<GTask> result = new ArrayList<GTask>(issues.size());
        for (IssueData issue : issues) {
            GTask task = MantisDataConverter.convertToGenericTask(issue);
            result.add(task);
        }
        return result;
    }

    
    @Override
    public TaskSaveResult saveData(List<GTask> tasks, ProgressMonitor monitor, Mappings mappings) throws ConnectorException {
    	return new MantisTaskSaver(config, mappings, monitor).saveData(tasks);
    }
}

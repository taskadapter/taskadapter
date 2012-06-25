package com.taskadapter.connector.mantis;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.mantis.ta.MantisManager;
import org.mantis.ta.beans.IssueData;

import com.taskadapter.connector.common.AbstractConnector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;

public class MantisConnector extends AbstractConnector<MantisConfig> {

    public MantisConnector(MantisConfig config) {
        super(config);
    }

    @Override
    public void updateRemoteIDs(ConnectorConfig configuration,
                                SyncResult res, ProgressMonitor monitor) {
        throw new RuntimeException("not implemented for this connector");

    }

    @Override
    public Descriptor getDescriptor() {
        return MantisDescriptor.instance;
    }

    @Override
    public GTask loadTaskByKey(String key) {
        try {
            MantisManager mgr = MantisManagerFactory.createMantisManager(config.getServerInfo());

            IssueData issue = mgr.getIssueById(new BigInteger(key));
            return MantisDataConverter.convertToGenericTask(issue);
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }
    } 
    
    @Override
    public List<GTask> loadData(ProgressMonitor monitorIGNORED) {
        try {
            MantisManager mgr = MantisManagerFactory.createMantisManager(config.getServerInfo());

            List<IssueData> issues = mgr.getIssuesByProject(new BigInteger(config.getProjectKey()));
            return convertToGenericTasks(issues);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
    public SyncResult saveData(List<GTask> tasks, ProgressMonitor monitor) throws ConnectorException {
    	return new MantisTaskSaver(config).saveData(tasks, monitor);
    }
}

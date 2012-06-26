package com.taskadapter.connector.mantis;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.mantis.ta.MantisManager;
import org.mantis.ta.beans.IssueData;

import com.taskadapter.connector.common.AbstractConnector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.connector.definition.TaskErrors;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.model.GTask;

public class MantisConnector extends AbstractConnector<MantisConfig> {

    public MantisConnector(MantisConfig config) {
        super(config);
    }

    @Override
    public void updateRemoteIDs(ConnectorConfig configuration,
                                Map<Integer, String> res, ProgressMonitor monitor) throws UnsupportedConnectorOperation {
        throw new UnsupportedConnectorOperation(
                "updateRemotedIDs not implemented for matnis connector");
    }

    @Override
    public Descriptor getDescriptor() {
        return MantisDescriptor.instance;
    }

    @Override
    public GTask loadTaskByKey(String key) throws ConnectorException {
            MantisManager mgr = MantisManagerFactory.createMantisManager(config.getServerInfo());

            IssueData issue;
            try {
                issue = mgr.getIssueById(new BigInteger(key));
            } catch (RemoteException e) {
                throw MantisUtils.convertException(e);
            } catch (ServiceException e) {
                throw MantisUtils.convertException(e);
            }
            return MantisDataConverter.convertToGenericTask(issue);
    } 
    
    @Override
    public List<GTask> loadData(ProgressMonitor monitorIGNORED) throws ConnectorException {
        try {
            MantisManager mgr = MantisManagerFactory.createMantisManager(config.getServerInfo());

            List<IssueData> issues = mgr.getIssuesByProject(new BigInteger(config.getProjectKey()));
            return convertToGenericTasks(issues);
        } catch (RemoteException e) {
            throw MantisUtils.convertException(e);
        } catch (ServiceException e) {
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
    public SyncResult<TaskSaveResult, TaskErrors<Throwable>> saveData(List<GTask> tasks, ProgressMonitor monitor) throws ConnectorException {
    	return new MantisTaskSaver(config).saveData(tasks, monitor);
    }
}

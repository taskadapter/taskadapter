package com.taskadapter.connector.mantis;

import com.taskadapter.connector.common.AbstractTaskSaver;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.EntityProcessingException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.mantisapi.MantisManager;
import com.taskadapter.mantisapi.RequiredItemException;
import com.taskadapter.mantisapi.beans.AccountData;
import com.taskadapter.mantisapi.beans.IssueData;
import com.taskadapter.mantisapi.beans.ProjectData;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class MantisTaskSaver extends AbstractTaskSaver<MantisConfig> {

    private MantisManager mgr;
    private ProjectData mntProject;
    private MantisDataConverter converter;
	private Mappings mappings;

    public MantisTaskSaver(MantisConfig config, Mappings mappings) {
        super(config);
		this.mappings = mappings;
    }

    @Override
    public void beforeSave() throws ConnectorException {
        this.mgr = MantisManagerFactory.createMantisManager(config.getServerInfo());
        try {
            mntProject = mgr.getProjectById(new BigInteger(config.getProjectKey()));
            converter = new MantisDataConverter(config);
            converter.setUsers(loadUsers());
        } catch (RemoteException e) {
            throw MantisUtils.convertException(e);
        } 
    }

    private List<AccountData> loadUsers() {
        List<AccountData> users;
        if (config.isFindUserByName()) {
            try {
                users = mgr.getUsers();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            users = new ArrayList<AccountData>();
        }
        return users;
    }

    @Override
    protected Object convertToNativeTask(GTask task) {
        return converter.convertToMantisIssue(mntProject, task, mappings);
    }

    @Override
    protected GTask createTask(Object nativeTask) throws ConnectorException {
        try {
            BigInteger issueId = mgr.createIssue((IssueData) nativeTask);
            IssueData createdIssue = mgr.getIssueById(issueId);
            return MantisDataConverter.convertToGenericTask(createdIssue);
        } catch (RemoteException e) {
            throw MantisUtils.convertException(e);
        } catch (RequiredItemException e) {
            throw new EntityProcessingException(e);
        }
    }

    @Override
    protected void updateTask(String taskId, Object nativeTask) throws ConnectorException {
        IssueData mntIssue = (IssueData) nativeTask;

        try {
            mgr.updateIssue(new BigInteger(taskId), mntIssue);
        } catch (RemoteException e) {
            throw MantisUtils.convertException(e);
        } 
    }

    @Override
    protected void saveRelations(List<GRelation> relations) throws UnsupportedConnectorOperation {
        throw new UnsupportedConnectorOperation("saveRelations");
    }
}

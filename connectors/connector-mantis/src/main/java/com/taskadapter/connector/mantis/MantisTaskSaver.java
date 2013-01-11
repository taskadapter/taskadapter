package com.taskadapter.connector.mantis;

import com.taskadapter.connector.common.BasicIssueSaveAPI;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.EntityProcessingException;
import com.taskadapter.mantisapi.MantisManager;
import com.taskadapter.mantisapi.RequiredItemException;
import com.taskadapter.mantisapi.beans.IssueData;

import java.math.BigInteger;
import java.rmi.RemoteException;

final class MantisTaskSaver implements BasicIssueSaveAPI<IssueData> {

    private final MantisManager mgr;

    public MantisTaskSaver(MantisManager mgr) throws ConnectorException {
        this.mgr = mgr;
    }

    @Override
    public String createTask(IssueData nativeTask) throws ConnectorException {
        try {
            final BigInteger issueId = mgr.createIssue(nativeTask);
            return String.valueOf(issueId);
        } catch (RemoteException e) {
            throw MantisUtils.convertException(e);
        } catch (RequiredItemException e) {
            throw new EntityProcessingException(e);
        }
    }

    @Override
    public void updateTask(String taskId, IssueData nativeTask) throws ConnectorException {
        try {
            mgr.updateIssue(new BigInteger(taskId), nativeTask);
        } catch (RemoteException e) {
            throw MantisUtils.convertException(e);
        } 
    }
}

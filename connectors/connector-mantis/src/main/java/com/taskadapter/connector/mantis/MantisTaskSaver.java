package com.taskadapter.connector.mantis;

import biz.futureware.mantis.rpc.soap.client.IssueData;
import com.taskadapter.connector.common.BasicIssueSaveAPI;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.EntityProcessingException;

import java.math.BigInteger;
import java.rmi.RemoteException;

final class MantisTaskSaver implements BasicIssueSaveAPI<IssueData> {

    private final MantisManager mgr;

    public MantisTaskSaver(MantisManager mgr) {
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
    public void updateTask(IssueData nativeTask) throws ConnectorException {
        try {
            mgr.updateIssue(nativeTask);
        } catch (RemoteException e) {
            throw MantisUtils.convertException(e);
        } 
    }
}

package com.taskadapter.connector.mantis;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.EntityProcessingException;

public final class MantisUtils {
    public static ConnectorException convertException(RemoteException e) {
        return new CommunicationException(e);
    }

    public static ConnectorException convertException(ServiceException e) {
        return new EntityProcessingException(e);
    }
}

package com.taskadapter.core;

import java.util.Map;

import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTaskDescriptor.FIELD;

/**
 * Updater for remote IDs.
 * 
 */
public final class RemoteIdUpdater {

    private RemoteIdUpdater() {
        throw new UnsupportedOperationException();
    }

    public static void updateRemoteIds(Map<Integer, String> remappedIds,
            Mappings sourceMappings, Connector<?> connector)
            throws ConnectorException {
        if (sourceMappings.isFieldSelected(FIELD.REMOTE_ID)
                && remappedIds.size() > 0) {
            connector.updateRemoteIDs(remappedIds,
                    ProgressMonitorUtils.getDummyMonitor(), sourceMappings);
        }

    }

}

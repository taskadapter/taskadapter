package com.taskadapter.core;

import java.util.List;
import java.util.Map;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.common.ProgressMonitorUtils;
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
                                       List<FieldRow> rows, NewConnector connector)
            throws ConnectorException {
        // TODO TA3 restore
//        if (sourceMappings.isFieldSelected(FIELD.REMOTE_ID) && remappedIds.size() > 0) {
//            connector.updateRemoteIDs(remappedIds,ProgressMonitorUtils.DUMMY_MONITOR, rows);
//        }

    }

}

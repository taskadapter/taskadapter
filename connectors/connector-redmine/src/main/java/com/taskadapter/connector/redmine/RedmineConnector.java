package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.AbstractConnector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SyncResult;

public class RedmineConnector extends AbstractConnector<RedmineConfig> {

    public RedmineConnector(ConnectorConfig config) {
        super((RedmineConfig) config);
    }

    // TODO check if should change this to a flat list,
    // like it's already done for loadData() operation

    @Override
    public void updateRemoteIDs(ConnectorConfig configuration,
                                SyncResult res, ProgressMonitor monitor) {
        throw new RuntimeException("not implemented for this connector");
    }

    @Override
    public Descriptor getDescriptor() {
        return RedmineDescriptor.instance;
    }

}

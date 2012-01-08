package com.taskadapter.connector.mantis;

import com.taskadapter.connector.common.AbstractConnector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SyncResult;

public class MantisConnector extends AbstractConnector<MantisConfig> {

	public MantisConnector(ConnectorConfig config) {
		super((MantisConfig) config);
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

}

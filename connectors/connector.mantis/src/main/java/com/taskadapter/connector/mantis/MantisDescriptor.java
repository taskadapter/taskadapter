package com.taskadapter.connector.mantis;

import com.taskadapter.connector.common.PriorityLoader;
import com.taskadapter.connector.common.ProjectLoader;
import com.taskadapter.connector.common.TaskLoader;
import com.taskadapter.connector.common.TaskSaver;
import com.taskadapter.connector.definition.AvailableFieldsProvider;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;

import java.util.Arrays;
import java.util.Collection;

public class MantisDescriptor implements Descriptor {

	public static final MantisDescriptor instance = new MantisDescriptor();
	
	public static final String ID = "Mantis";
	
	private static final String INFO = "Mantis connector (supports Mantis v. 1.1.1+)";

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String getLabel() {
		return MantisConfig.DEFAULT_LABEL;
	}
    @Override
    public String getDescription() {
        return INFO;
    }

	@Override
	public ConnectorConfig createDefaultConfig() {
		return new MantisConfig();
	}

	@Override
	public Class<MantisConfig> getConfigClass() {
		return MantisConfig.class;
	}

	@Override
	public AvailableFieldsProvider getAvailableFieldsProvider() {
		return new MantisAvailableFieldsProvider();
	}

	@Override
	public ProjectLoader getProjectLoader() {
		return new MantisProjectLoader();
	}

	@Override
	public TaskSaver<MantisConfig> getTaskSaver(ConnectorConfig config) {
		return new MantisTaskSaver((MantisConfig) config);
	}

	@Override
	public TaskLoader getTaskLoader() {
		return new MantisTaskLoader();
	}

	@Override
	public Connector createConnector(ConnectorConfig config) {
		return new MantisConnector((MantisConfig) config);
	}

    @Override
	public Collection<Feature> getSupportedFeatures() {
		return Arrays.asList(Feature.LOAD_TASK, Feature.SAVE_TASK);
	}
    
	@Override
	public PriorityLoader getPriorityLoader() {
		throw new RuntimeException("NOT READY");
	}
}

package com.taskadapter.connector.github;

import com.taskadapter.connector.common.PriorityLoader;
import com.taskadapter.connector.common.ProjectLoader;
import com.taskadapter.connector.common.TaskLoader;
import com.taskadapter.connector.common.TaskSaver;
import com.taskadapter.connector.definition.*;

import java.util.Arrays;
import java.util.Collection;

public class GithubDescriptor implements Descriptor {
    public static final GithubDescriptor instance = new GithubDescriptor();

    private static final String ID = "GithubConnector v1";
    private static final String DESCRIPTION = "Github connector";
    private static final String LABEL = "Github.com";

    public ConnectorConfig createDefaultConfig() {
        return new GithubConfig();
    }

    public AvailableFieldsProvider getAvailableFieldsProvider() {
        return new GithubFieldsProvider();
    }

    public ProjectLoader getProjectLoader() {
        return new GithubProjectLoader();
    }

    public TaskSaver getTaskSaver(ConnectorConfig config) {
        return new GithubTaskSaver((GithubConfig) config);
    }

    public TaskLoader getTaskLoader() {
        return new GithubTaskLoader();
    }

    public String getLabel() {
        return LABEL;
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public String getID() {
        return ID;
    }

    @Override
    public String toString() {
        return getID();
    }

    public Class<?> getConfigClass() {
        return GithubConfig.class;
    }

    public Collection<Feature> getSupportedFeatures() {
        return Arrays.asList(Feature.LOAD_TASK, Feature.SAVE_TASK);
    }

    public boolean isSupported(Feature feature) {
        return getSupportedFeatures().contains(feature);
    }

    @Override
    public PriorityLoader getPriorityLoader() {
        throw new RuntimeException("NOT READY");
    }

    @Override
    public PluginFactory getPluginFactory() {
        return new GithubFactory();
    }
}

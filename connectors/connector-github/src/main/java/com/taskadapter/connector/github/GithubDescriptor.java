package com.taskadapter.connector.github;

import com.taskadapter.connector.common.PriorityLoader;
import com.taskadapter.connector.common.ProjectLoader;
import com.taskadapter.connector.common.TaskLoader;
import com.taskadapter.connector.common.TaskSaver;
import com.taskadapter.connector.definition.AvailableFieldsProvider;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;

import java.util.Arrays;
import java.util.Collection;

public class GithubDescriptor implements Descriptor {
    public static final GithubDescriptor instance = new GithubDescriptor();

    /**
     * Keep it the same to enable backward compatibility with the existing
     * config files.
     */
    private static final String ID = "Github";
    private static final String DESCRIPTION = "Github connector";
    private static final String LABEL = "Github";

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

    public Class<GithubConfig> getConfigClass() {
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

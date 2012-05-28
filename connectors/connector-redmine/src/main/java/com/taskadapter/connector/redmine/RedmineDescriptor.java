package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.PriorityLoader;
import com.taskadapter.connector.common.ProjectLoader;
import com.taskadapter.connector.common.TaskLoader;
import com.taskadapter.connector.common.TaskSaver;
import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.AvailableFieldsBuilder;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.model.GTaskDescriptor.FIELD;

import java.util.Arrays;
import java.util.Collection;

public class RedmineDescriptor implements Descriptor {
    private static final String INFO_TEXT = "Redmine/Chiliproject connector. Connects to Redmine servers via REST API. Supports Redmine v. 1.1+";

    /**
     * Keep it the same to enable backward compatibility for previously created config files.
     */
    private static final String ID = "Redmine REST";

    public static final RedmineDescriptor instance = new RedmineDescriptor();

    /**
     * Supported fields.
     */
    private static final AvailableFields SUPPORTED_FIELDS;
    
    static {
    	final AvailableFieldsBuilder builder = AvailableFieldsBuilder.start();
    	builder.addField(FIELD.SUMMARY, "Summary");
    	builder.addField(FIELD.DESCRIPTION, "Description");
    	builder.addField(FIELD.TASK_TYPE, "Tracker type");
    	builder.addField(FIELD.ESTIMATED_TIME, "Estimated time");
    	builder.addField(FIELD.DONE_RATIO, "Done ratio");
    	builder.addField(FIELD.ASSIGNEE, "Assignee");
    	builder.addField(FIELD.DUE_DATE, "Due Date");
    	builder.addField(FIELD.START_DATE, "Start date");
    	builder.addField(FIELD.TASK_STATUS, "Task status");
    	SUPPORTED_FIELDS = builder.end();
    }
    
    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String toString() {
        return getID();
    }

    @Override
    public RedmineConfig createDefaultConfig() {
        return new RedmineConfig();
    }

    @Override
    public String getDescription() {
        return INFO_TEXT;
    }

    @Override
    public String getLabel() {
        return RedmineConfig.DEFAULT_LABEL;
    }

    @Override
    public Class<RedmineConfig> getConfigClass() {
        return RedmineConfig.class;
    }

    @Override
    public AvailableFields getAvailableFields() {
        return SUPPORTED_FIELDS;
    }

    @Override
    public ProjectLoader getProjectLoader() {
        return new RedmineProjectLoader();
    }

    @Override
    public TaskLoader<RedmineConfig> getTaskLoader() {
        return new RedmineTaskLoader();
    }

    @Override
    public TaskSaver<RedmineConfig> getTaskSaver(ConnectorConfig config) {
        return new RedmineTaskSaver((RedmineConfig) config);
    }

    @Override
    public Collection<Feature> getSupportedFeatures() {
        return Arrays.asList(Feature.LOAD_TASK, Feature.SAVE_TASK, Feature.UPDATE_TASK, Feature.TASK_TYPE);
    }

    @Override
    public PriorityLoader getPriorityLoader() {
        throw new RuntimeException("NOT READY");
    }
}

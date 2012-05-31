package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.AvailableFieldsBuilder;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.model.GTaskDescriptor.FIELD;

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
    
    /**
     * List of supported fields.
     */
    private static final AvailableFields SUPPORTED_FIELDS;
    
    static {
    	final AvailableFieldsBuilder builder = AvailableFieldsBuilder.start();
    	builder.addField(FIELD.SUMMARY, "Summary");
    	builder.addField(FIELD.DESCRIPTION, "Description");
    	builder.addField(FIELD.ASSIGNEE, "Assignee");
    	builder.addField(FIELD.START_DATE, "Start date");
    	SUPPORTED_FIELDS = builder.end();
    }

    public ConnectorConfig createDefaultConfig() {
        return new GithubConfig();
    }

    public AvailableFields getAvailableFields() {
        return SUPPORTED_FIELDS;
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

    public Collection<Feature> getSupportedFeatures() {
        return Arrays.asList(Feature.LOAD_TASK, Feature.SAVE_TASK);
    }

    public boolean isSupported(Feature feature) {
        return getSupportedFeatures().contains(feature);
    }
}

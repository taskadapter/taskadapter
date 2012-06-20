package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.AvailableFieldsBuilder;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.Descriptors;
import com.taskadapter.model.GTaskDescriptor.FIELD;

public class GithubDescriptor {

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

    public static final Descriptor instance = Descriptors
            .createPluginDescriptor(ID, LABEL, DESCRIPTION, SUPPORTED_FIELDS);

}

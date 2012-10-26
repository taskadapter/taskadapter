package com.taskadapter.web.uiapi;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.NewMappings;
import com.taskadapter.model.GTaskDescriptor;

public class NewMappingBuilder {
    static NewMappings createNewMappings(AvailableFields m1, AvailableFields m2) {
        final NewMappings res = new NewMappings();
        for (GTaskDescriptor.FIELD field : GTaskDescriptor.FIELD.values()) {
            if (field == GTaskDescriptor.FIELD.ID || field == GTaskDescriptor.FIELD.REMOTE_ID) {
                continue;
            }

            if (!m1.isFieldSupported(field) || !m2.isFieldSupported(field)) {
                continue;
            }

            /* Don't create mappings here. New mappings will be generated in the fix-up phase.
             */
            final boolean selectByDefault = m1.isSelectedByDefault(field)
                    && m2.isSelectedByDefault(field);

            res.put(new FieldMapping(field, m1.getDefaultValue(field), m2
                    .getDefaultValue(field), selectByDefault));
        }

        if (m2.isFieldSupported(GTaskDescriptor.FIELD.REMOTE_ID)) {
            res.put(new FieldMapping(GTaskDescriptor.FIELD.REMOTE_ID, null, 
                    m2.getDefaultValue(GTaskDescriptor.FIELD.REMOTE_ID), 
                    m2.isSelectedByDefault(GTaskDescriptor.FIELD.REMOTE_ID)));
        }

        if (m1.isFieldSupported(GTaskDescriptor.FIELD.REMOTE_ID)) {
            res.put(new FieldMapping(GTaskDescriptor.FIELD.REMOTE_ID, 
                    m1.getDefaultValue(GTaskDescriptor.FIELD.REMOTE_ID), null, 
                    m1.isSelectedByDefault(GTaskDescriptor.FIELD.REMOTE_ID)));
        }

        return res;

    }
}

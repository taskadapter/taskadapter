package com.taskadapter.web.uiapi;

import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.NewMappings;
import com.taskadapter.model.GTaskDescriptor;

public class NewMappingBuilder {
    static NewMappings createNewMappings(Mappings m1, Mappings m2) {
        final NewMappings res = new NewMappings();
        for (GTaskDescriptor.FIELD field : GTaskDescriptor.FIELD.values()) {
            if (field == GTaskDescriptor.FIELD.ID || field == GTaskDescriptor.FIELD.REMOTE_ID) {
                continue;
            }

            if (!m1.haveMappingFor(field) || !m2.haveMappingFor(field)) {
                continue;
            }

            /* Don't create mappings here. New mappings will be generated in the fix-up phase.
             */
            if (!m1.isFieldSelected(field) && !m2.isFieldSelected(field)) {
                continue;
            }

//            if (m1.get(fieldName).isJsonNull()
//                    || map2.get(fieldName).isJsonNull()) {
//                continue;
//            }

            res.put(new FieldMapping(field, m1.getMappedTo(field), m2.getMappedTo(field), true));
        }

        if (m2.haveMappingFor(GTaskDescriptor.FIELD.REMOTE_ID)
                && m2.isFieldSelected(GTaskDescriptor.FIELD.REMOTE_ID)
//                && !map2.get(GTaskDescriptor.FIELD.REMOTE_ID.name()).isJsonNull()
                ) {
            res.put(new FieldMapping(GTaskDescriptor.FIELD.REMOTE_ID, null, m2.getMappedTo(GTaskDescriptor.FIELD.REMOTE_ID), true));
        }

        if (m1.haveMappingFor(GTaskDescriptor.FIELD.REMOTE_ID)
                && m1.isFieldSelected(GTaskDescriptor.FIELD.REMOTE_ID)
//                && !map1.get(GTaskDescriptor.FIELD.REMOTE_ID.name()).isJsonNull()
                ) {
            res.put(new FieldMapping(GTaskDescriptor.FIELD.REMOTE_ID, m1.getMappedTo(GTaskDescriptor.FIELD.REMOTE_ID), null, true));
        }

        return res;

    }
}

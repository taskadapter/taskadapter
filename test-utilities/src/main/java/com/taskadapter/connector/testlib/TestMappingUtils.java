package com.taskadapter.connector.testlib;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.Mappings;

public final class TestMappingUtils {
    public static Mappings fromFields(AvailableFields fields) {
        final Mappings res = new Mappings();
        for (String field : fields.getSupportedFields()) {
            res.setMapping(field, fields.isSelectedByDefault(field),
                    fields.getDefaultValue(field), null);
        }
        return res;
    }
}

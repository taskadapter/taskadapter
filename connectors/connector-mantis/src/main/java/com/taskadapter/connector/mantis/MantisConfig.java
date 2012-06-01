package com.taskadapter.connector.mantis;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.WebConfig;
import com.taskadapter.model.GTaskDescriptor;

import java.util.HashMap;

public class MantisConfig extends WebConfig {

    static final String DEFAULT_LABEL = "Mantis";

    private static final long serialVersionUID = 1L;

    public MantisConfig() {
        super(DEFAULT_LABEL);
    }

    @Override
    protected Mappings generateDefaultFieldsMapping() {
    	final Mappings result = new Mappings();
        result.addField(GTaskDescriptor.FIELD.SUMMARY);
        result.addField(GTaskDescriptor.FIELD.DESCRIPTION);
        result.addField(GTaskDescriptor.FIELD.ASSIGNEE);
        result.addField(GTaskDescriptor.FIELD.DUE_DATE);
        return result;

    }

    @Override
    protected Priorities generateDefaultPriorities() {
        return new Priorities(new HashMap<String, Integer>() {
            private static final long serialVersionUID = 516389048716909610L;

            {
                put("low", 100);
                put("normal", 500);
                put("high", 700);
                put("urgent", 800);
                put("immediate", 1000);
            }
        });
    }
}

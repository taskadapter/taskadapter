package com.taskadapter.connector.mantis;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.Mapping;
import com.taskadapter.connector.definition.WebConfig;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GTaskDescriptor.FIELD;

import java.util.HashMap;
import java.util.Map;

public class MantisConfig extends WebConfig {

	static final String DEFAULT_LABEL = "Mantis";
	
	private static final long serialVersionUID = 1L;

	public MantisConfig() {
        super(DEFAULT_LABEL);
	}

    @Override
   	protected Map<FIELD, Mapping> generateDefaultFieldsMapping() {
   		Map<FIELD, Mapping> fieldsMapping = new HashMap<FIELD, Mapping>();
   		fieldsMapping.put(GTaskDescriptor.FIELD.SUMMARY, new Mapping());
   		fieldsMapping.put(GTaskDescriptor.FIELD.DESCRIPTION, new Mapping());
   		fieldsMapping.put(GTaskDescriptor.FIELD.ASSIGNEE, new Mapping());
   		fieldsMapping.put(GTaskDescriptor.FIELD.DUE_DATE, new Mapping());
   		return fieldsMapping;

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

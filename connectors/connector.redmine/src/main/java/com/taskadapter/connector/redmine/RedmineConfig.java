package com.taskadapter.connector.redmine;

import com.google.common.base.Objects;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.Mapping;
import com.taskadapter.connector.definition.WebConfig;
import com.taskadapter.model.GTaskDescriptor;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class RedmineConfig extends WebConfig {

    static final String DEFAULT_LABEL = "Redmine";

	private static final long serialVersionUID = 1L;

	// TODO hardcoded "Bug" value
	/**
	 * Samples: "Bug", "Task", "Feature", "Support"
	 */
	private String defaultTaskType = "Bug";
	
	public RedmineConfig() {
		super(DEFAULT_LABEL);
	}

	public String getDefaultTaskType() {
		return defaultTaskType;
	}

	public void setDefaultTaskType(String defaultTaskType) {
		this.defaultTaskType = defaultTaskType;
	}

	// TODO move to Descriptor?
	@Override
	public Map<GTaskDescriptor.FIELD, Mapping> generateDefaultFieldsMapping() {
		Map<GTaskDescriptor.FIELD, Mapping> fieldsMapping = new TreeMap<GTaskDescriptor.FIELD, Mapping>();
		fieldsMapping.put(GTaskDescriptor.FIELD.SUMMARY, new Mapping());
		fieldsMapping.put(GTaskDescriptor.FIELD.TASK_TYPE, new Mapping());
		fieldsMapping.put(GTaskDescriptor.FIELD.ESTIMATED_TIME, new Mapping());
		fieldsMapping.put(GTaskDescriptor.FIELD.DONE_RATIO, new Mapping());
		fieldsMapping.put(GTaskDescriptor.FIELD.ASSIGNEE, new Mapping());
		fieldsMapping.put(GTaskDescriptor.FIELD.DESCRIPTION, new Mapping());
		fieldsMapping.put(GTaskDescriptor.FIELD.START_DATE, new Mapping());
		fieldsMapping.put(GTaskDescriptor.FIELD.DUE_DATE, new Mapping());
		return fieldsMapping;
	}

	@Override
	protected Priorities generateDefaultPriorities() {
		return new Priorities(new HashMap<String, Integer>() {
			private static final long serialVersionUID = 516389048716909610L;
			{
				put("Low", 100);
				put("Normal", 500);
				put("High", 700);
				put("Urgent", 800);
				put("Immediate", 1000);
			}
		});
	}


	@Override
	public int hashCode() {
        return  31 * super.hashCode() +
                Objects.hashCode(defaultTaskType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
        if(obj instanceof RedmineConfig){
            RedmineConfig other = (RedmineConfig) obj;
           return Objects.equal(defaultTaskType, other.defaultTaskType);
        }  else {
            return false;
        }
	}
}

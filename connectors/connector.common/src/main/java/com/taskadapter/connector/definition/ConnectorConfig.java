package com.taskadapter.connector.definition;

import com.google.common.base.Objects;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GTaskDescriptor.FIELD;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Alexey Skorokhodov
 */
public abstract class ConnectorConfig implements Serializable {
	
	private static final long serialVersionUID = 1L;

    private static final int DEFAULT_PRIORITY_VALUE = 500;

	protected Map<FIELD, Mapping> fieldsMapping;
	
	protected String label;

    protected boolean saveIssueRelations = false;

	/**
	 * Samples: "Bug", "Task", "Feature", "Support"
	 */
	protected String defaultTaskType;

    /**
  	 * priority text -> priority number mappings.
  	 * <P>e.g. "low"->100
  	 */
  	protected Map<String, Integer> prioritiesMapping;

    public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Label describing where the data is located.
	 * <p>sample values: 
	 * for Redmine connector: "http://www.redmine.org:1234", 
	 * for MSProject: "c:\folder1\file.txt"
	 */
    public abstract String getSourceLocation();

    public abstract String getTargetLocation();

	public ConnectorConfig() {
		fieldsMapping = generateDefaultFieldsMapping();
        prioritiesMapping = generateDefaultPrioritiesMapping();
	}

    public boolean getSaveIssueRelations() {
        return saveIssueRelations;
    }

    public void setSaveIssueRelations(boolean saveIssueRelations) {
        this.saveIssueRelations = saveIssueRelations;
    }

    // TODO replace the MAP with a concrete class holding the mapping
	public Map<FIELD, Mapping> getFieldsMapping() {
		return fieldsMapping;
	}

	public void setFieldsMapping(Map<FIELD, Mapping> fieldsMapping) {
		this.fieldsMapping = fieldsMapping;
	}

	abstract protected Map<GTaskDescriptor.FIELD, Mapping> generateDefaultFieldsMapping();
    abstract protected Map<String, Integer> generateDefaultPrioritiesMapping();
	
	public boolean isFieldSelected(FIELD field) {
		Mapping mapping = fieldsMapping.get(field);
		return (mapping != null && mapping.isSelected());
	}
	
	/**
	 * returns the current value or NULL if the mapping does not exist / unknown 
	 */
	public String getFieldMappedValue(FIELD field) {
		Mapping mapping = fieldsMapping.get(field);
		if (mapping != null) {
			return mapping.getCurrentValue();
		}
		return null;
	}
	
	public Mapping getFieldMapping(FIELD field) {
		return fieldsMapping.get(field);
	}

	public void setFieldMapping(FIELD field, Mapping mapping) {
		fieldsMapping.put(field, mapping);
	}

	public String getDefaultTaskType() {
		return defaultTaskType;
	}

	public void setDefaultTaskType(String defaultTaskType) {
		this.defaultTaskType = defaultTaskType;
	}

    public Map<String, Integer> getPrioritiesMapping() {
        return prioritiesMapping;
    }

    public void setPrioritiesMapping(Map<String, Integer> prioritiesMapping) {
        this.prioritiesMapping = prioritiesMapping;
    }

    //get priority value for MSP based on the Tracker priority field
    public Integer getPriorityByText(String priorityText) {
        Integer priorityNumber = prioritiesMapping.get(priorityText);
        if (priorityNumber == null) {
            priorityNumber = DEFAULT_PRIORITY_VALUE;
            System.out.println("unknown priority text: " + priorityText);
        }
        return priorityNumber;
    }

    //get the nearest priority value for Tracker based on the MSP integer value (priority field)
    public String getPriorityByMSP(Integer mspValue) {
        Integer minIntValue = 9999;
        String minStringValue = "";

        if (mspValue == null) {
            mspValue = DEFAULT_PRIORITY_VALUE;
        }

        //for ex.
        //Low: 100 : 0 - 100
        //Trivial: 200 : 100-200
        //Normal: 400 : 200-400
        //High: 700 : 400-700
        //Critical: 900 : 700-900
        //Blocker: 1000 : 900-1000

        for (Map.Entry<String, Integer> entry : prioritiesMapping.entrySet()) {
            if (entry.getValue() >= mspValue && entry.getValue() < minIntValue) {
                //check if mspValue in current interval and it's value smaller than minIntValue we should remember this key
                minIntValue = entry.getValue();
                minStringValue = entry.getKey();
            }
        }

        return minStringValue;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fieldsMapping, defaultTaskType, label);
    }

	@Override
	public boolean equals(Object obj) {
        if(obj instanceof ConnectorConfig) {
            ConnectorConfig other = (ConnectorConfig) obj;
            return Objects.equal(fieldsMapping, other.fieldsMapping) &&
                    Objects.equal(defaultTaskType, other.defaultTaskType) &&
                    Objects.equal(label, other.label);
        }   else {
            return false;
        }
	}

    /**
   	 * The default implementation does nothing.
   	 */
   	public void validateForSave() throws ValidationException {
   		// nothing
   	}

   	/**
   	 * The default implementation does nothing.
   	 */
   	public void validateForLoad() throws ValidationException {
   		// nothing
   	}
}

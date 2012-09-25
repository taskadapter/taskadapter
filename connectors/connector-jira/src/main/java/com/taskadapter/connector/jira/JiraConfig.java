package com.taskadapter.connector.jira;

import com.google.common.base.Objects;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebConfig;
import com.taskadapter.model.GTaskDescriptor;

import java.util.HashMap;

public class JiraConfig extends WebConfig {

    static final String DEFAULT_LABEL = "Atlassian Jira";

    private static final long serialVersionUID = 1L;

    // TODO this can probably be moved to the super class
    private String component = "";

    /**
     * Version ("milestone") in the project.
     */
    private String affectedVersion = "";

    /**
     * Version ("milestone") in the project.
     */
    private String fixForVersion = "";

    private String queryString;

    public JiraConfig() {
        super(DEFAULT_LABEL);
        setDefaultTaskType(TASK_TYPE_BUG);
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getFixForVersion() {
        return fixForVersion;
    }

    public void setFixForVersion(String fixForVersion) {
        this.fixForVersion = fixForVersion;
    }

    public String getAffectedVersion() {
        return affectedVersion;
    }

    public void setAffectedVersion(String version) {
        this.affectedVersion = version;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    @Override
    protected Mappings generateDefaultFieldsMapping() {
    	final Mappings result = new Mappings();
        result.addField(GTaskDescriptor.FIELD.SUMMARY);
        result.addField(GTaskDescriptor.FIELD.TASK_TYPE);
        result.addField(GTaskDescriptor.FIELD.ESTIMATED_TIME);
        result.addField(GTaskDescriptor.FIELD.ASSIGNEE);
        result.addField(GTaskDescriptor.FIELD.DESCRIPTION);
        result.addField(GTaskDescriptor.FIELD.DUE_DATE);
        result.addField(GTaskDescriptor.FIELD.PRIORITY);
        return result;
    }

    @Override
    public void validateForLoad() throws ValidationException {
        super.validateForLoad();

        if (getQueryId() == null) {
            throw new ValidationException("The current Task Adapter version supports loading data from Jira\n" +
                    "only using saved \"Query ID\".\n" +
                    "Please specify it in the Jira configuration dialog");
        }
    }

    @Override
    public void validateForSave() throws ValidationException {
        super.validateForSave();

        if (getProjectKey().isEmpty()) {
            throw new ValidationException("Please specify the Jira project name\n" +
                    "where you want your tasks to be created.");
        }
    }

    @Override
    protected Priorities generateDefaultPriorities() {
        return createDefaultPriorities();
    }

    /**
     * Creates a default priorities.
     * @return default priorities.
     */
	public static Priorities createDefaultPriorities() {
		return new Priorities(new HashMap<String, Integer>() {
            private static final long serialVersionUID = 516389048716909610L;

            {
                put("Trivial", 100);
                put("Minor", 300);
                put("Major", 700);
                put("Critical", 800);
                put("Blocker", 1000);
            }
        });
	}


    @Override
    public int hashCode() {
        return 31 * super.hashCode() +
                Objects.hashCode(affectedVersion, component, getDefaultTaskType(), fixForVersion);

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (obj instanceof JiraConfig) {
            JiraConfig other = (JiraConfig) obj;
            return Objects.equal(affectedVersion, other.affectedVersion) &&
                    Objects.equal(component, other.component) &&
                    Objects.equal(getDefaultTaskType(), other.component) &&
                    Objects.equal(fixForVersion, other.fixForVersion);

        } else {
            return false;
        }
    }
}

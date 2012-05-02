package com.taskadapter.connector.jira;

import com.google.common.base.Objects;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.Mapping;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebConfig;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GTaskDescriptor.FIELD;

import java.util.HashMap;
import java.util.Map;

public class JiraConfig extends WebConfig {

    static final String DEFAULT_LABEL = "Atlassian Jira";

    private static final long serialVersionUID = 1L;

    private String defaultTaskType = "Bug";

    // TODO this can probably be moved to the super class
    protected String component = "";

    /**
     * Version ("milestone") in the project.
     */
    protected String affectedVersion = "";

    /**
     * Version ("milestone") in the project.
     */
    protected String fixForVersion = "";

    public JiraConfig() {
        super(DEFAULT_LABEL);
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


    public String getDefaultTaskType() {
        return defaultTaskType;
    }

    public void setDefaultTaskType(String defaultTaskType) {
        this.defaultTaskType = defaultTaskType;
    }

    @Override
    protected Map<FIELD, Mapping> generateDefaultFieldsMapping() {
        Map<FIELD, Mapping> fieldsMapping = new HashMap<FIELD, Mapping>();
        fieldsMapping.put(GTaskDescriptor.FIELD.SUMMARY, new Mapping());
        fieldsMapping.put(GTaskDescriptor.FIELD.TASK_TYPE, new Mapping());
        fieldsMapping.put(GTaskDescriptor.FIELD.ESTIMATED_TIME, new Mapping());
        fieldsMapping.put(GTaskDescriptor.FIELD.ASSIGNEE, new Mapping());
        fieldsMapping.put(GTaskDescriptor.FIELD.DESCRIPTION, new Mapping());
        fieldsMapping.put(GTaskDescriptor.FIELD.DUE_DATE, new Mapping());
        fieldsMapping.put(GTaskDescriptor.FIELD.PRIORITY, new Mapping());
        return fieldsMapping;
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
                Objects.hashCode(affectedVersion, component, defaultTaskType, fixForVersion);

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
                    Objects.equal(defaultTaskType, other.component) &&
                    Objects.equal(fixForVersion, other.fixForVersion);

        } else {
            return false;
        }
    }

    /*@Override
     protected void populatePrioritiesFromTracker(WebServerInfo info) throws MalformedURLException, RemoteException {
         // TODO Auto-generated method stub
         JiraConnection conn = JiraConnectionFactory.createConnection(info);
         RemotePriority[] priorities = conn.getPriorities();

         for (int i = 0; i < priorities.length; i++) {
             prioritiesMapping.put(priorities[i].getName(), 0);
         }
     }*/

/*	public static void main(String[] args) throws MalformedURLException, RemoteException {
		WebServerInfo info = new WebServerInfo("http://ta-dev.dyndns.biz:8080", "admin", "zzz666");
		JiraConnection conn = JiraConnectionFactory.createConnection(info);
//		RemoteProject = conn.getProject("ZMDPRJ");
		
		Map<String, String> priorityList = new HashMap<String, String>();
		RemotePriority[] priorities = conn.getPriorities();
		
		for (int i = 0; i < priorities.length; i++) {
			priorityList.put(priorities[i].getId(), priorities[i].getName());
		}
		
		Map<TAConfig, IPath> items = ConfigStorage.getAllConfigs();
		//TAConfig config = ConfigStorage.loadConfig(fileInput.getPath()); 
		
		RemoteIssue iss = conn.getIssueByKey("ZMDPRJ-1");
		priorityList.get(iss.getPriority());
		
		System.out.println(iss.getPriority() + ":" + priorityList.get(iss.getPriority()));
	}*/

/*		JiraConfig jira = new JiraConfig();
		
		Map prioritiesMapping = new HashMap<String, Integer>();
		prioritiesMapping.put("Low", 100);
		prioritiesMapping.put("Trivial", 200);
		prioritiesMapping.put("Normal", 400);
		prioritiesMapping.put("High", 700);
		prioritiesMapping.put("Critical", 900);		
		prioritiesMapping.put("Blocker", 1000);		
		
		jira.setPrioritiesMapping(prioritiesMapping);
		
		
		System.out.println(jira.getPriorityByTracker("High"));
		System.out.println(jira.getPriorityByMSP(101));
	}*/

}

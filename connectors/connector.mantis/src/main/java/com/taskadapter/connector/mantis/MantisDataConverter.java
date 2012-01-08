package com.taskadapter.connector.mantis;

import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GUser;
import org.mantis.ta.beans.*;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MantisDataConverter {

	private static final String DEFAULT_TASK_DESCRIPTION = "-";

	private static HashMap<String, Integer> priorityNumbers = new HashMap<String, Integer>() {
        private static final long serialVersionUID = 516389048716909610L;

        {
            // TODO this can be moved to properties section to be defined by
            // user.
            put("none", 100);
            put("low", 100);
            put("normal", 500);
            put("high", 700);
            put("urgent", 800);
            put("immediate", 800);
        }
    };

    private final MantisConfig config;
	private List<AccountData> users;

    public MantisDataConverter(MantisConfig config) {
		this.config = config;
    }
    
    public static GUser convertToGUser(AccountData mantisUser) {
        GUser user = new GUser();
        user.setId(mantisUser.getId().intValue());
        //user.setId(new Integer(mantisUser.getId().intValue()));
        user.setLoginName(mantisUser.getName());
        return user;
    }

    public IssueData convertToMantisIssue(ProjectData mntProject, GTask task) {
        IssueData issue = new IssueData();

        ObjectRef mntProjectRef = new ObjectRef(mntProject.getId(), mntProject.getName());
        issue.setProject(mntProjectRef);

        if (config.isFieldSelected(GTaskDescriptor.FIELD.SUMMARY)) {
            issue.setSummary(task.getSummary());
        }

        if (config.isFieldSelected(GTaskDescriptor.FIELD.DESCRIPTION)) {
        	String description = task.getDescription();
        	// empty description is not allowed by Mantis API.
        	// see bug https://www.hostedredmine.com/issues/39248
        	if (description.isEmpty()) {
        		description = DEFAULT_TASK_DESCRIPTION;
        	}
            issue.setDescription(description);
        }

        if (config.isFieldSelected(GTaskDescriptor.FIELD.DUE_DATE)) {
            if (task.getDueDate() != null) {
                Calendar dueDate = Calendar.getInstance();
                dueDate.setTime(task.getDueDate());
                issue.setDue_date(dueDate);
            }
        }

/*		Calendar created = Calendar.getInstance();
		created.setTime(task.getCreatedOn());
		issue.setDate_submitted(created);

		Calendar updated = Calendar.getInstance();
		updated.setTime(task.getUpdatedOn());
		issue.setLast_updated(updated);*/

        if (config.isFieldSelected(GTaskDescriptor.FIELD.ASSIGNEE)) {
            GUser ass = task.getAssignee();

			if (ass != null) {
				AccountData mntUser;
				if (ass.getId() != null) {
					mntUser = new AccountData();
					mntUser.setId(BigInteger.valueOf(ass.getId()));
					mntUser.setName(ass.getLoginName());
				} else {
					mntUser = findUser(ass);
				}
				issue.setHandler(mntUser);
			}
        }

        return issue;
    }

    public static GTask convertToGenericTask(IssueData issue) {
        GTask task = new GTask();

        task.setId(issue.getId().intValue());
        task.setKey(String.valueOf(issue.getId()));
        // task.setParentId(parentId);

        AccountData mntUser = issue.getHandler();
        if (mntUser != null) {
            GUser ass = new GUser(mntUser.getId().intValue(), mntUser.getName());
            task.setAssignee(ass);
        }

        // task.setType(type);
        task.setSummary(issue.getSummary());
        // task.setEstimatedHours(estimatedHours); only in string values for ex.
        // (< 1 day, 2-3 days, < 1week, < 1 month)
        // task.setDoneRatio(doneRatio);
        // task.setStartDate(startDate);
        // task.setDueDate(dueDate);
        task.setCreatedOn(issue.getDate_submitted().getTime());
        task.setUpdatedOn(issue.getLast_updated().getTime());

        Integer priorityValue = priorityNumbers.get(issue.getPriority().getName());
        task.setPriority(priorityValue);

        task.setDescription(issue.getDescription());
        if (issue.getDue_date() != null) {
            task.setDueDate(issue.getDue_date().getTime());
        }

        processRelations(issue, task);

        return task;
    }

    private static void processRelations(IssueData mntIssue, GTask genericTask) {
        RelationshipData[] relations = mntIssue.getRelationships();
        if (relations != null) {
            for (RelationshipData relation : relations) {
                if (relation.getType().getName().equals("child of")) {
                    GRelation r = new GRelation(String.valueOf(relation.getId()),
                            String.valueOf(relation.getTarget_id()),
                            GRelation.TYPE.precedes);
                    genericTask.getRelations().add(r);
                } else {
                    System.out.println("relation type is not supported: "
                            + relation.getType());
                }
            }
        }
    }

	public void setUsers(List<AccountData> users) {
		this.users = users;
	}
	
	/**
	 * @return NULL if the user is not found or if "users" weren't previously set via setUsers() method
	 */
	private AccountData findUser(GUser ass) {
		if (users == null) {
			return null;
		}
		AccountData foundUser = null;
		for (AccountData user : users) { 
			if (ass.getLoginName().equalsIgnoreCase(user.getName())
					|| ass.getLoginName().equalsIgnoreCase(
							user.getReal_name())) {
				foundUser = user;
				break;
			}
		}
		return foundUser;
	}

}

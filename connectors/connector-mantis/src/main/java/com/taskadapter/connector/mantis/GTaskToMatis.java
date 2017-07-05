package com.taskadapter.connector.mantis;

import biz.futureware.mantis.rpc.soap.client.AccountData;
import biz.futureware.mantis.rpc.soap.client.IssueData;
import biz.futureware.mantis.rpc.soap.client.ObjectRef;
import biz.futureware.mantis.rpc.soap.client.ProjectData;
import com.taskadapter.connector.common.data.ConnectorConverter;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GUser;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class GTaskToMatis implements ConnectorConverter<GTask, IssueData> {
    
    private static final String DEFAULT_TASK_DESCRIPTION = "-";

    /** see https://bitbucket.org/taskadapter/taskadapter/issues/25/once-created-tasks-cannot-be-updated-in
     * "Update task" fails unless you set some "category" on it. weirdly, "create tasks" works fine.
     * whatever, I will just set this "General" category that exists on a default MantisBT server.
     */
    private static final String DEFAULT_TASK_CATEGORY = "General";

    private final ProjectData mntProject;
    private final Collection<GTaskDescriptor.FIELD> fieldsToExport;
    private final List<AccountData> users;

    public GTaskToMatis(ProjectData mntProject, Collection<GTaskDescriptor.FIELD> fieldsToExport, List<AccountData> users) {
        this.mntProject = mntProject;
        this.fieldsToExport = fieldsToExport;
        this.users = users;
    }

    @Override
    public IssueData convert(GTask task) throws ConnectorException {
        IssueData issue = new IssueData();
        final String key = task.getKey();
        if (key != null) {
            final long numericKey = Long.parseLong(key);
            issue.setId(BigInteger.valueOf(numericKey));
        }

        // see Javadoc for DEFAULT_TASK_CATEGORY why need to set this.
        issue.setCategory(DEFAULT_TASK_CATEGORY);

        ObjectRef mntProjectRef = new ObjectRef(mntProject.getId(), mntProject.getName());
        issue.setProject(mntProjectRef);

        if (fieldsToExport.contains(GTaskDescriptor.FIELD.SUMMARY)) {
            issue.setSummary(task.getSummary());
        }

        if (fieldsToExport.contains(GTaskDescriptor.FIELD.DESCRIPTION)) {
            String description = task.getDescription();
            // empty description is not allowed by Mantis API.
            // see bug https://www.hostedredmine.com/issues/39248
            if (description.isEmpty()) {
                description = DEFAULT_TASK_DESCRIPTION;
            }
            issue.setDescription(description);
        }

        if (fieldsToExport.contains(GTaskDescriptor.FIELD.DUE_DATE)) {
            if (task.getDueDate() != null) {
                Calendar dueDate = Calendar.getInstance();
                dueDate.setTime(task.getDueDate());
                issue.setDue_date(dueDate);
            }
        }

/*      Calendar created = Calendar.getInstance();
        created.setTime(task.getCreatedOn());
        issue.setDate_submitted(created);

        Calendar updated = Calendar.getInstance();
        updated.setTime(task.getUpdatedOn());
        issue.setLast_updated(updated);*/

        if (fieldsToExport.contains(GTaskDescriptor.FIELD.ASSIGNEE)) {
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
    
    private AccountData findUser(GUser ass) {
        if (users == null) {
            return null;
        }
        
        // getting best name to search
        String nameToSearch = ass.getLoginName();
        if (nameToSearch == null || "".equals(nameToSearch)) {
            nameToSearch = ass.getDisplayName();
        }
        if (nameToSearch == null || "".equals(nameToSearch)) {
            return null;
        }

        for (AccountData user : users) {
            if (nameToSearch.equalsIgnoreCase(user.getName())
                    || nameToSearch.equalsIgnoreCase(
                    user.getReal_name())) {
                return user;
            }
        }
        return null;
    }


}

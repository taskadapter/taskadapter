package com.taskadapter.connector.mantis;

import biz.futureware.mantis.rpc.soap.client.AccountData;
import biz.futureware.mantis.rpc.soap.client.IssueData;
import biz.futureware.mantis.rpc.soap.client.ObjectRef;
import biz.futureware.mantis.rpc.soap.client.ProjectData;
import com.taskadapter.connector.common.data.ConnectorConverter;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GUser;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;

public class GTaskToMatisConverter implements
        ConnectorConverter<GTask, IssueData> {
    
    private static final String DEFAULT_TASK_DESCRIPTION = "-";
    
    private final ProjectData mntProject;
    private final Mappings mappings;
    private final List<AccountData> users;

    public GTaskToMatisConverter(ProjectData mntProject, Mappings mappings,
            List<AccountData> users) {
        this.mntProject = mntProject;
        this.mappings = mappings;
        this.users = users;
    }

    @Override
    public IssueData convert(GTask task) throws ConnectorException {
        IssueData issue = new IssueData();

        ObjectRef mntProjectRef = new ObjectRef(mntProject.getId(), mntProject.getName());
        issue.setProject(mntProjectRef);

        if (mappings.isFieldSelected(GTaskDescriptor.FIELD.SUMMARY)) {
            issue.setSummary(task.getSummary());
        }

        if (mappings.isFieldSelected(GTaskDescriptor.FIELD.DESCRIPTION)) {
            String description = task.getDescription();
            // empty description is not allowed by Mantis API.
            // see bug https://www.hostedredmine.com/issues/39248
            if (description.isEmpty()) {
                description = DEFAULT_TASK_DESCRIPTION;
            }
            issue.setDescription(description);
        }

        if (mappings.isFieldSelected(GTaskDescriptor.FIELD.DUE_DATE)) {
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

        if (mappings.isFieldSelected(GTaskDescriptor.FIELD.ASSIGNEE)) {
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

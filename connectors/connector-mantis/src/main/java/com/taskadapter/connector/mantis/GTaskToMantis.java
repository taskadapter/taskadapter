package com.taskadapter.connector.mantis;

import biz.futureware.mantis.rpc.soap.client.AccountData;
import biz.futureware.mantis.rpc.soap.client.IssueData;
import biz.futureware.mantis.rpc.soap.client.ObjectRef;
import biz.futureware.mantis.rpc.soap.client.ProjectData;
import com.google.common.base.Strings;
import com.taskadapter.connector.common.data.ConnectorConverter;
import com.taskadapter.connector.definition.exception.FieldConversionException;
import com.taskadapter.model.AssigneeFullName;
import com.taskadapter.model.AssigneeLoginName;
import com.taskadapter.model.Description;
import com.taskadapter.model.DueDate;
import com.taskadapter.model.Field;
import com.taskadapter.model.GTask;
import com.taskadapter.model.Summary;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class GTaskToMantis implements ConnectorConverter<GTask, IssueData> {
    private static final String DEFAULT_TASK_DESCRIPTION = "-";

    /**
     * see https://bitbucket.org/taskadapter/taskadapter/issues/25/once-created-tasks-cannot-be-updated-in
     * "Update task" fails unless you set some "category" on it. weirdly, "create tasks" works fine.
     * whatever, I will just set this "General" category that exists on a default MantisBT server.
     */
    public static final String DEFAULT_TASK_CATEGORY = "General";

    private ProjectData mntProject;
    private List<AccountData> users;

    public GTaskToMantis(ProjectData mntProject, List<AccountData> users) {
        this.mntProject = mntProject;
        this.users = users;
    }

    public IssueData convert(GTask task) throws FieldConversionException {
        var issue = new IssueData();
        var id = task.getId();
        if (id != null) {
            issue.setId(BigInteger.valueOf(id));
        }

        for (Map.Entry<Field<?>, Object> row : task.getFields().entrySet()) {
            try {
                processField(issue, row.getKey(), row.getValue());
            } catch (Exception e) {
                throw new FieldConversionException(MantisConnector.ID, row.getKey(), row.getValue(), e.getMessage());
            }
        }

        // see Javadoc for DEFAULT_TASK_CATEGORY why need to set this.
        issue.setCategory(GTaskToMantis.DEFAULT_TASK_CATEGORY);
        var mntProjectRef = new ObjectRef(mntProject.getId(), mntProject.getName());
        issue.setProject(mntProjectRef);
        return issue;
    }

    private void processField(IssueData issue, Field<?> field, Object value) {
        if (field instanceof Summary) {
            issue.setSummary((String) value);
            return;

        }
        if (field instanceof Description) {
            // empty description is not allowed by Mantis API.
            // see bug https://www.hostedredmine.com/issues/39248
            if (Strings.isNullOrEmpty((String) value)) {
                issue.setDescription(DEFAULT_TASK_DESCRIPTION);
            } else {
                issue.setDescription((String) value);
            }
            return;
        }
        if (field instanceof DueDate) {
            if (value != null) {
                var calendar = Calendar.getInstance();
                calendar.setTime((Date) value);
                issue.setDue_date(calendar);
                return;
            }
        }

        if (field instanceof AssigneeFullName) {
            var fullName = (String) value;
            issue.setHandler(users.stream()
                    .filter(user -> user.getName().equals(fullName))
                    .findFirst().orElse(null));
            return;
        }
        if (field instanceof AssigneeLoginName) {
            var login = (String) value;
            issue.setHandler(users.stream()
                    .filter(user -> user.getName().equals(login))
                    .findFirst()
                    .orElse(null));
        }
        // ignore the rest of the fields
    }
}

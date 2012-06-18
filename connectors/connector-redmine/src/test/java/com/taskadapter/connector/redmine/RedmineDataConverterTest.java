package com.taskadapter.connector.redmine;

import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.model.GUser;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNull;

public class RedmineDataConverterTest {

    @Test
    public void unmappedSummaryIsIgnored() {
        GTask gtask = new GTask();
        gtask.setSummary("Should be ignored");
        RedmineConfig config = new RedmineConfig();
        config.getFieldMappings().deselectField(FIELD.SUMMARY);
        Issue task = new RedmineDataConverter(config).convertToRedmineIssue(new Project(), gtask);
        Assert.assertNull(task.getSubject());
    }

    @Test
    public void gUserWithRedmineLoginOnLoginName() {
        GTask gtask = createDummyTaskForUser("diogo.nascimento");
        Issue task = getConverterWithAssigneeMapped().convertToRedmineIssue(new Project(), gtask);
        Assert.assertNotNull(task.getAssignee());
        Assert.assertEquals("diogo.nascimento", task.getAssignee().getLogin());
    }

    @Test
    public void gUserWithRedmineFullNameOnLoginName() {
        GTask gtask = createDummyTaskForUser("Felipe Castro");
        Issue task = getConverterWithAssigneeMapped().convertToRedmineIssue(new Project(), gtask);
        Assert.assertNotNull(task.getAssignee());
        Assert.assertEquals("felipe.castro", task.getAssignee().getLogin());
    }

    @Test
    public void gUserWithRedmineLoginOnDisplayName() {
        GTask gtask = createDummyTaskForUser("diogo.nascimento");

        Issue task = getConverterWithAssigneeMapped().convertToRedmineIssue(new Project(), gtask);

        Assert.assertNotNull(task.getAssignee());
        Assert.assertEquals("diogo.nascimento", task.getAssignee().getLogin());
    }

    @Test
    public void gUserWithRedmineFullNameOnDisplayName() {
        GTask gtask = createDummyTaskForUser("Felipe Castro");
        Issue task = getConverterWithAssigneeMapped().convertToRedmineIssue(new Project(), gtask);
        Assert.assertNotNull(task.getAssignee());
        Assert.assertEquals("felipe.castro", task.getAssignee().getLogin());
    }

    private GTask createDummyTaskForUser(String userDisplayName) {
        GTask gtask = new GTask();
        gtask.setSummary("S1");
        GUser assignee = new GUser();
        // put the resource name on displayName like in MSP Connector
        assignee.setDisplayName(userDisplayName);
        gtask.setAssignee(assignee);
        return gtask;
    }

    private RedmineDataConverter getConverterWithAssigneeMapped() {
        return getConverterWithAssignee(true);
    }

    private RedmineDataConverter getConverterWithAssigneeSkipped() {
        return getConverterWithAssignee(false);
    }

    private RedmineDataConverter getConverterWithAssignee(boolean assigneeIsMapped) {
        RedmineConfig config = new RedmineConfig();
        if (assigneeIsMapped) {
            config.getFieldMappings().selectField(FIELD.ASSIGNEE);
        } else {
            config.getFieldMappings().deselectField(FIELD.ASSIGNEE);
        }
        RedmineDataConverter converter = new RedmineDataConverter(config);
        converter.setUsers(createUsers());
        return converter;
    }

    private List<User> createUsers() {
        List<User> users = new ArrayList<User>();

        User user1 = new User();
        user1.setFirstName("Diogo");
        user1.setLastName("Nascimento");
        user1.setLogin("diogo.nascimento");
        users.add(user1);

        User user2 = new User();
        user2.setFirstName("Felipe");
        user2.setLastName("Castro");
        user2.setLogin("felipe.castro");
        users.add(user2);

        return users;
    }


    @Test
    public void nullReturnedWhenNoUsersSet() {
        RedmineDataConverter converter = createDefaultConverter();
        // should not fail with NPE or anything
        assertNull(converter.findRedmineUserInCache(new GUser("mylogin")));
    }

    private RedmineDataConverter createDefaultConverter() {
        RedmineConfig config = new RedmineConfig();
        return new RedmineDataConverter(config);
    }
}

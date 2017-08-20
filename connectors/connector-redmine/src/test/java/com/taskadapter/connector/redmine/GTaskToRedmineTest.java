package com.taskadapter.connector.redmine;

import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.redmineapi.bean.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class GTaskToRedmineTest {

    private final Project project = ProjectFactory.create();

/*    @Test
    public void summaryIsConvertedByDefault() {
        checkSummary(createDefaultConverter(), "summary 1");
    }

    @Test
    public void summaryIsConvertedWhenSelected() {
        checkSummary(createConverterWithSelectedField(FIELD.SUMMARY, null), "summary 1");
    }

    @Test
    public void summaryIsIgnoredWhenUnselected() {
        checkSummary(createConverterWithUnselectedField(FIELD.SUMMARY), null);
    }

    private void checkSummary(GTaskToRedmine converter, String expected) {
        GTask task = new GTask();
        task.setValue(RedmineField.summary(), "summary 1");
        Issue redmineIssue = converter.convertToRedmineIssue(task);
        assertEquals(expected, redmineIssue.getSubject());
    }

    @Test
    public void gUserWithRedmineLoginOnLoginName() {
        GTask gtask = createDummyTaskForUser("diogo.nascimento");
        Issue task = getConverterWithAssigneeMapped().convertToRedmineIssue(gtask);
        assertNotNull(task.getAssignee());
        assertEquals("diogo.nascimento", task.getAssignee().getLogin());
    }

    @Test
    public void gUserWithRedmineFullNameOnLoginName() {
        GTask gtask = createDummyTaskForUser("Felipe Castro");
        Issue task = getConverterWithAssigneeMapped().convertToRedmineIssue(gtask);
        assertNotNull(task.getAssignee());
        assertEquals("felipe.castro", task.getAssignee().getLogin());
    }

    @Test
    public void gUserWithRedmineLoginOnDisplayName() {
        GTask gtask = createDummyTaskForUser("diogo.nascimento");

        Issue task = getConverterWithAssigneeMapped().convertToRedmineIssue(gtask);

        assertNotNull(task.getAssignee());
        assertEquals("diogo.nascimento", task.getAssignee().getLogin());
    }

    @Test
    public void gUserWithRedmineFullNameOnDisplayName() {
        GTask gtask = createDummyTaskForUser("Felipe Castro");
        Issue task = getConverterWithAssigneeMapped().convertToRedmineIssue(gtask);
        assertNotNull(task.getAssignee());
        assertEquals("felipe.castro", task.getAssignee().getLogin());
    }

    private GTask createDummyTaskForUser(String userDisplayName) {
        GTask gtask = new GTask();
        gtask.setValue(RedmineField.summary(), "S1");
        // put the resource name on displayName like in MSP Connector
        gtask.setValue(RedmineField.assignee(), userDisplayName);
        return gtask;
    }

    private GTaskToRedmine getConverterWithAssigneeMapped() {
        return createConverterWithSelectedField(FIELD.ASSIGNEE, createUsers());
    }

    private List<User> createUsers() {
        List<User> users = new ArrayList<>();

        User user1 = UserFactory.create(1);
        user1.setFirstName("Diogo");
        user1.setLastName("Nascimento");
        user1.setLogin("diogo.nascimento");
        users.add(user1);

        User user2 = UserFactory.create(2);
        user2.setFirstName("Felipe");
        user2.setLastName("Castro");
        user2.setLogin("felipe.castro");
        users.add(user2);

        return users;
    }


    @Test
    public void nullReturnedWhenNoUsersSet() {
        GTaskToRedmine converter = createDefaultConverter();
        // should not fail with NPE or anything
        assertNull(converter.findRedmineUserInCache("mylogin"));
    }

    private GTaskToRedmine createDefaultConverter() {
        RedmineConfig config = new RedmineConfig();
        return new GTaskToRedmine(config,
                null, project, Collections.<User>emptyList(),
                Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList());
    }

    private GTaskToRedmine createConverterWithSelectedField(GTaskDescriptor.FIELD field, List<User> users) {
        return createConverterWithField(field, true, users);
    }

    private GTaskToRedmine createConverterWithUnselectedField(GTaskDescriptor.FIELD field) {
        return createConverterWithField(field, false, null);
    }

    private GTaskToRedmine createConverterWithField(GTaskDescriptor.FIELD field, boolean selected, List<User> users) {
        RedmineConfig config = new RedmineConfig();
//        Collection<FIELD> selectedFields = FieldSelector.getSelectedFields(RedmineSupportedFields.legacyFields(),
//                field, selected);
        return new GTaskToRedmine(config, null, project, users, new ArrayList<>(), new ArrayList<>(), Collections.<Version>emptyList());
    }
    */
}

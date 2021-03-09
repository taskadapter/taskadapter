package com.taskadapter.core;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.FileSetup;
import com.taskadapter.connector.definition.SaveResult;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.msp.MSPConnector;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineConnector;
import com.taskadapter.connector.testlib.ResourceLoader;
import com.taskadapter.connector.testlib.TestUtils;
import com.taskadapter.integrationtests.RedmineTestInitializer;
import com.taskadapter.integrationtests.RedmineTestLoader;
import com.taskadapter.integrationtests.TestConfigs;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.CustomString;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskBuilder;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.taskadapter.core.JavaFieldAdapter.AssigneeFullNameOpt;
import static com.taskadapter.core.JavaFieldAdapter.customStringOpt;
import static com.taskadapter.core.JavaFieldAdapter.descriptionOpt;
import static com.taskadapter.core.JavaFieldAdapter.summaryOpt;
import static org.assertj.core.api.Assertions.assertThat;

public class RedmineMspIT {
    private RedmineConfig sourceConfig = TestConfigs.getRedmineConfig();
    private RedmineConfig targetConfig = TestConfigs.getRedmineConfig();
    private RedmineConnector sourceRedmineConnector = new RedmineConnector(sourceConfig, TestConfigs.getRedmineSetup());
    private RedmineConnector targetRedmineConnector = new RedmineConnector(targetConfig, TestConfigs.getRedmineSetup());

    private RedmineConfig redmineConfigWithResolveAssignees = TestConfigs.getRedmineConfig();
    private RedmineConnector redmineConnectorWithResolveAssignees = new RedmineConnector(redmineConfigWithResolveAssignees, TestConfigs.getRedmineSetup());
    private Project redmineProject;
    private Adapter adapter = new Adapter(sourceRedmineConnector, targetRedmineConnector);

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void beforeEachTest() {
        redmineConfigWithResolveAssignees.setFindUserByName(true);
        // have to create a project for each test, otherwise stuff created during one test interferes with others
        redmineProject = RedmineTestInitializer.createProject();
        sourceConfig.setProjectKey(redmineProject.getIdentifier());
        targetConfig.setProjectKey(redmineProject.getIdentifier());
        redmineConfigWithResolveAssignees.setProjectKey(redmineProject.getIdentifier());
    }

    @After
    public void afterEachTest() throws RedmineException {
        RedmineTestInitializer.deleteProject(redmineProject.getIdentifier());
    }

    // assignee can be loaded from MSP and saved to Redmine
    @Test
    public void assigneeCanBeLoadedFromMspAndSavedToRedmine() throws ConnectorException {
        MSPConnector mspConnector = new MSPConnector(getMspSetup("2tasks-projectlibre-assignees.xml"));
        GTask redmineResult = TestUtils.loadAndSave(mspConnector, redmineConnectorWithResolveAssignees,
                Arrays.asList(new FieldRow(summaryOpt, summaryOpt, ""),
                        new FieldRow(AssigneeFullNameOpt, AssigneeFullNameOpt, null)
                )
        );
        assertThat(redmineResult.getValue(AllFields.assigneeFullName)).isEqualTo("Redmine Admin");
    }

    /**
     * subtasks are saved to MSP.
     * <p>
     * This is a regression test for a bug reported by a user: Redmine subtasks were skipped when saving
     * to MSP: https://bitbucket.org/taskadapter/taskadapter/issues/85/subtasks-are-not-saved
     * Turned out all subtasks were broken in the system for a long time! X8-[==]
     */
    @Test
    public void subtasksAreSavedToMsp() throws Exception {
        createRedmineHierarchy(targetRedmineConnector);
        MSPConnector mspConnector = getMspConnector(tempFolder.getRoot());

        List<GTask> result = TestUtils.loadAndSaveList(sourceRedmineConnector, mspConnector,
                Arrays.asList(new FieldRow(summaryOpt, summaryOpt, "")
                )
        );
        assertThat(result).hasSize(3);
    }

    // msp tasks with non-linear IDs are saved to Redmine
    @Test
    public void mspTasksWithNonLinearIDsAreSaveToRedmine() throws ConnectorException {
        MSPConnector msProjectConnector = new MSPConnector(getMspSetup("com/taskadapter/integrationtests/non-linear-uuid.xml"));
        RedmineConfig redmineConfig = TestConfigs.getRedmineConfig();
        redmineConfig.setProjectKey(redmineProject.getIdentifier());

        // load from MSP
        int maxTasksNumber = 9999;
        List<GTask> loadedTasks = TaskLoader.loadTasks(maxTasksNumber, msProjectConnector, "msp1",
                ProgressMonitorUtils.DUMMY_MONITOR);

        RedmineConnector redmineConnector = new RedmineConnector(redmineConfig, TestConfigs.getRedmineSetup());
        // save to Redmine
        List<GTask> result = TestUtils.saveAndLoadList(redmineConnector, loadedTasks,
                JavaFieldAdapter.rows(AllFields.summary)
        );
        assertThat(result).overridingErrorMessage("must have created 2 tasks")
                .hasSize(2);
    }

    // msp tasks with one-side disconnected relationships are saved to Redmine
    @Test
    public void mspTasksWithOneSideDisconnectedRelationshipsAreSavedToRedmine() throws ConnectorException {
        RedmineConfig redmineConfig = TestConfigs.getRedmineConfig();
        redmineConfig.setProjectKey(redmineProject.getIdentifier());

        MSPConnector projectConnector = new MSPConnector(
                getMspSetup("com/taskadapter/integrationtests/ProjectWithOneSideDisconnectedRelationships.xml"));

        int maxTasksNumber = 9999;
        List<GTask> loadedTasks = TaskLoader.loadTasks(maxTasksNumber, projectConnector, "project1",
                ProgressMonitorUtils.DUMMY_MONITOR);
        // save to Redmine
        RedmineConnector redmineConnector = new RedmineConnector(redmineConfig, TestConfigs.getRedmineSetup());

        List<GTask> result = TestUtils.saveAndLoadList(redmineConnector, loadedTasks,
                JavaFieldAdapter.rows(AllFields.summary)
        );
        assertThat(result).overridingErrorMessage("must have created 13 tasks")
                .hasSize(13);
    }

    // custom value saved to another custom value with default value
    @Test
    public void customValueSavedToAnotherCustomValueWithDefaultValue() throws RedmineException {
        List<FieldRow<?>> rows = Arrays.asList(
                new FieldRow(summaryOpt, summaryOpt, ""),
                new FieldRow(Optional.of(new CustomString("my_custom_1")), Optional.of(new CustomString("my_custom_2")), "default custom alex")
        );
        Issue issue = RedmineTestUtil.createIssueInRedmineWithCustomField(redmineProject.getId(), new CustomString("my_custom_1"), "");
        SaveResult result = adapter.adapt(rows);

        Issue loaded = RedmineTestLoader.loadCreatedTask(RedmineTestInitializer.mgr, result);
        assertThat(loaded.getCustomFieldByName("my_custom_1").getValue())
                .isEqualTo("");
        assertThat(loaded.getCustomFieldByName("my_custom_2").getValue())
                .isEqualTo("default custom alex");
    }

    // loads custom field in task and saves it to another custom field
    @Test
    public void loadsCustomFieldInTaskAndSavesItToAnotherCustomField() throws RedmineException {
        List<FieldRow<?>> rows = Arrays.asList(
                new FieldRow(summaryOpt, summaryOpt, ""),
                new FieldRow(Optional.of(new CustomString("my_custom_1")), Optional.of(new CustomString("my_custom_2")), "")
        );
        Issue issue = RedmineTestUtil.createIssueInRedmineWithCustomField(redmineProject.getId(), new CustomString("my_custom_1"), "some value");

        SaveResult result = adapter.adapt(rows);

        Issue loaded = RedmineTestLoader.loadCreatedTask(RedmineTestInitializer.mgr, result);

        assertThat(loaded.getCustomFieldByName("my_custom_1").getValue())
                .isEqualTo("");
        assertThat(loaded.getCustomFieldByName("my_custom_2").getValue())
                .isEqualTo("some value");
    }

    // description field gets default value on save if needed
    @Test
    public void descriptionFieldGetsDefaultValueOnSaveIfNeeded() throws RedmineException {
        List<FieldRow<?>> rows = Arrays.asList(
                new FieldRow(summaryOpt, summaryOpt, ""),
                new FieldRow(descriptionOpt, descriptionOpt, "default alex description")
        );

        Issue issue = RedmineTestUtil.createIssueInRedmine(redmineProject, "");
        SaveResult result = adapter.adapt(rows);

        Issue loaded = RedmineTestLoader.loadCreatedTask(RedmineTestInitializer.mgr, result);

        assertThat(loaded.getDescription()).isEqualTo("default alex description");
    }

    // description field keeps source value when it is present
    @Test
    public void descriptionFieldKeepsSourceValueWhenItIsPresent() throws RedmineException {
        List<FieldRow<?>> rows = Arrays.asList(
                new FieldRow(summaryOpt, summaryOpt, ""),
                new FieldRow(descriptionOpt, descriptionOpt, "default alex description")
        );
        Issue issue = RedmineTestUtil.createIssueInRedmine(redmineProject, "description1");
        SaveResult result = adapter.adapt(rows);

        Issue loaded = RedmineTestLoader.loadCreatedTask(RedmineTestInitializer.mgr, result);


        assertThat(loaded.getDescription()).isEqualTo("description1");

        RedmineTestUtil.deleteIssue(issue.getId());
    }

    // description field value is saved to custom field
    @Test
    public void descriptionFieldValueIsSavedToCustomField() throws RedmineException {
        List<FieldRow<?>> rows = Arrays.asList(
                new FieldRow(summaryOpt, summaryOpt, ""),
                new FieldRow(descriptionOpt, customStringOpt("my_custom_1"), "")
        );

        Issue issue = RedmineTestUtil.createIssueInRedmine(redmineProject, "description 1");
        SaveResult result = adapter.adapt(rows);

        Issue loaded = RedmineTestLoader.loadCreatedTask(RedmineTestInitializer.mgr, result);
        assertThat(loaded.getCustomFieldByName("my_custom_1").getValue())
                .isEqualTo("description 1");
    }

    private void createRedmineHierarchy(RedmineConnector redmineConnector) {
        List<FieldRow<?>> redmineFields = Arrays.asList(new FieldRow(summaryOpt, summaryOpt, ""));
        try {
            GTask parent = GTaskBuilder.withSummary("parent task");
            TaskId parentId = TestUtils.save(redmineConnector, parent, redmineFields);

            GTask sub1 = GTaskBuilder.withSummary("sub 1");
            sub1.setParentIdentity(parentId);

            GTask sub2 = GTaskBuilder.withSummary("sub 2");
            sub2.setParentIdentity(parentId);


            TestUtils.save(redmineConnector, sub1, redmineFields);
            TestUtils.save(redmineConnector, sub2, redmineFields);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private FileSetup getMspSetup(String resourceName) {
        File file = new File(ResourceLoader.getAbsolutePathForResource(resourceName));
        return FileSetup.apply(MSPConnector.ID, "label", file.getAbsolutePath(), file.getAbsolutePath());
    }

    private MSPConnector getMspConnector(File folder) {
        var file = new File(folder, "msp_temp_file.xml");
        var setup = FileSetup.apply(MSPConnector.ID, "label", file.getAbsolutePath(), file.getAbsolutePath());
        return new MSPConnector(setup);
    }
}

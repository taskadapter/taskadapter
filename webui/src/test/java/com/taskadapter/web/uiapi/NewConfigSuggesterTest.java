package com.taskadapter.web.uiapi;

import com.taskadapter.common.ui.FieldMapping;
import com.taskadapter.common.ui.NewConfigSuggester;
import com.taskadapter.connector.jira.JiraFactory;
import com.taskadapter.connector.redmine.RedmineFactory;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.Field;
import com.taskadapter.model.GUser;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NewConfigSuggesterTest {
    private static final List<Field<?>> defaultRedmineFields = new RedmineFactory().getDefaultFieldsForNewConfig();
    private static final List<Field<?>> defaultJiraFields = new JiraFactory().getDefaultFieldsForNewConfig();

    private static final int jiraRedmineFieldsNumber = 8;

    @Test
    public void suggestsAllElementsFromLeftConnector() {
        var list = NewConfigSuggester.suggestedFieldMappingsForNewConfig(
                defaultRedmineFields,
                defaultJiraFields);

        assertThat(list).hasSize(jiraRedmineFieldsNumber);
        assertThat(list).contains(
                new FieldMapping(AllFields.assigneeLoginName, AllFields.assigneeLoginName, true, null));
    }

    @Test
    public void suggestsAllElementsFromRightConnector() {
        var list = NewConfigSuggester.suggestedFieldMappingsForNewConfig(
                defaultJiraFields,
                defaultRedmineFields);

        assertThat(list).hasSize(jiraRedmineFieldsNumber);
    }
}

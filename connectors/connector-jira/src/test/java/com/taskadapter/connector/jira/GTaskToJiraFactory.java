package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.Version;

import java.util.List;

public class GTaskToJiraFactory {

    public static JiraConfig config = JiraPropertiesLoader.createTestConfig();
    public static Iterable<Priority> defaultPriorities = MockData.loadPriorities();
    public static Iterable<IssueType> issueTypeList = MockData.loadIssueTypes();
    public static Iterable<Version> versions = MockData.loadVersions();
    public static Iterable<BasicComponent> components = MockData.loadComponents();
    public static CustomFieldResolver customFieldsResolver = new CustomFieldResolver(List.of());

    public static GTaskToJira getConverter() {
        return getConverter(defaultPriorities);
    }

    public static GTaskToJira getConverter(Iterable<Priority> priorities) {
        return new GTaskToJira(config, customFieldsResolver, versions, components, priorities);
    }
}

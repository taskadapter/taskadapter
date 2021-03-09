package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.Field;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.internal.json.FieldJsonParser;
import com.atlassian.jira.rest.client.internal.json.IssueJsonParser;
import com.atlassian.jira.rest.client.internal.json.JsonArrayParser;
import com.google.common.io.Resources;
import com.google.gson.reflect.TypeToken;
import com.taskadapter.connector.jira.mock.BasicComponentMock;
import com.taskadapter.connector.jira.mock.IssueTypeMock;
import com.taskadapter.connector.jira.mock.PriorityMock;
import com.taskadapter.connector.jira.mock.VersionMock;
import com.taskadapter.connector.testlib.TestDataLoader;
import org.assertj.core.util.Lists;
import org.codehaus.jettison.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MockData {
    public static Issue loadIssue(String fileName) {
        try {
            var issueParser = new IssueJsonParser();
            var fileContents = Resources.toString(Resources.getResource(fileName), StandardCharsets.UTF_8);
            var obj = new JSONObject(fileContents);
            return issueParser.parse(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Iterable<Priority> loadPriorities() {
        return (Iterable<Priority>)
                TestDataLoader.load("priorities_6.4.11.json", new TypeToken<ArrayList<PriorityMock>>() {
                }.getType());
    }

    public static Iterable<IssueType> loadIssueTypes() {
        return (Iterable<IssueType>)
                TestDataLoader.load("issuetypes_jira5.0.6.json", new TypeToken<ArrayList<IssueTypeMock>>() {
                }.getType());
    }

    public static Iterable<Version> loadVersions() {
        /* I deleted "release date" attribute from the versions file to fix
         * "Unable to invoke no-args constructor for class org.joda.time.Chronology" problem.
         * See the original file "versions_jira5.0.6.json"
         */
        return (Iterable<Version>)
                TestDataLoader.load("versions_without_release_date_jira5.0.6.json", new TypeToken<ArrayList<VersionMock>>() {
                }.getType());
    }

    public static Iterable<BasicComponent> loadComponents() {
        return (Iterable<BasicComponent>)
                TestDataLoader.load("components_jira5.0.6.json", new TypeToken<ArrayList<BasicComponentMock>>() {
                }.getType());
    }

    public static Iterable<com.atlassian.jira.rest.client.api.domain.Field> loadFieldDefinitions() {
        try {
            var fieldsParser = FieldJsonParser.createFieldsArrayParser();
            Iterable<Field> fields = fieldsParser.parse(ResourceUtil.getJsonArrayFromResource("7.1.9/fields.json"));
            return Lists.newArrayList(fields);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

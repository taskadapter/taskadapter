package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.internal.json.IssueJsonParser;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.reflect.TypeToken;
import com.taskadapter.connector.jira.mock.BasicComponentMock;
import com.taskadapter.connector.jira.mock.IssueTypeMock;
import com.taskadapter.connector.jira.mock.PriorityMock;
import com.taskadapter.connector.jira.mock.VersionMock;
import com.taskadapter.connector.testlib.TestDataLoader;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MockData {

    public static Issue loadIssue(String fileName) throws IOException, JSONException {
        final IssueJsonParser issueParser = new IssueJsonParser();
        String fileContents = Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
        JSONObject obj = new JSONObject(fileContents);
        return issueParser.parse(obj);
    }

    public static Iterable<Priority> loadPriorities() {
        return (Iterable<Priority>) TestDataLoader.load("priorities_6.4.11.json", new TypeToken<ArrayList<PriorityMock>>() {
        }.getType());
    }

    public static Iterable<IssueType> loadIssueTypes() {
        return (Iterable<IssueType>) TestDataLoader.load("issuetypes_jira5.0.6.json", new TypeToken<ArrayList<IssueTypeMock>>() {
        }.getType());
    }

    public static Iterable<Version> loadVersions() {
        /* I deleted "release date" attribute from the versions file to fix
         * "Unable to invoke no-args constructor for class org.joda.time.Chronology" problem.
         * See the original file "versions_jira5.0.6.json"
         */
        return (Iterable<Version>) TestDataLoader.load("versions_without_release_date_jira5.0.6.json", new TypeToken<ArrayList<VersionMock>>() {
        }.getType());
    }

    public static Iterable<BasicComponent> loadComponents() {
        return (Iterable<BasicComponent>) TestDataLoader.load("components_jira5.0.6.json", new TypeToken<ArrayList<BasicComponentMock>>() {
        }.getType());
    }
}

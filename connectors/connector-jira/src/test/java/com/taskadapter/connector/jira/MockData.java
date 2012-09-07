package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.*;
import com.atlassian.jira.rest.client.internal.json.IssueJsonParser;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.taskadapter.connector.TestDataLoader;
import com.taskadapter.connector.jira.mock.*;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MockData {

    public static Issue loadIssue() throws IOException, JSONException {
        final IssueJsonParser issueParser = new IssueJsonParser();
        String fileContents = Resources.toString(Resources.getResource("issue_jira_5.0.1.json"), Charsets.UTF_8);
        JSONObject obj = new JSONObject(fileContents);
        return issueParser.parse(obj);
    }

    public static Iterable<Priority> loadPriorities() {
        return (Iterable<Priority>) TestDataLoader.load("priorities_jira5.0.6.json", new TypeToken<ArrayList<PriorityMock>>() {
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

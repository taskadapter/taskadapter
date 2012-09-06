package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.*;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.taskadapter.connector.jira.mock.BasicComponentMock;
import com.taskadapter.connector.jira.mock.IssueTypeMock;
import com.taskadapter.connector.jira.mock.PriorityMock;
import com.taskadapter.connector.jira.mock.VersionMock;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MockData {
    public static Iterable<Priority> loadPriorities() {
        return load("priorities_jira5.0.6.json", new TypeToken<ArrayList<PriorityMock>>() {}.getType());
    }

    public static Iterable<IssueType> loadIssueTypes() {
        return load("issuetypes_jira5.0.6.json", new TypeToken<ArrayList<IssueTypeMock>>() {}.getType());
    }

//    public static Iterable<Version> loadVersions() {
//        return load("versions_jira5.0.6.json", new TypeToken<ArrayList<VersionMock>>() {}.getType());
//    }

    public static Iterable<BasicComponent> loadComponents() {
        return load("components_jira5.0.6.json", new TypeToken<ArrayList<BasicComponentMock>>() {}.getType());
    }

    public static Iterable load(String fileName, Type fooType) {
        try {
            String fileContents = Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
            return new Gson().fromJson(fileContents, fooType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

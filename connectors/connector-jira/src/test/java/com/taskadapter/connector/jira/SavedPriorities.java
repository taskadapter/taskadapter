package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.Priority;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SavedPriorities {
    public static Iterable<Priority> load() {
        try {
            String fileContents = Resources.toString(Resources.getResource("priorities_jira5.0.6.json"), Charsets.UTF_8);
            Type fooType = new TypeToken<ArrayList<PriorityMock>>() {}.getType();
            return new Gson().fromJson(fileContents, fooType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

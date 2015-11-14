package com.taskadapter.model;

import java.util.HashMap;
import java.util.Map;

public class GTaskDescriptor {

    // TODO REVIEW Probably it should not be a enum anymore. But something like
    // class <T> Field<T> {
    //      public Class getImplClass()
    //      public ? parseDefaultValue(String xxx)
    //      public JSon serializeDefaultValue(? q)
    //      public ? deserializeDefaultValue(J json)
    //      etc...
    // }
    public enum FIELD {
        ID, KEY, PARENT_KEY,
        SUMMARY, DESCRIPTION, TASK_TYPE, ESTIMATED_TIME, DONE_RATIO, ASSIGNEE, START_DATE, DUE_DATE,
        CREATED_ON, UPDATED_ON,
        CLOSE_DATE, PRIORITY, REMOTE_ID, TASK_STATUS,
        ENVIRONMENT,
        CHILDREN, TARGET_VERSION, RELATIONS
    }

    // TODO this should be on the UI level, not in the model.
    @SuppressWarnings("serial")
    private static Map<FIELD, String> displayNames = new HashMap<FIELD, String>() {
        {
            put(FIELD.ID, "Id");
            put(FIELD.SUMMARY, "Summary");
            put(FIELD.TASK_TYPE, "Task type");
            put(FIELD.START_DATE, "Start Date");
            put(FIELD.ASSIGNEE, "Assignee");
            put(FIELD.DESCRIPTION, "Description");
            put(FIELD.DUE_DATE, "Due Date");
            put(FIELD.CLOSE_DATE, "Close Date");
            put(FIELD.ESTIMATED_TIME, "Estimated time");
            put(FIELD.DONE_RATIO, "Done ratio (% done)");
            /*
                * save "remote/original ID" in this connector's database/file/whatever after tasks were loaded from some source
                */
            put(FIELD.REMOTE_ID, "Remote ID");
            put(FIELD.PRIORITY, "Task priority");
            put(FIELD.TASK_STATUS, "Task status");
            put(FIELD.ENVIRONMENT, "Environment");
            put(FIELD.TARGET_VERSION, "Target Version");
        }
    };

    public static String getDisplayValue(FIELD f) {
        return displayNames.get(f);
    }
}

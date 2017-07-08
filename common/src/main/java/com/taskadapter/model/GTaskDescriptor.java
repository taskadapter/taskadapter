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
    private static Map<String, String> displayNames = new HashMap<String, String>() {
        {
            put(FIELD.ID.name(), "Id");
            put(FIELD.SUMMARY.name(), "Summary");
            put(FIELD.TASK_TYPE.name(), "Task type");
            put(FIELD.START_DATE.name(), "Start Date");
            put(FIELD.ASSIGNEE.name(), "Assignee");
            put(FIELD.DESCRIPTION.name(), "Description");
            put(FIELD.DUE_DATE.name(), "Due Date");
            put(FIELD.CLOSE_DATE.name(), "Close Date");
            put(FIELD.ESTIMATED_TIME.name(), "Estimated time");
            put(FIELD.DONE_RATIO.name(), "Done ratio (% done)");
            /*
                * save "remote/original ID" in this connector's database/file/whatever after tasks were loaded from some source
                */
            put(FIELD.REMOTE_ID.name(), "Remote ID");
            put(FIELD.PRIORITY.name(), "Task priority");
            put(FIELD.TASK_STATUS.name(), "Task status");
            put(FIELD.ENVIRONMENT.name(), "Environment");
            put(FIELD.TARGET_VERSION.name(), "Target Version");
        }
    };

    public static String getDisplayValue(String f) {
        return displayNames.get(f);
    }
}

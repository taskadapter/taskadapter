package com.taskadapter.model;

import java.util.HashMap;
import java.util.Map;

public class GTaskDescriptor {

	public static enum FIELD {
        SUMMARY, DESCRIPTION, TASK_TYPE, ESTIMATED_TIME, DONE_RATIO, ASSIGNEE, START_DATE, DUE_DATE, PRIORITY, REMOTE_ID
	}

	@SuppressWarnings("serial")
	private static Map<FIELD, String> displayNames = new HashMap<FIELD, String>() {
		{
			put(FIELD.SUMMARY, "Summary");
			put(FIELD.TASK_TYPE, "Task type");
			put(FIELD.START_DATE, "Start Date");
			put(FIELD.ASSIGNEE, "Assignee");
			put(FIELD.DESCRIPTION, "Description");
			put(FIELD.DUE_DATE, "Due Date");
			put(FIELD.ESTIMATED_TIME, "Estimated time");
			put(FIELD.DONE_RATIO, "Done ratio (% done)");
			/*
			 * save "remote/original ID" in this connector's database/file/whatever after tasks were loaded from some source
			 */
			put(FIELD.REMOTE_ID, "Remote ID");
            put(FIELD.PRIORITY, "Task priority");
		}
	};

	public static String getDisplayValue(FIELD f) {
		return displayNames.get(f);
	}
}

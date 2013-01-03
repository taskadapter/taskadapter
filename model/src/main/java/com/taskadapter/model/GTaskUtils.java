package com.taskadapter.model;

import java.util.Comparator;

/**
 * Utilities to work with a task.
 * 
 * @author maxkar
 * 
 */
public final class GTaskUtils {
	/**
	 * Compares two tasks by its' IDs.
	 */
	public static Comparator<GTask> ID_COMPARATOR = new Comparator<GTask>() {
		@Override
		public int compare(GTask o1, GTask o2) {
			return o1.getId().compareTo(o2.getId());
		}
	};
}

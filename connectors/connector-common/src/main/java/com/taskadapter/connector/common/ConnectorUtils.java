package com.taskadapter.connector.common;

import java.util.Collections;
import java.util.List;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskUtils;

/**
 * Usefull (and not very usefull) methods for general connector interface.
 * 
 * @author maxkar
 * 
 */
public final class ConnectorUtils {

	/**
	 * Loads a data ordered by ID.
	 * 
	 * @param connector
	 *            connector to fetch a data from.
	 * @param monitor
	 *            monitor to use.
	 * @return loaded data, sorted by task ID.
	 */
	public static List<GTask> loadDataOrderedById(Connector<?> connector,
			ProgressMonitor monitor) {
		try {
			final List<GTask> tasks = connector.loadData(monitor);
			Collections.sort(tasks, GTaskUtils.ID_COMPARATOR);
			return tasks;
		} catch (Exception e) {
			// FIXME: we should use proper exceptions!
			throw new RuntimeException(e);
		}
	}

	/**
	 * Loads a data ordered by ID.
	 * 
	 * @param connector
	 *            connector to fetch a data from.
	 * @return loaded data, sorted by task ID.
	 */
	public static List<GTask> loadDataOrderedById(Connector<?> connector) {
		return loadDataOrderedById(connector, ProgressMonitorUtils.getDummyMonitor());
	}
}

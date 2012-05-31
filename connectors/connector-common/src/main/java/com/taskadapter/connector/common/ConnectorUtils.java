package com.taskadapter.connector.common;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskUtils;

import java.util.Collections;
import java.util.List;

/**
 * Usefull (and not very usefull) methods for general connector interface.
 * 
 * @author maxkar
 * 
 */
public final class ConnectorUtils {

	/**
	 * Loads tasks sorted by ID.
	 * 
	 * @param connector
	 *            connector to fetch data from.
	 * @param monitor
	 *            monitor to use.
	 * @return loaded tasks list, sorted by task ID.
	 */
	@Deprecated
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
	 * Loads tasks sorted by ID.
	 * 
	 * @param connector
	 *            connector to fetch data from.
	 * @return loaded tasks list, sorted by task ID.
	 */
	@Deprecated
	public static List<GTask> loadDataOrderedById(Connector<?> connector) {
		return loadDataOrderedById(connector, ProgressMonitorUtils.getDummyMonitor());
	}
}

package com.taskadapter.connector.common;

import com.taskadapter.connector.definition.ProgressMonitor;

/**
 * Utilities for {@link ProgressMonitor}.
 * 
 * @author maxkar
 * 
 */
public class ProgressMonitorUtils {

	/**
	 * "Dummy" connector monitor.
	 */
	private static final ProgressMonitor DUMMY_MONITOR = new ProgressMonitor() {
		@Override
		public void worked(int work) {
			// not used.
		}

		@Override
		public void done() {
			// not used.
		}

		@Override
		public void beginTask(String taskName, int total) {
			// not used.
		}
	};

	/**
	 * Returns a "dummy" monitor. Note, that this method may return same
	 * instance each time, not a new one.
	 * 
	 * @return dummy monitor, which ignores all data.
	 */
	public static ProgressMonitor getDummyMonitor() {
		return ProgressMonitorUtils.DUMMY_MONITOR;
	}

}

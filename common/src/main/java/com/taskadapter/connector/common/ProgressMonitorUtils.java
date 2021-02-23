package com.taskadapter.connector.common;

import com.taskadapter.connector.definition.ProgressMonitor;

public final class ProgressMonitorUtils {

	public static final ProgressMonitor DUMMY_MONITOR = new ProgressMonitor() {
		@Override
		public void worked(int work) {
			// not used.
		}

		@Override
		public void done() {
			// not used.
		}

		@Override
		public void stopTask() {
			// not used
		}

		@Override
		public boolean isStopped() {
			return false;
		}

		@Override
		public void beginTask(String taskName, int total) {
			// not used.
		}
	};
}

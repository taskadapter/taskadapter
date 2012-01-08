package com.taskadapter.connector.common;

import com.taskadapter.connector.definition.ConnectorConfig;

/**
 * @author Alexey Skorokhodov
 *
 * @param <T>
 */
public abstract class AbstractTaskLoader<T extends ConnectorConfig> implements TaskLoader<T> {

	/**
	 * The default implementation does nothing.
	 */
	@Override
	public void beforeTasksLoad(T config) {
		// nothing
	}
}


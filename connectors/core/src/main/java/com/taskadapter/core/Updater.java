package com.taskadapter.core;

import java.util.ArrayList;
import java.util.List;

import com.taskadapter.connector.common.TreeUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.FileBasedConnector;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.model.GTask;

public class Updater {

	private List<GTask> existingTasks;
	private List<GTask> tasksInExternalSystem;
	private Connector fileConnector;
	private Connector remoteConnector;
	private ProgressMonitor monitor;

	public Updater(Connector fileConnector, Connector remoteConnector) {
		super();
		this.fileConnector = fileConnector;
		this.remoteConnector = remoteConnector;
	}

	public void start() {
		loadTasksFromFile(null);
		removeTasksWithoutRemoteIds();
		loadExternalTasks();
		saveFile();
	}

	public void loadTasksFromFile(ProgressMonitor monitor) {
		this.existingTasks = fileConnector.loadData(monitor);
	}

	public void loadExternalTasks() {
		this.tasksInExternalSystem = new ArrayList<GTask>(existingTasks.size());
		if (monitor != null) {
			monitor.beginTask("Loading " + existingTasks.size()
					+ " tasks from " + getRemoteSystemURI(),
					existingTasks.size());
		}
		for (GTask gTask : existingTasks) {
			if (gTask.getRemoteId() != null) {
				GTask task = remoteConnector.loadTaskByKey(gTask.getRemoteId());
				task.setRemoteId(gTask.getRemoteId());
				tasksInExternalSystem.add(task);
			}
			if (monitor != null) {
				monitor.worked(1);
			}
		}
		if (monitor != null) {
			monitor.done();
		}

	}
	
	public void saveFile() {
		// TODO remove the casting!
		((FileBasedConnector)fileConnector).updateTasksByRemoteIds(tasksInExternalSystem);
	}
	
	public int getNumberOfUpdatedTasks() {
		return tasksInExternalSystem.size();
	}

	public List<GTask> getExistingTasks() {
		return existingTasks;
	}
	
	public void setConfirmedTasks(List<GTask> tasks) {
		this.existingTasks = tasks;
	}
	
	public void removeTasksWithoutRemoteIds() {
		this.existingTasks = TreeUtils.cloneTreeSkipEmptyRemoteIds(existingTasks);
	}
	
	public String getFilePath() {
		return fileConnector.getConfig().getTargetLocation();
	}
	
	public String getRemoteSystemURI() {
		return remoteConnector.getConfig().getSourceLocation();
	}

	public void setMonitor(ProgressMonitor monitor) {
		this.monitor = monitor;
	}
}

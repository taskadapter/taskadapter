package com.taskadapter.webui;

import java.util.ArrayList;
import java.util.List;

import com.taskadapter.model.GTask;
import com.taskadapter.model.GUser;

/**
 * This is a temporary class to test the tasks tree shown in UI.
 *
 * @author Alexey Skorokhodov
 */
public class TestModel {
	List<GTask> tasks = new ArrayList<GTask>();

	public List<GTask> getTasks() {
		return tasks;
	}

	GTask root;
	
	public void fillWithTestData(){
		root = new GTask();

		for (int i = 0; i < 4; i++) {
			GTask task = new GTask();
			task.setId(i);
			task.setSummary("task number " + i);
			task.setRemoteId(Integer.toString(1000+i));
			GUser ass = new GUser("Alex");
			task.setAssignee(ass);
			tasks.add(task);
			
			root.getChildren().add(task);
			
			// add children
			GTask cloned = new GTask(task);
			cloned.setId(task.getId() + 100);
			cloned.setSummary(task.getSummary()+" - sub1");
			task.getChildren().add(cloned);
		}
	}
	
	public GTask getRoot() {
		return root;
	}
	
}

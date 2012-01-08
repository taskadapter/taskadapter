package com.taskadapter.connector.common;

import com.taskadapter.model.GTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class TreeUtils {

	static public List<GTask> cloneTree(List<GTask> tree) {
		List<GTask> clonedTree = new ArrayList<GTask>();

		for (Iterator<GTask> iterator = tree.iterator(); iterator.hasNext();) {
			GTask task = iterator.next();
			GTask cloned = new GTask(task);
			cloned.getChildren().clear();
			clonedTree.add(cloned);

			cloned.getChildren().addAll(cloneTree(task.getChildren()));
		}

		return clonedTree;
	}

	// TODO refactor: this can be generalized and combined with the other util methods
	// in this class
	public static List<GTask> cloneTreeSkipEmptyRemoteIds(List<GTask> tree) {
		List<GTask> clonedTree = new ArrayList<GTask>();

		for (Iterator<GTask> iterator = tree.iterator(); iterator.hasNext();) {
			GTask task = iterator.next();
			GTask cloned = new GTask(task);
			cloned.getChildren().clear();
			if (task.getRemoteId() != null) {
				// only skip the tasks with no Remote IDs
				// the children can still have Remote IDs and thus need to be
				// included in the tree
				clonedTree.add(cloned);
			}
			cloned.getChildren().addAll(cloneTree(task.getChildren()));
		}

		return clonedTree;
	}

//	private static void parseTreeToLists(List<GTask> tasksTree,
//			List<GTask> tasksWithRemoteID, List<GTask> tasksWithoutRemoteID) {
//
//		for (Iterator<GTask> iterator = tasksTree.iterator(); iterator.hasNext();) {
//			GTask task = iterator.next();
//
//			// to avoid having "flat lists with tasks", where each element is
//			// actually a node with a whole subtree
//			GTask cloned = new GTask(task);
//
//			cloned.getChildren().clear();
//
//			if (task.getRemoteId() == null) {
//				tasksWithoutRemoteID.add(cloned);
//			} else {
//				tasksWithRemoteID.add(cloned);
//			}
//			if (!task.getChildren().isEmpty()) {
//				parseTreeToLists(task.getChildren(), tasksWithRemoteID,
//						tasksWithoutRemoteID);
//			}
//		}
//	}

	public static List<GTask> buildTreeFromFlatList(List<GTask> tasksFlatList) {
		TreeUtilsMap map = new TreeUtilsMap(tasksFlatList);
		GTask root = new GTask();
		for (GTask task : tasksFlatList) {
			GTask parentTask = map.getByKey(task.getParentKey());
			if (parentTask != null) {
				parentTask.getChildren().add(task);
			} else {
				root.getChildren().add(task);
			}
		}
		return root.getChildren();
	}
	
	/**
	 * Utility class to help convert flat list of Tasks to a tree structure.
	 * 
	 * @author Alexey Skorokhodov
	 */
	static class TreeUtilsMap {
		/**
		 * map: key -> GTask
		 */
		private HashMap<String, GTask> tasksMap = new HashMap<String, GTask>();

		public TreeUtilsMap(List<GTask> list) {
			Iterator<GTask> it = list.iterator();
			while (it.hasNext()) {
				GTask task = it.next();
				tasksMap.put(task.getKey(), task);
			}
		}

		public GTask getByKey(String key) {
			return tasksMap.get(key);
		}
	}
	
	public static class CreateUpdateWrapper {
		List<GTask> toCreate;
		List<GTask> toUpdate;
		public CreateUpdateWrapper(List<GTask> toCreate,
				List<GTask> toUpdate) {
			super();
			this.toCreate = toCreate;
			this.toUpdate = toUpdate;
		}
		public List<GTask> getToCreate() {
			return toCreate;
		}
		public List<GTask> getToUpdate() {
			return toUpdate;
		}
		
	}
}

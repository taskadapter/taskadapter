package com.taskadapter.connector.common;

import com.taskadapter.model.GTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class TreeUtils {

    static public List<GTask> cloneTree(Collection<GTask> tree) {
        List<GTask> clonedTree = new ArrayList<>();

        for (GTask task : tree) {
            GTask cloned = createShallowCopyWithoutChildren(task);
            cloned.getChildren().clear();
            clonedTree.add(cloned);

            cloned.getChildren().addAll(cloneTree(task.getChildren()));
        }

        return clonedTree;
    }

    // TODO refactor: this can be generalized and combined with the other util methods
    // in this class
    public static List<GTask> cloneTreeSkipEmptyRemoteIds(List<GTask> tree) {
        List<GTask> clonedTree = new ArrayList<>();

        for (GTask task : tree) {
            GTask cloned = createShallowCopyWithoutChildren(task);
            cloned.getChildren().clear();
            if (task.getSourceSystemId() != null) {
                // only skip the tasks with no Remote IDs
                // the children can still have Remote IDs and thus need to be
                // included in the tree
                clonedTree.add(cloned);
            }
            cloned.getChildren().addAll(cloneTree(task.getChildren()));
        }

        return clonedTree;
    }

    public static List<GTask> buildTreeFromFlatList(List<GTask> tasksFlatList) {
        TreeUtilsMap map = new TreeUtilsMap(tasksFlatList);
        GTask root = new GTask();
        for (GTask task : tasksFlatList) {
            if (task.getParentIdentity() != null) {
                GTask parentTask = map.getByKey(task.getParentIdentity().getKey());
                if (parentTask != null) {
                    parentTask.getChildren().add(task);
                } else {
                    root.getChildren().add(task);
                }
            } else {
                root.getChildren().add(task);
            }
        }
        return root.getChildren();
    }

    /**
     * Utility class to help convert flat list of Tasks to a tree structure.
     */
    static class TreeUtilsMap {
        private HashMap<String, GTask> keyToGTaskMap = new HashMap<>();

        public TreeUtilsMap(List<GTask> list) {
            for (GTask task : list) {
                keyToGTaskMap.put(task.getKey(), task);
            }
        }

        public GTask getByKey(String key) {
            return keyToGTaskMap.get(key);
        }
    }

    static GTask createShallowCopyWithoutChildren(GTask task) {
        GTask newTask = GTask.shallowClone(task);
        newTask.setChildren(new ArrayList<>());
        return newTask;
    }

}

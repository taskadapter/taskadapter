package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.model.NamedKeyedObjectImpl;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.configeditor.LookupOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Igor Laishen
 */
public class LoadTrackersOperation extends LookupOperation {
    public LoadTrackersOperation(ConfigEditor editor, PluginFactory factory) {
        super(editor, factory);
    }

    @Override
    protected List<? extends NamedKeyedObject> loadData() throws Exception {
        RedmineConfig config = (RedmineConfig) connector.getConfig();

        RedmineManager redmineManager = RedmineManagerFactory.createRedmineManager(config.getServerInfo());
        Project project = redmineManager.getProjectByKey(config.getProjectKey());

        List<Tracker> trackers = project.getTrackers();
        List<NamedKeyedObject> result = new ArrayList<NamedKeyedObject>(trackers.size());

        // XXX refactor: we don't even need these IDs
        for (Tracker tracker : trackers) {
            result.add(new NamedKeyedObjectImpl(Integer.toString(tracker.getId()), tracker.getName()));
        }
        return result;
    }
}

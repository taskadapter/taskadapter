package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.model.NamedKeyedObjectImpl;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.LookupOperation;
import org.redmine.ta.NotFoundException;
import org.redmine.ta.RedmineManager;
import org.redmine.ta.beans.Project;
import org.redmine.ta.beans.SavedQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public class RedmineLoadSavedQueriesOperation extends LookupOperation {

    public RedmineLoadSavedQueriesOperation(ConfigEditor editor, PluginFactory factory) {
        super(editor, factory);
    }

    @Override
    public List<? extends NamedKeyedObject> loadData()
            throws Exception {
        RedmineConfig config = (RedmineConfig) connector.getConfig();
        RedmineManager mgr = RedmineManagerFactory.createRedmineManager(config.getServerInfo());
        List<NamedKeyedObject> result = new ArrayList<NamedKeyedObject>();
        try {
            //get project id to filter saved queries
            Integer projectId = null;
            if (config.getProjectKey() != null && config.getProjectKey().length() > 0) {
                Project project = mgr.getProjectByKey(config.getProjectKey());
                if (project != null) {
                    projectId = project.getId();
                }
            }

            List<SavedQuery> savedQueries = mgr.getSavedQueries();
            // XXX refactor: we don't even need these IDs
            for (SavedQuery savedQuery : savedQueries) {
                Integer projectIdFromQuery = savedQuery.getProjectId();
                //we should add only common queries and queries which belongs to selected project
                if (projectIdFromQuery == null || projectIdFromQuery == 0 || projectIdFromQuery.equals(projectId)) {
                    result.add(new NamedKeyedObjectImpl(Integer.toString(savedQuery.getId()), savedQuery.getName()));
                }
            }
        } catch (NotFoundException e) {
            EditorUtil.show(editor.getWindow(), "Can't load Saved Queries", "The server did not return any saved queries.\n" +
                    "NOTE: This operation is only supported by Redmine 1.3.0+");
        }
        return result;
    }

}


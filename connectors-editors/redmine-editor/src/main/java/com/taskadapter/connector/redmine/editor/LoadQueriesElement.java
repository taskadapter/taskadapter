package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.redmineapi.NotFoundException;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.configeditor.EditorUtil;

import java.util.List;

public class LoadQueriesElement {

    private WindowProvider windowProvider;
    private RedmineConfig config;

    public LoadQueriesElement(WindowProvider windowProvider, RedmineConfig config) {
        this.windowProvider = windowProvider;
        this.config = config;
    }

    List<? extends NamedKeyedObject> loadQueries() throws ValidationException {
        try {
            return RedmineLoaders.loadData(config.getServerInfo(), config.getProjectKey());
        } catch (NotFoundException e) {
            EditorUtil.show(windowProvider.getWindow(), "Can't load Saved Queries", "The server did not return any saved queries.\n" +
                    "NOTE: This operation is only supported by Redmine 1.3.0+");
            return null;
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

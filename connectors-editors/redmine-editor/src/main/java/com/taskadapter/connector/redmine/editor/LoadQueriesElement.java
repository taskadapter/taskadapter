package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.redmineapi.NotFoundException;
import com.vaadin.ui.Notification;

import java.util.List;

public class LoadQueriesElement {

    private RedmineConfig config;

    public LoadQueriesElement(RedmineConfig config) {
        this.config = config;
    }

    List<? extends NamedKeyedObject> loadQueries() throws BadConfigException {
        try {
            return RedmineLoaders.loadData(config.getServerInfo(), config.getProjectKey());
        } catch (NotFoundException e) {
            Notification.show("Can't load Saved Queries", "The server did not return any saved queries.\n" +
                    "NOTE: This operation is only supported by Redmine 1.3.0+", Notification.Type.HUMANIZED_MESSAGE);
            return null;
        } catch (BadConfigException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

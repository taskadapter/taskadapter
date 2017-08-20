package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.redmineapi.NotFoundException;
import com.vaadin.ui.Notification;

import java.util.List;

public class LoadQueriesElement {

    private RedmineConfig config;
    private WebConnectorSetup setup;

    public LoadQueriesElement(RedmineConfig config, WebConnectorSetup setup) {
        this.config = config;
        this.setup = setup;
    }

    List<? extends NamedKeyedObject> loadQueries() throws BadConfigException {
        try {
            return RedmineLoaders.loadData(setup, config.getProjectKey());
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

package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.redmineapi.NotFoundException;
import com.taskadapter.web.callbacks.DataProvider;
import com.vaadin.flow.component.notification.Notification;

import java.util.List;

public class RedmineQueryListLoader implements DataProvider<List<? extends NamedKeyedObject>> {
    private final RedmineConfig config;
    private final WebConnectorSetup setup;

    public RedmineQueryListLoader(RedmineConfig config, WebConnectorSetup setup) {
        this.config = config;
        this.setup = setup;
    }

    @Override
    public List<? extends NamedKeyedObject> loadData() throws ConnectorException {
        try {
            return RedmineLoaders.loadData(setup, config.getProjectKey());
        } catch (NotFoundException e) {
            // TODO 14 remove UI element from here. this is inconsistent with error handling
            // in other loaders.
            Notification.show("The server did not return any saved queries.\n" +
                    "NOTE: This operation is only supported by Redmine 1.3.0+");
            return null;
        } catch (BadConfigException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

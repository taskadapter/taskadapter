package com.taskadapter.connector.mantis.editor;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.mantis.MantisConfig;
import com.taskadapter.connector.mantis.MantisConnector;
import com.taskadapter.web.ConnectorSetupPanel;
import com.taskadapter.web.DroppingNotSupportedException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
import com.taskadapter.web.configeditor.ProjectPanel;
import com.taskadapter.web.configeditor.server.ServerPanelFactory;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Sandbox;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.VerticalLayout;
import scala.Option;

import static com.vaadin.server.Sizeable.Unit.PIXELS;

public class MantisEditorFactory implements PluginEditorFactory<MantisConfig, WebConnectorSetup> {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.mantis.editor.messages";
    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public boolean isWebConnector() {
        return true;
    }

    @Override
    public ConnectorSetupPanel getEditSetupPanel(Sandbox sandbox, Option<WebConnectorSetup> setupOption) {
        return ServerPanelFactory.withLoginAndPassword(MantisConnector.ID(), MantisConnector.ID(), setupOption);
    }

    @Override
    public String formatError(Throwable e) {
        if (e instanceof ProjectNotSetException) {
            return MESSAGES.get("error.projectNotSet");
        }
        if (e instanceof ServerURLNotSetException) {
            return MESSAGES.get("error.serverUrlNotSet");
        }
        if (e instanceof QueryParametersNotSetException) {
            return MESSAGES.get("error.queryParametersNotSet");
        }
        if (e instanceof UnsupportedOperationException) {
            final UnsupportedOperationException uop = (UnsupportedOperationException) e;
            if ("saveRelations".equals(uop.getMessage())) {
                return MESSAGES.get("error.unsupported.relations");
            }
        }
        return null;
    }

    @Override
    public ComponentContainer getMiniPanelContents(Sandbox sandbox, MantisConfig config, WebConnectorSetup setup) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth(380, PIXELS);
        SimpleCallback NULL_PROJECT_INFO_CALLBACK = null;

        layout.addComponent(new ProjectPanel(new MethodProperty<>(config, "projectKey"),
                new MethodProperty<>(config, "queryIdStr"),
                Interfaces.fromMethod(DataProvider.class, MantisLoaders.class,
                        "getProjects", setup)
                , NULL_PROJECT_INFO_CALLBACK,
                Interfaces.fromMethod(DataProvider.class, MantisLoaders.class, "getFilters", config, setup), this));
        layout.addComponent(new OtherMantisFieldsPanel(config));

        return layout;
    }

    @Override
    public void validateForSave(MantisConfig config, WebConnectorSetup setup) throws BadConfigException {
        if (Strings.isNullOrEmpty(setup.host())) {
            throw new ServerURLNotSetException();
        }

        if (config.getProjectKey() == null || config.getProjectKey().isEmpty()) {
            throw new ProjectNotSetException();
        }
    }

    @Override
    public void validateForLoad(MantisConfig config, WebConnectorSetup setup) throws BadConfigException {
        if (Strings.isNullOrEmpty(setup.host())) {
            throw new ServerURLNotSetException();
        }
        if ((config.getProjectKey() == null || config.getProjectKey().isEmpty()) &&
                (config.getQueryId() == null)) {
            throw new ProjectNotSetException();
        }
    }

    @Override
    public String describeSourceLocation(MantisConfig config, WebConnectorSetup setup) {
        return setup.host();
    }

    @Override
    public String describeDestinationLocation(MantisConfig config, WebConnectorSetup setup) {
        return describeSourceLocation(config, setup);
    }

    @Override
    public boolean updateForSave(MantisConfig config, Sandbox sandbox, WebConnectorSetup setup)
            throws BadConfigException {
        validateForSave(config, setup);
        return false;
    }

    @Override
    public void validateForDropInLoad(MantisConfig config)
            throws BadConfigException, DroppingNotSupportedException {
        throw DroppingNotSupportedException.INSTANCE;
    }

}

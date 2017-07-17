package com.taskadapter.connector.mantis.editor;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.mantis.MantisConfig;
import com.taskadapter.web.DroppingNotSupportedException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.ProjectPanel;
import com.taskadapter.web.configeditor.server.ServerPanel;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Sandbox;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.VerticalLayout;

import static com.vaadin.server.Sizeable.Unit.PIXELS;

public class MantisEditorFactory implements PluginEditorFactory<MantisConfig> {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.mantis.editor.messages";
    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

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
    public ComponentContainer getMiniPanelContents(Sandbox sandbox, MantisConfig config) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth(380, PIXELS);
        final WebServerInfo serverInfo = config.getServerInfo();

        ServerPanel serverPanel = new ServerPanel(new MethodProperty<>(config, "label"),
                new MethodProperty<>(serverInfo, "host"),
                new MethodProperty<>(serverInfo, "userName"),
                new MethodProperty<>(serverInfo, "password"));
        layout.addComponent(serverPanel);

        // TODO VAADIN 7 why is this unused? I commented it out for now.
//        DataProvider<List<? extends NamedKeyedObject>> NULL_QUERY_PROVIDER = null;
        SimpleCallback NULL_PROJECT_INFO_CALLBACK = null;

        layout.addComponent(new ProjectPanel(new MethodProperty<>(config, "projectKey"),
                new MethodProperty<>(config, "queryIdStr"),
                Interfaces.fromMethod(DataProvider.class, MantisLoaders.class,
                        "getProjects", config.getServerInfo())
                , NULL_PROJECT_INFO_CALLBACK, Interfaces.fromMethod(DataProvider.class, MantisLoaders.class, "getFilters", config), this));
        layout.addComponent(new OtherMantisFieldsPanel(config));

        return layout;
    }

    @Override
    public void validateForSave(MantisConfig config) throws BadConfigException {
        final WebServerInfo serverInfo = config.getServerInfo();
        if (!serverInfo.isHostSet()) {
            throw new ServerURLNotSetException();
        }

        if (config.getProjectKey() == null || config.getProjectKey().isEmpty()) {
            throw new ProjectNotSetException();
        }
    }

    @Override
    public void validateForLoad(MantisConfig config) throws BadConfigException {
        final WebServerInfo serverInfo = config.getServerInfo();
        if (!serverInfo.isHostSet()) {
            throw new ServerURLNotSetException();
        }
        if ((config.getProjectKey() == null || config.getProjectKey().isEmpty()) && 
                (config.getQueryId() == null)) {
            throw new ProjectNotSetException();
        }
    }

    @Override
    public String describeSourceLocation(MantisConfig config) {
        return config.getServerInfo().getHost();
    }

    @Override
    public String describeDestinationLocation(MantisConfig config) {
        return describeSourceLocation(config);
    }

    @Override
    public boolean updateForSave(MantisConfig config, Sandbox sandbox)
            throws BadConfigException {
        validateForSave(config);
        return false;
    }

    @Override
    public void validateForDropInLoad(MantisConfig config)
            throws BadConfigException, DroppingNotSupportedException {
        throw DroppingNotSupportedException.INSTANCE;
    }

}

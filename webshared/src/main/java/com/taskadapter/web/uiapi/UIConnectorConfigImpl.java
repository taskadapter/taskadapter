package com.taskadapter.web.uiapi;

import java.util.ArrayList;
import java.util.List;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.service.Services;
import com.vaadin.ui.ComponentContainer;

/**
 * Implementation of RichConfig. Hides implementation details inside and keeps a
 * config-type magic inside.
 *
 * @param <T> type of a connector config.
 */
final class UIConnectorConfigImpl<T extends ConnectorConfig> extends UIConnectorConfig {
    private final PluginFactory<T> connectorFactory;
    private final PluginEditorFactory<T> editorFactory;
    private final T config;
    private final String connectorTypeId;

    public UIConnectorConfigImpl(PluginFactory<T> connectorFactory,
                                 PluginEditorFactory<T> editorFactory, T config,
                                 String connectorTypeId) {
        this.connectorFactory = connectorFactory;
        this.editorFactory = editorFactory;
        this.config = config;
        this.connectorTypeId = connectorTypeId;
    }

    @Override
    public String getConnectorTypeId() {
        return connectorTypeId;
    }

    @Override
    public String getConfigString() {
        return connectorFactory.writeConfig(config).toString();
    }

    @Override
    public String getLabel() {
        return config.getLabel();
    }

    @Override
    public void validateForLoad() throws BadConfigException {
        editorFactory.validateForLoad(config);
    }

    @Override
    public void validateForSave() throws BadConfigException {
        editorFactory.validateForSave(config);
    }

    @Override
    @Deprecated
    public ConnectorConfig getRawConfig() {
        return config;
    }

    @Override
    public Connector<?> createConnectorInstance() {
        return connectorFactory.createConnector(config);
    }

    @Override
    public ComponentContainer createMiniPanel(WindowProvider windowProvider, Services services) {
        return editorFactory.getMiniPanelContents(windowProvider, services, config);
    }

    @Override
    public AvailableFields getAvailableFields() {
        return connectorFactory.getAvailableFields();
    }

    @Override
    public String getSourceLocation() {
        return editorFactory.describeSourceLocation(config);
    }

    @Override
    public String getDestinationLocation() {
        return editorFactory.describeDestinationLocation(config);
    }

    @Override
    public String decodeException(Throwable e) {
        return editorFactory.formatError(e);
    }

}

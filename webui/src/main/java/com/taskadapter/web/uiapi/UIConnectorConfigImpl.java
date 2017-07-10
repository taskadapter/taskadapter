package com.taskadapter.web.uiapi;

import com.taskadapter.connector.Field;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.web.DroppingNotSupportedException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.webui.data.ExceptionFormatter;
import com.vaadin.ui.ComponentContainer;

import java.util.List;

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
    public void setLabel(String label) {
        config.setLabel(label);
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
    public void validateForDropIn() throws BadConfigException,
            DroppingNotSupportedException {
        editorFactory.validateForDropInLoad(config);
    }
    
    @Override
    public boolean updateForSave(Sandbox sandbox) throws BadConfigException {
        return editorFactory.updateForSave(config, sandbox);
    }

    @Override
    public NewConnector createConnectorInstance() {
        return connectorFactory.createConnector(config);
    }

    @Override
    public ComponentContainer createMiniPanel(Sandbox sandbox) {
        return editorFactory.getMiniPanelContents(sandbox, config);
    }

    @Override
    public List<Field> getAvailableFields() {
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
        final String guess = editorFactory.formatError(e);
        if (guess != null) {
            return guess;
        }
        return ExceptionFormatter.format(e);
    }

    @Override
    public String toString() {
        return "UIConnectorConfigImpl{" +
                "connectorTypeId='" + connectorTypeId + '\'' +
                '}';
    }
}

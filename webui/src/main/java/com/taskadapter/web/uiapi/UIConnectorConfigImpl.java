package com.taskadapter.web.uiapi;

import com.taskadapter.common.ui.FieldMapping;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ConnectorSetup;
import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.model.Field;
import com.taskadapter.web.DroppingNotSupportedException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.webui.data.ExceptionFormatter;

import java.util.List;

/**
 * Implementation of RichConfig. Hides implementation details inside and keeps a
 * config-type magic inside.
 *
 * @tparam C type of a connector config.
 * @tparam S type of setup, e.g. [[FileSetup]], [[WebConnectorSetup]]
 */
public class UIConnectorConfigImpl<C extends ConnectorConfig, S extends ConnectorSetup> implements UIConnectorConfig {

    private PluginFactory<C, S> connectorFactory;
    private PluginEditorFactory<C, S> editorFactory;
    private C config;
    private String connectorTypeId;

    private S setup;

    public UIConnectorConfigImpl(PluginFactory<C, S> connectorFactory, PluginEditorFactory<C, S> editorFactory, C config, String connectorTypeId) {
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
        return getConnectorSetup().getLabel();
    }

    @Override
    public ConnectorSetup getConnectorSetup() {
        return setup;
    }

    @Override
    public void setConnectorSetup(ConnectorSetup setup) {
        this.setup = (S) setup;
    }

    @Override
    public List<BadConfigException> validateForLoad() {
        return editorFactory.validateForLoad(config, setup);
    }

    @Override
    public List<BadConfigException> validateForSave(List<FieldMapping<?>> mappings) {
        return editorFactory.validateForSave(config, setup, mappings);
    }

    @Override
    public void validateForDropIn() throws BadConfigException, DroppingNotSupportedException {
        editorFactory.validateForDropInLoad(config);
    }

    @Override
    public NewConnector createConnectorInstance() {
        return connectorFactory.createConnector(config, setup);
    }

    @Override
    public SavableComponent createMiniPanel(Sandbox sandbox) {
        return editorFactory.getMiniPanelContents(sandbox, config, setup);
    }

    @Override
    public List<Field<?>> getAllFields() {
        return connectorFactory.getAllFields();
    }

    @Override
    public List<Field<?>> getDefaultFieldsForNewConfig() {
        return connectorFactory.getDefaultFieldsForNewConfig();
    }

    @Override
    public String getSourceLocation() {
        return editorFactory.describeSourceLocation(config, setup);
    }

    @Override
    public String getDestinationLocation() {
        return editorFactory.describeDestinationLocation(config, setup);
    }

    @Override
    public Messages fieldNames() {
        return editorFactory.fieldNames();
    }

    @Override
    public String decodeException(Throwable e) {
        var guess = editorFactory.formatError(e);
        if (guess != null) {
            return guess;
        }
        return ExceptionFormatter.format(e);
    }
}


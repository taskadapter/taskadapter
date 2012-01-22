package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.connector.definition.WebConfig;
import com.taskadapter.model.NamedKeyedObject;

import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public abstract class LookupOperation {
    protected final ConfigEditor editor;
    protected PluginFactory factory;
    protected Connector connector;
    protected WebConfig config;

    public LookupOperation(ConfigEditor editor, PluginFactory factory) {
        this.editor = editor;
        this.factory = factory;
    }

    protected void initOperation() {
        ConnectorConfig config;
//			editor.validateServerInfo();
        config = editor.getConfig();
        setConnector(factory.createConnector(config));
        // TODO casting is a hack.
        setConfig((WebConfig) config);
    }

    public List<? extends NamedKeyedObject> run() throws Exception {
        initOperation();
        return loadData();
    }

    protected abstract List<? extends NamedKeyedObject> loadData() throws Exception;

    protected void setConnector(Connector connector) {
        this.connector = connector;
    }

    protected void setConfig(WebConfig config) {
        this.config = config;
    }
}


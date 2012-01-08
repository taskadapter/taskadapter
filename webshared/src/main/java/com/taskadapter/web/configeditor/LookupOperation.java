package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.WebConfig;
import com.taskadapter.model.NamedKeyedObject;

import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public abstract class LookupOperation {
    protected final ConfigEditor editor;
    protected Descriptor descriptor;
    protected Connector connector;
    protected WebConfig config;

    public LookupOperation(ConfigEditor editor, Descriptor descriptor) {
        this.editor = editor;
        this.descriptor = descriptor;
    }

    protected void initOperation() {
        ConnectorConfig config;
//			editor.validateServerInfo();
        config = editor.getConfig();
        setConnector(descriptor.createConnector(config));
        // TODO casting is a hack.
        setConfig((WebConfig) config);
    }

    public List<? extends NamedKeyedObject> run() throws Exception {
//		Display.getDefault().syncExec(new Runnable() {
//			public void run() {
        initOperation();
//			}
//		});
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


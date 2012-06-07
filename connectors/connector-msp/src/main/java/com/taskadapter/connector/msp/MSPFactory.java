package com.taskadapter.connector.msp;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.taskadapter.connector.common.ConfigUtils;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;

/**
 * @author Alexey Skorokhodov
 */
public class MSPFactory implements PluginFactory<MSPConfig> {
    @Override
    public MSPConnector createConnector(MSPConfig config) {
        return new MSPConnector(config);
    }

    @Override
    public Descriptor getDescriptor() {
        return MSPDescriptor.instance;
    }
    
	@Override
	public JsonElement writeConfig(MSPConfig config) {
		return ConfigUtils.createDefaultGson().toJsonTree(config);
	}

	@Override
	public MSPConfig readConfig(JsonElement config) throws JsonParseException {
		return ConfigUtils.createDefaultGson().fromJson(config, MSPConfig.class);
	}

}

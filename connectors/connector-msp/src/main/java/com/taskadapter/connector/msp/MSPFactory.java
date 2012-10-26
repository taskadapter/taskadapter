package com.taskadapter.connector.msp;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.taskadapter.connector.common.ConfigUtils;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.PluginFactory;

public class MSPFactory implements PluginFactory<MSPConfig> {
    private static final Descriptor DESCRIPTOR = new Descriptor(MSPConnector.ID, MSPConfig.DEFAULT_LABEL);

    @Override
    public MSPConnector createConnector(MSPConfig config) {
        return new MSPConnector(config);
    }

    @Override
    public Descriptor getDescriptor() {
        return DESCRIPTOR;
    }
    
	@Override
	public JsonElement writeConfig(MSPConfig config) {
		return ConfigUtils.createDefaultGson().toJsonTree(config);
	}

	@Override
	public MSPConfig readConfig(JsonElement config) throws JsonParseException {
		return ConfigUtils.createDefaultGson().fromJson(config, MSPConfig.class);
	}

    @Override
    public MSPConfig createDefaultConfig() {
        return new MSPConfig();
    }

    @Override
    public Mappings createDefaultMappings() {
        return DefaultMSPMappings.generate();
    }
}

package com.taskadapter.connector.mantis;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.taskadapter.connector.common.ConfigUtils;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;

public class MantisFactory implements PluginFactory<MantisConfig> {
    @Override
    public MantisConnector createConnector(MantisConfig config) {
        return new MantisConnector(config);
    }

    @Override
    public Descriptor getDescriptor() {
        return MantisDescriptor.instance;
    }
    
	@Override
	public JsonElement writeConfig(MantisConfig config) {
		return ConfigUtils.createDefaultGson().toJsonTree(config);
	}

	@Override
	public MantisConfig readConfig(JsonElement config)
			throws JsonParseException {
		return ConfigUtils.createDefaultGson().fromJson(config,
				MantisConfig.class);
	}

}

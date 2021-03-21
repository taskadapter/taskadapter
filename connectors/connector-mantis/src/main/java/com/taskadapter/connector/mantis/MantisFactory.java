package com.taskadapter.connector.mantis;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.taskadapter.connector.common.ConfigUtils;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.model.Field;

import java.util.List;

public class MantisFactory implements PluginFactory<MantisConfig, WebConnectorSetup> {
    private static final Descriptor DESCRIPTOR = new Descriptor(MantisConnector.ID, "MantisBT");

    @Override
    public List<Field<?>> getAllFields() {
        return MantisField.fields;
    }

    @Override
    public MantisConnector createConnector(MantisConfig config, WebConnectorSetup setup) {
        return new MantisConnector(config, setup);
    }

    @Override
    public Descriptor getDescriptor() {
        return DESCRIPTOR;
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

    @Override
    public MantisConfig createDefaultConfig() {
        return new MantisConfig();
    }

    @Override
    public List<Field<?>> getDefaultFieldsForNewConfig() {
        return MantisField.fields;
    }
}

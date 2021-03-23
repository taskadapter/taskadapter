package com.taskadapter.connector.trello;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.common.ConfigUtils;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.model.Field;

import java.util.List;

public class TrelloFactory implements PluginFactory<TrelloConfig, WebConnectorSetup> {

    @Override
    public List<Field<?>> getAllFields() {
        return TrelloField.fields;
    }

    @Override
    public List<Field<?>> getDefaultFieldsForNewConfig() {
        return TrelloField.defaultFieldsForNewConfig();
    }

    @Override
    public NewConnector createConnector(TrelloConfig config, WebConnectorSetup setup) {
        return new TrelloConnector(config, setup);
    }

    @Override
    public Descriptor getDescriptor() {
        return new Descriptor(TrelloConnector.ID, "Trello");
    }

    @Override
    public JsonElement writeConfig(TrelloConfig config) {
        return ConfigUtils.createDefaultGson().toJsonTree(config);
    }

    @Override
    public TrelloConfig readConfig(JsonElement config) throws JsonParseException {
        return ConfigUtils.createDefaultGson()
                .fromJson(config, TrelloConfig.class);
    }

    @Override
    public TrelloConfig createDefaultConfig() {
        return new TrelloConfig();
    }
}

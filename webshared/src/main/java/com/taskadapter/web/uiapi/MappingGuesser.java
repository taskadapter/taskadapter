package com.taskadapter.web.uiapi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.taskadapter.config.StoredExportConfig;
import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.NewMappings;
import com.taskadapter.model.GTaskDescriptor;

public class MappingGuesser {
    /**
     * Guesses a new config from an old (non-updated) config.
     *
     * @param storedConfig config to update.
     * @return new mappings guessed from a previous config.
     */
    static NewMappings guessNewMappings(StoredExportConfig storedConfig) {
        final JsonObject cc1 = new JsonParser()
                .parse(storedConfig.getConnector1().getSerializedConfig())
                .getAsJsonObject().get("fieldsMapping").getAsJsonObject();
        final JsonObject cc2 = new JsonParser()
                .parse(storedConfig.getConnector2().getSerializedConfig())
                .getAsJsonObject().get("fieldsMapping").getAsJsonObject();

        /* Config is too old. Don't do anything with it */
        if (!cc1.has("selected") || !cc2.has("selected")) {
            return new NewMappings();
        }
        final JsonObject sel1 = cc1.get("selected").getAsJsonObject();
        final JsonObject sel2 = cc2.get("selected").getAsJsonObject();
        final JsonObject map1 = cc1.get("mapTo").getAsJsonObject();
        final JsonObject map2 = cc1.get("mapTo").getAsJsonObject();

        final NewMappings res = new NewMappings();

        for (GTaskDescriptor.FIELD field : GTaskDescriptor.FIELD.values()) {
            if (field == GTaskDescriptor.FIELD.ID || field == GTaskDescriptor.FIELD.REMOTE_ID) {
                continue;
            }

            final String fieldName = field.name();

            if (!sel1.has(fieldName) || !sel2.has(fieldName)) {
                continue;
            }

            /* Don't create mappings here. New mappings will be generated in
             * a fixup phase.
             */
            if (!sel1.get(fieldName).getAsBoolean()
                    || !sel2.get(fieldName).getAsBoolean()) {
                continue;
            }

            JsonElement map1FieldValue = map1.get(fieldName);
            JsonElement map2FieldValue = map2.get(fieldName);
            if (map1FieldValue == null || map1FieldValue.isJsonNull()
                    || map2FieldValue == null || map2FieldValue.isJsonNull()) {
                continue;
            }

            res.put(new FieldMapping(field, map1FieldValue.getAsString(),
                    map2FieldValue.getAsString(), true));
        }

        if (sel2.has(GTaskDescriptor.FIELD.REMOTE_ID.name())
                && sel2.get(GTaskDescriptor.FIELD.REMOTE_ID.name()).getAsBoolean()
                && !map2.get(GTaskDescriptor.FIELD.REMOTE_ID.name()).isJsonNull()) {
            res.put(new FieldMapping(GTaskDescriptor.FIELD.REMOTE_ID, null, map2.get(
                    GTaskDescriptor.FIELD.REMOTE_ID.name()).getAsString(), true));
        }

        if (sel1.has(GTaskDescriptor.FIELD.REMOTE_ID.name())
                && sel1.get(GTaskDescriptor.FIELD.REMOTE_ID.name()).getAsBoolean()
                && !map1.get(GTaskDescriptor.FIELD.REMOTE_ID.name()).isJsonNull()) {
            res.put(new FieldMapping(GTaskDescriptor.FIELD.REMOTE_ID, map1.get(
                    GTaskDescriptor.FIELD.REMOTE_ID.name()).getAsString(), null, true));
        }

        return res;
    }
}

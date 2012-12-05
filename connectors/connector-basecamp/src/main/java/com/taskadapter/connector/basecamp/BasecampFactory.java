package com.taskadapter.connector.basecamp;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.taskadapter.connector.basecamp.transport.BaseCommunicator;
import com.taskadapter.connector.basecamp.transport.ObjectAPIFactory;
import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.AvailableFieldsBuilder;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;
import static com.taskadapter.model.GTaskDescriptor.FIELD.*;

public class BasecampFactory implements PluginFactory<BasecampConfig> {

    private static final Descriptor DESCRIPTOR = new Descriptor("Basecamp",
            "Basecamp");

    private final ObjectAPIFactory factory = new ObjectAPIFactory(
            new BaseCommunicator());

    @Override
    public AvailableFields getAvailableFields() {
        final AvailableFieldsBuilder builder = AvailableFieldsBuilder.start();
        builder.addField(SUMMARY, "content");
//        builder.addField(DESCRIPTION, "content");
        builder.addField(DONE_RATIO, "done_ratio");
        builder.addField(DUE_DATE, "due_at");
        builder.addField(ASSIGNEE, "assignee");
        return builder.end();
    }

    @Override
    public Connector<BasecampConfig> createConnector(BasecampConfig config) {
        return new BasecampConnector(config, factory);
    }

    @Override
    public Descriptor getDescriptor() {
        return DESCRIPTOR;
    }

    @Override
    public JsonElement writeConfig(BasecampConfig config) {
        final JsonObject res = new JsonObject();
        res.addProperty("version", 1);
        setp(res, "label", config.getLabel());
        setp(res, "account", config.getAccountId());
        setp(res, "auth", toJson(config.getAuth()));
        setp(res, "projectKey", config.getProjectKey());
        setp(res, "todoKey", config.getTodoKey());
        return res;
    }

    private JsonElement toJson(BasecampAuth auth) {
        if (auth instanceof BasicBasecampAuth) {
            BasicBasecampAuth bba = (BasicBasecampAuth) auth;
            return toJson(bba);
        } else if (auth instanceof ApiKeyBasecampAuth) {
            ApiKeyBasecampAuth akba = (ApiKeyBasecampAuth) auth;
            return toJson(akba);
        }
        return null;
    }

    private JsonElement toJson(BasicBasecampAuth auth) {
        final JsonObject res = new JsonObject();
        setp(res, "login", auth.getLogin());
        setp(res, "password", auth.getPassword());
        setp(res, "kind", "basic");
        return res;
    }

    private JsonElement toJson(ApiKeyBasecampAuth auth) {
        final JsonObject res = new JsonObject();
        setp(res, "key", auth.getApiKey());
        setp(res, "kind", "api-key");
        return res;
    }

    @Override
    public BasecampConfig readConfig(JsonElement config)
            throws JsonParseException {
        final JsonObject obj = config.getAsJsonObject();
        final BasecampConfig res = new BasecampConfig();
        res.setLabel(getS("label", obj));
        res.setAccountId(getS("account", obj));
        res.setAuth(parseAuth(getO("auth", obj)));
        res.setProjectKey(getS("projectKey", obj));
        res.setTodoKey(getS("todoKey", obj));
        return res;
    }

    private BasecampAuth parseAuth(JsonObject o) {
        if (o == null) {
            return null;
        }
        final String kind = getS("kind", o);
        if ("basic".equals(kind)) {
            return parseBasicAuth(o);
        } else if ("api-key".equals(kind)) {
            return parseApiKeyAuth(o);
        }
        return null;
    }

    private ApiKeyBasecampAuth parseApiKeyAuth(JsonObject o) {
        final ApiKeyBasecampAuth res = new ApiKeyBasecampAuth();
        res.setApiKey(getS("key", o));
        return res;
    }

    private BasecampAuth parseBasicAuth(JsonObject o) {
        final BasicBasecampAuth res = new BasicBasecampAuth();
        res.setLogin(getS("login", o));
        res.setPassword(getS("password", o));
        return res;
    }

    @Override
    public BasecampConfig createDefaultConfig() {
        return new BasecampConfig();
    }

    private static void setp(JsonObject obj, String name, JsonElement value) {
        if (value == null) {
            return;
        }
        obj.add(name, value);
    }

    private static void setp(JsonObject obj, String name, String value) {
        if (value == null) {
            return;
        }
        obj.addProperty(name, value);
    }

    private JsonObject getO(String property, JsonObject config) {
        final JsonElement elt = config.get(property);
        if (elt == null || !elt.isJsonObject()) {
            return null;
        }
        return elt.getAsJsonObject();
    }

    private String getS(String property, JsonObject config) {
        final JsonElement elt = config.get(property);
        if (elt == null || !elt.isJsonPrimitive()) {
            return null;
        }
        return elt.getAsString();
    }
}

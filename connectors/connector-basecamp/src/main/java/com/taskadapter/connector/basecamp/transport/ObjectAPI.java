package com.taskadapter.connector.basecamp.transport;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;

public final class ObjectAPI {
    /**
     * Connection prefix.
     */
    private final String prefix;

    /**
     * User communicator.
     */
    private final Communicator communicator;

    public ObjectAPI(String userId, Communicator communicator) {
        this.prefix = "https://basecamp.com/" + userId + "api/v1/";
        this.communicator = communicator;
    }

    /**
     * Gets a json object.
     * 
     * @param suffix
     *            object suffix.
     * @return returned object.
     */
    public JSONObject getObject(String suffix) throws ConnectorException {
        final HttpGet get = new HttpGet(prefix + suffix);
        final BasicHttpResponse resp = communicator.sendRequest(get);
        if (resp.getResponseCode() != 200) {
            throw new CommunicationException("Unexpected error code "
                    + resp.getResponseCode() + " : " + resp.getContent());
        }
        try {
            return new JSONObject(resp.getContent());
        } catch (JSONException e) {
            throw new CommunicationException("Unexpected content "
                    + resp.getContent());
        }
    }

    public JSONArray getObjects(String suffix) throws ConnectorException {
        final HttpGet get = new HttpGet(prefix + suffix);
        final BasicHttpResponse resp = communicator.sendRequest(get);
        if (resp.getResponseCode() != 200) {
            throw new CommunicationException("Unexpected error code "
                    + resp.getResponseCode() + " : " + resp.getContent());
        }
        try {
            return new JSONArray(resp.getContent());
        } catch (JSONException e) {
            throw new CommunicationException("Unexpected content "
                    + resp.getContent());
        }
    }

    public JSONObject post(String suffix, String content)
            throws ConnectorException {
        throw new ConnectorException();
    }

    public JSONObject put(String suffix, String content)
            throws ConnectorException {
        throw new ConnectorException();
    }

}

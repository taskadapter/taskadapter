package com.taskadapter.connector.basecamp.classic.transport;

import com.taskadapter.connector.basecamp.classic.BasecampConfig;
import com.taskadapter.connector.basecamp.classic.BasecampUtils;
import com.taskadapter.connector.basecamp.classic.transport.throttling.IntervalThrottler;
import com.taskadapter.connector.definition.exceptions.ConnectorException;

public final class ObjectAPIFactory {

    private static final int THROTTLING_TIMEOUT_MILLIS = 25;
    private static final String AUTH_KEY_PASSWORD = "X";

    private final Communicator baseTransport;

    private volatile ObjectAPIHolder oah;

    public ObjectAPIFactory(Communicator baseTransport) {
        this.baseTransport = baseTransport;
    }

    public ObjectAPI createObjectAPI(BasecampConfig config)
            throws ConnectorException {
        BasecampUtils.validateServerAuth(config);

        final ObjectAPIHolder oldApi = oah;
        final String serverUrl = config.getServerUrl();
        final String authKey = config.getApiKey();
        if (oldApi != null && oldApi.apiUrl.equals(serverUrl)
                && oldApi.authKey.equals(authKey))
            return oldApi.api;

        final ObjectAPIHolder newApi = new ObjectAPIHolder(serverUrl, authKey,
                createApi(serverUrl, authKey));
        oah = newApi;
        return newApi.api;

    }

    private ObjectAPI createApi(String url, String apiKey)
            throws ConnectorException {
        final Communicator authComm = new BasicAuthenticator(baseTransport,
                apiKey, AUTH_KEY_PASSWORD);
        final Communicator throller = new ThrottlingCommunicator(authComm,
                new IntervalThrottler(THROTTLING_TIMEOUT_MILLIS));
        final ObjectAPI api = new ObjectAPI(url, throller);
        return api;
    }
}

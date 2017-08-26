package com.taskadapter.connector.basecamp.classic.transport;

import com.taskadapter.connector.basecamp.classic.BasecampConfigValidator;
import com.taskadapter.connector.basecamp.transport.BasicAuthenticator;
import com.taskadapter.connector.basecamp.transport.Communicator;
import com.taskadapter.connector.basecamp.transport.ThrottlingCommunicator;
import com.taskadapter.connector.basecamp.transport.throttling.IntervalThrottler;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;

public final class ObjectAPIFactory {

    private static final int THROTTLING_TIMEOUT_MILLIS = 25;
    private static final String AUTH_KEY_PASSWORD = "X";

    private final Communicator baseTransport;

    private volatile ObjectAPIHolder oah;

    public ObjectAPIFactory(Communicator baseTransport) {
        this.baseTransport = baseTransport;
    }

    public ObjectAPI createObjectAPI(WebConnectorSetup setup) throws ConnectorException {
        BasecampConfigValidator.validateServerAuth(setup);

        final ObjectAPIHolder oldApi = oah;
        final String serverUrl = setup.host();
        final String authKey = setup.apiKey();
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
        return new ObjectAPI(url, throller);
    }
}

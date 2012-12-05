package com.taskadapter.connector.basecamp.transport;

import com.taskadapter.connector.basecamp.ApiKeyBasecampAuth;
import com.taskadapter.connector.basecamp.BasecampAuth;
import com.taskadapter.connector.basecamp.BasecampConfig;
import com.taskadapter.connector.basecamp.BasicBasecampAuth;
import com.taskadapter.connector.basecamp.exceptions.FieldNotSetException;
import com.taskadapter.connector.basecamp.transport.throttling.IntervalThrottler;
import com.taskadapter.connector.definition.exceptions.ConnectorException;

public final class ObjectAPIFactory {

    private final int THROTTLING_TIMEOUT_MILLIS = 25;
    private final Communicator baseTransport;
    private volatile ObjectAPIHolder holder;
    private static final String AUTH_KEY_PASSWORD = "X";

    public ObjectAPIFactory(Communicator baseTransport) {
        this.baseTransport = baseTransport;
    }

    public ObjectAPI createObjectAPI(BasecampConfig config)
            throws ConnectorException {
        final String userId = config.getAccountId();
        if (userId == null) {
            throw new FieldNotSetException("user-id");
        }
        final BasecampAuth auth = config.getAuth();
        if (auth == null) {
            throw new FieldNotSetException("auth");
        }
        final ObjectAPIHolder cached = holder;
        if (holder != null && holder.userId.equals(userId)
                && holder.accepts(auth)) {
            return cached.api;
        }
        final ObjectAPIHolder created = createHolder(userId, auth);
        holder = created;
        return created.api;

    }

    private ObjectAPIHolder createHolder(String userId, BasecampAuth auth)
            throws ConnectorException {
        if (auth instanceof BasicBasecampAuth) {
            final BasicBasecampAuth bba = (BasicBasecampAuth) auth;
            final Communicator authComm = new BasicAuthenticator(baseTransport,
                    bba.getLogin(), bba.getPassword());
            final Communicator troller = new ThrottlingCommunicator(authComm,
                    new IntervalThrottler(THROTTLING_TIMEOUT_MILLIS));
            final ObjectAPI api = new ObjectAPI(userId, troller);
            return new BasicAuthAPIholder(api, userId, bba);
        } else if (auth instanceof ApiKeyBasecampAuth) {
            final ApiKeyBasecampAuth akba = (ApiKeyBasecampAuth) auth;
            final Communicator authComm = new BasicAuthenticator(baseTransport,
                    akba.getApiKey(), AUTH_KEY_PASSWORD);
            final Communicator troller = new ThrottlingCommunicator(authComm,
                    new IntervalThrottler(THROTTLING_TIMEOUT_MILLIS));
            final ObjectAPI api = new ObjectAPI(userId, troller);

            final BasicBasecampAuth dummy = new BasicBasecampAuth();
            dummy.setLogin(akba.getApiKey());
            dummy.setPassword(AUTH_KEY_PASSWORD);

            return new BasicAuthAPIholder(api, userId, dummy);
        }
        throw new FieldNotSetException("auth");
    }
}

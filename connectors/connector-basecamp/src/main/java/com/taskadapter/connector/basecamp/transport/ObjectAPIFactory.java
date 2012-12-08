package com.taskadapter.connector.basecamp.transport;

import com.taskadapter.connector.basecamp.BasecampAuth;
import com.taskadapter.connector.basecamp.BasecampConfig;
import com.taskadapter.connector.basecamp.BasecampUtils;
import com.taskadapter.connector.basecamp.exceptions.FieldNotSetException;
import com.taskadapter.connector.basecamp.transport.throttling.IntervalThrottler;
import com.taskadapter.connector.definition.exceptions.ConnectorException;

public final class ObjectAPIFactory {

    private static final int THROTTLING_TIMEOUT_MILLIS = 25;
    private static final String AUTH_KEY_PASSWORD = "X";

    private final Communicator baseTransport;
    private volatile ObjectAPIHolder holder;

    public ObjectAPIFactory(Communicator baseTransport) {
        this.baseTransport = baseTransport;
    }

    public ObjectAPI createObjectAPI(BasecampConfig config)
            throws ConnectorException {
        BasecampUtils.validateAccount(config);
        
        final String accountId = config.getAccountId();

        final BasecampAuth auth = config.getAuth();
        if (auth == null) {
            throw new FieldNotSetException("auth");
        }
        final ObjectAPIHolder cached = holder;
        if (holder != null && holder.userId.equals(accountId)
                && holder.accepts(auth)) {
            return cached.api;
        }
        final ObjectAPIHolder created = createHolder(accountId, auth);
        holder = created;
        return created.api;

    }

    private ObjectAPIHolder createHolder(String userId, BasecampAuth auth)
            throws ConnectorException {
        if (auth.isUseAPIKeyInsteadOfLoginPassword()) {
            final Communicator authComm = new BasicAuthenticator(baseTransport,
                    auth.getApiKey(), AUTH_KEY_PASSWORD);
            final Communicator throller = new ThrottlingCommunicator(authComm,
                    new IntervalThrottler(THROTTLING_TIMEOUT_MILLIS));
            final ObjectAPI api = new ObjectAPI(userId, throller);

            final BasecampAuth dummy = new BasecampAuth();
            dummy.setLogin(auth.getApiKey());
            dummy.setPassword(AUTH_KEY_PASSWORD);

            return new BasicAuthAPIholder(api, userId, dummy);
        } else {
            final Communicator authComm = new BasicAuthenticator(baseTransport,
                    auth.getLogin(), auth.getPassword());
            final Communicator throller = new ThrottlingCommunicator(authComm,
                    new IntervalThrottler(THROTTLING_TIMEOUT_MILLIS));
            final ObjectAPI api = new ObjectAPI(userId, throller);
            return new BasicAuthAPIholder(api, userId, auth);
        }
    }
}

package com.taskadapter.connector.basecamp.transport;

import com.taskadapter.connector.basecamp.BasecampConfig;
import com.taskadapter.connector.basecamp.BasecampUtils;
import com.taskadapter.connector.basecamp.transport.throttling.IntervalThrottler;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;

public final class ObjectAPIFactory {

    private static final int THROTTLING_TIMEOUT_MILLIS = 25;
    private static final String AUTH_KEY_PASSWORD = "X";

    private final Communicator baseTransport;
    private volatile ObjectAPIHolder holder;

    public ObjectAPIFactory(Communicator baseTransport) {
        this.baseTransport = baseTransport;
    }

    public ObjectAPI createObjectAPI(BasecampConfig config, WebConnectorSetup setup)
            throws ConnectorException {
        BasecampUtils.validateAccount(config);
        
        final String accountId = config.getAccountId();

        final ObjectAPIHolder cached = holder;
        if (holder != null && holder.userId.equals(accountId)
                && holder.accepts(setup)) {
            return cached.api;
        }
        final ObjectAPIHolder created = createHolder(accountId, setup);
        holder = created;
        return created.api;

    }

    private ObjectAPIHolder createHolder(String userId, WebConnectorSetup setup)
            throws ConnectorException {
        if (setup.useApiKey()) {
            final Communicator authComm = new BasicAuthenticator(baseTransport,setup.apiKey(), AUTH_KEY_PASSWORD);
            final Communicator throttler = new ThrottlingCommunicator(authComm, new IntervalThrottler(THROTTLING_TIMEOUT_MILLIS));
            final ObjectAPI api = new ObjectAPI(userId, throttler);

//            final BasecampAuth dummy = new BasecampAuth();
//            dummy.setLogin(setup.getApiKey());
//            dummy.setPassword(AUTH_KEY_PASSWORD);

            WebConnectorSetup dummy = new WebConnectorSetup(setup.connectorId(), setup.id(), setup.label(), setup.host(),
                    setup.apiKey(), AUTH_KEY_PASSWORD, false, "");

            return new BasicAuthAPIholder(api, userId, dummy);
        } else {
            final Communicator authComm = new BasicAuthenticator(baseTransport, setup.userName(), setup.password());
            final Communicator throttler = new ThrottlingCommunicator(authComm, new IntervalThrottler(THROTTLING_TIMEOUT_MILLIS));
            final ObjectAPI api = new ObjectAPI(userId, throttler);
            return new BasicAuthAPIholder(api, userId, setup);
        }
    }
}

package com.taskadapter.auth;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taskadapter.auth.check.AuthFactory;
import com.taskadapter.auth.check.HMacAuthenticator;
import com.taskadapter.auth.cred.Credentials;
import com.taskadapter.auth.cred.CredentialsStore;
import com.taskadapter.auth.cred.CredentialsV0;
import com.taskadapter.auth.cred.CredentialsV1;

/**
 * Basic credentials manager.
 * 
 */
public final class BasicCredentialsManager implements CredentialsManager {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(BasicCredentialsManager.class);

    /**
     * Random characters.
     */
    private static final String RND_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKMLNOPQRSTUVWXYZ";

    private static final Map<String, AuthFactory<String, String>> AUTHENTICATORS = new HashMap<String, AuthFactory<String, String>>();

    /**
     * Default authenticators.
     */
    private static final String DEFAULT_AUTH_NAME = "AUHMACSHA1";

    static {
        AUTHENTICATORS
                .put(DEFAULT_AUTH_NAME, new HMacAuthenticator("HmacSHA1"));
    }

    /**
     * Credentials store to use.
     */
    private final CredentialsStore store;

    /**
     * Secondary authentication backlog.
     */
    private final int secondaryAuthBacklog;

    /**
     * Creates a new authentication manager. No more than
     * <code>secondaryAuthBacklog</code> secondary tokens can be used for
     * secondary auth. Least-recently used secondary tokens are automatically
     * deleted.
     * 
     * @param store
     *            credentials store.
     * @param secondaryAuthBacklog
     *            secondary authentication backlog size.
     */
    public BasicCredentialsManager(CredentialsStore store,
            int secondaryAuthBacklog) {
        this.store = store;
        this.secondaryAuthBacklog = secondaryAuthBacklog;
    }

    @Override
    public boolean isSecondaryAuthentic(String user, String auth) {
        final int prefixIdx = auth.indexOf(':');
        if (prefixIdx <= 0) {
            return false;
        }

        final String authShort = auth.substring(0, prefixIdx);
        final String authKey = auth.substring(prefixIdx + 1);

        final CredentialsV1 v1creds;
        try {
            v1creds = loadV1(user);
        } catch (AuthException e1) {
            return false;
        }

        final List<String> credKeys = v1creds.secondaryCredentials;
        int goodKey = -1;
        for (int i = 0; i < credKeys.size(); i++) {
            if (isGoodSecondary(authShort, authKey, credKeys.get(i))) {
                goodKey = i;
                break;
            }
        }

        /* Key not found, bad credentinals */
        if (goodKey < 0) {
            return false;
        }

        if (goodKey == credKeys.size() - 1) {
            return true;
        }

        /* Update order of credentials */
        final List<String> newOrder = new ArrayList<String>(credKeys.size());
        for (int i = 0; i < credKeys.size(); i++) {
            if (i != goodKey) {
                newOrder.add(credKeys.get(i));
            }
        }
        newOrder.add(credKeys.get(goodKey));
        try {
            store.saveCredentials(user, new CredentialsV1(
                    v1creds.primaryCredentials, newOrder));
        } catch (AuthException e) {
            LOGGER.info("Error!", e);
        }
        return true;
    }

    private boolean isGoodSecondary(String authShort, String authKey,
            String akey) {
        final int asep = akey.indexOf(':');
        if (asep <= 0) {
            return false;
        }
        final String akey1 = akey.substring(0, asep);
        if (!authShort.equals(akey1)) {
            return false;
        }
        return matchKeys(authKey, akey.substring(asep + 1));
    }

    private boolean matchKeys(String userKey, String localKey) {
        final int asep = localKey.indexOf(':');
        if (asep <= 0) {
            return false;
        }
        final String algoId = localKey.substring(0, asep);
        final AuthFactory<String, String> authFactory = AUTHENTICATORS
                .get(algoId);
        if (authFactory == null) {
            return false;
        }
        return authFactory.isAuthentic(userKey, localKey.substring(asep + 1));
    }

    @Override
    public boolean isPrimaryAuthentic(String user, String auth) {
        final CredentialsV1 creds;
        try {
            creds = loadV1(user);
        } catch (AuthException e) {
            return false;
        }
        return matchKeys(auth, creds.primaryCredentials);
    }

    private CredentialsV1 loadV1(String user) throws AuthException {
        Credentials creds = store.loadCredentials(user);
        if (creds instanceof CredentialsV0) {
            final CredentialsV0 oldCreds = (CredentialsV0) creds;
            setPrimaryAuthToken(user, oldCreds.password);
            creds = store.loadCredentials(user);
        }
        if (!(creds instanceof CredentialsV1)) {
            throw new AuthException(
                    "Interal conversion exception : no conversion");
        }
        return (CredentialsV1) creds;
    }

    @Override
    public String generateSecondaryAuth(String user, String primaryAuth)
            throws AuthException {
        final CredentialsV1 creds;
        try {
            creds = loadV1(user);
        } catch (AuthException e) {
            return null;
        }

        if (!matchKeys(primaryAuth, creds.primaryCredentials)) {
            return null;
        }

        final SecureRandom rnd = new SecureRandom();
        final String idStr = getString(rnd, 12);
        final String keyStr = getString(rnd, 24);

        final AuthFactory<String, String> auth = AUTHENTICATORS
                .get(DEFAULT_AUTH_NAME);
        final String authData = auth.generateAuth(keyStr);
        final String tok = idStr + ":" + keyStr;
        final String key = idStr + ":" + DEFAULT_AUTH_NAME + ":" + authData;

        final int saveLength = Math.max(Math.min(creds.secondaryCredentials.size(),
                secondaryAuthBacklog) - 1, 0);
        final List<String> auths = new ArrayList<String>(
                creds.secondaryCredentials.subList(
                        creds.secondaryCredentials.size() - saveLength,
                        creds.secondaryCredentials.size()));
        auths.add(key);
        store.saveCredentials(user, new CredentialsV1(creds.primaryCredentials,
                auths));
        return tok;
    }

    private String getString(SecureRandom rnd, int len) {
        final int charLen = RND_CHARS.length();
        final char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            chars[i] = RND_CHARS.charAt(rnd.nextInt(charLen));
        }
        return new String(chars);
    }

    @Override
    public void destroySecondaryAuthToken(String user, String secondaryAuth)
            throws AuthException {

        final int prefixIdx = secondaryAuth.indexOf(':');
        if (prefixIdx <= 0) {
            return;
        }

        final String authShort = secondaryAuth.substring(0, prefixIdx);
        final String authKey = secondaryAuth.substring(prefixIdx + 1);

        final CredentialsV1 creds = loadV1(user);
        final List<String> newToks = new ArrayList<String>(
                creds.secondaryCredentials.size());

        for (String tok : creds.secondaryCredentials) {
            if (!isGoodSecondary(authShort, authKey, tok)) {
                newToks.add(authKey);
            }
        }

        if (newToks.size() != creds.secondaryCredentials.size()) {
            store.saveCredentials(user, new CredentialsV1(
                    creds.primaryCredentials, newToks));
        }
    }

    @Override
    public void setPrimaryAuthToken(String user, String newToken)
            throws AuthException {
        final AuthFactory<String, String> afactory = AUTHENTICATORS
                .get(DEFAULT_AUTH_NAME);
        store.saveCredentials(
                user,
                new CredentialsV1(DEFAULT_AUTH_NAME + ":"
                        + afactory.generateAuth(newToken), Collections
                        .<String> emptyList()));
    }

    @Override
    public void destroyAllSecondaryTokens(String user) throws AuthException {
        final CredentialsV1 creds = loadV1(user);
        store.saveCredentials(user, new CredentialsV1(creds.primaryCredentials,
                Collections.<String> emptyList()));
    }

}

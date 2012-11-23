package com.taskadapter.web.service;

import com.taskadapter.auth.AuthException;
import com.taskadapter.auth.CredentialsManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Authenticator {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(Authenticator.class);
    private static final String PERM_AUTH_KEY_COOKIE_NAME = "sPermAuth";
    private static final String PERM_AUTH_USER_COOKIE_NAME = "sPermUser";

    private final CookiesManager cookiesManager;
    private final CredentialsManager credentialsManager;
    private final EditableCurrentUserInfo currentUserInfo;

    public Authenticator(CredentialsManager credentialsManager,
            CookiesManager cookiesManager, EditableCurrentUserInfo currentUserInfo) {
        this.credentialsManager = credentialsManager;
        this.cookiesManager = cookiesManager;
        this.currentUserInfo = currentUserInfo;

        final String ucookie = cookiesManager
                .getCookie(PERM_AUTH_USER_COOKIE_NAME);
        final String kcookie = cookiesManager
                .getCookie(PERM_AUTH_KEY_COOKIE_NAME);

        if (ucookie != null && kcookie != null
                && credentialsManager.isSecondaryAuthentic(ucookie, kcookie)) {
            currentUserInfo.setUserName(ucookie);
        }
    }

    public void tryLogin(String userName, String password, boolean staySigned)
            throws WrongPasswordException {

        /* Simple authentication process */
        if (!staySigned) {
            if (!credentialsManager.isPrimaryAuthentic(userName, password)) {
                throw new WrongPasswordException();
            }
            currentUserInfo.setUserName(userName);
            return;
        }
        /* Complex authentication with a long-living cookies */
        String permatok;
        try {
            permatok = credentialsManager.generateSecondaryAuth(userName,
                    password);
        } catch (AuthException e) {
            LOGGER.info("Error!", e);
            permatok = null;
        }

        if (permatok == null) {
            throw new WrongPasswordException();
        }

        cookiesManager.setCookie(PERM_AUTH_USER_COOKIE_NAME, userName);
        cookiesManager.setCookie(PERM_AUTH_KEY_COOKIE_NAME, permatok);

        currentUserInfo.setUserName(userName);
    }

    public void checkUserPassword(String userName, String password)
            throws UserNotFoundException, WrongPasswordException {
        if (!credentialsManager.isPrimaryAuthentic(userName, password)) {
            throw new WrongPasswordException();
        }
    }

    public void logout() {
        final String ucookie = cookiesManager
                .getCookie(PERM_AUTH_USER_COOKIE_NAME);
        final String kcookie = cookiesManager
                .getCookie(PERM_AUTH_KEY_COOKIE_NAME);

        if (ucookie != null && kcookie != null) {
            try {
                credentialsManager.destroySecondaryAuthToken(ucookie, kcookie);
            } catch (AuthException e) {
                LOGGER.error("Failed to clean secondary auth token!", e);
            }
        }
        cookiesManager.expireCookie(PERM_AUTH_USER_COOKIE_NAME);
        cookiesManager.expireCookie(PERM_AUTH_KEY_COOKIE_NAME);

        currentUserInfo.setUserName(null);
    }

}

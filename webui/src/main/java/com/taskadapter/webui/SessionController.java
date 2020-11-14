package com.taskadapter.webui;

import com.taskadapter.auth.AuthException;
import com.taskadapter.auth.AuthorizedOperations;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.auth.SecondarizationResult;
import com.taskadapter.webui.pageset.LoggedInPageset;
import com.taskadapter.webui.service.Preservices;
import com.taskadapter.webui.service.WrongPasswordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Controller for one (and only one!) user session.
 */
public final class SessionController {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SessionController.class);
    private static final String PERM_AUTH_KEY_COOKIE_NAME = "sPermAuth";
    private static final String PERM_AUTH_USER_COOKIE_NAME = "sPermUser";
    /**
     * Used services.
     */
    private final Preservices services;

    /**
     * User credentials manager.
     */
    private final CredentialsManager credentialsManager;

    /**
     * Active session.
     */
    private final WebUserSession session;

    private SessionController(Preservices services,
                              WebUserSession session) {
        this.services = services;
        this.credentialsManager = services.credentialsManager;
        this.session = session;
    }

    /**
     * Initializes a new session.
     */
    private void initSession() {
        final String ucookie = CookiesManager
                .getCookie(PERM_AUTH_USER_COOKIE_NAME);
        final String kcookie = CookiesManager
                .getCookie(PERM_AUTH_KEY_COOKIE_NAME);

        if (ucookie == null || kcookie == null) {
            showLogin();
            return;
        }
        final AuthorizedOperations ops = credentialsManager
                .authenticateSecondary(ucookie, kcookie);
        if (ops == null)
            showLogin();
        else
            showUserHome(ucookie, ops);
    }

    /**
     * Shows a common interface (for logged-in user).
     */
    private void showUserHome(String login, AuthorizedOperations ops) {

        final SelfManagement selfManagement = new SelfManagement(login,
                credentialsManager);
        File userHomeFolder = new File(services.rootDir, login);
        final ConfigOperations configOps = new ConfigOperations(login, ops,
                credentialsManager,
                services.uiConfigStore,
                new File(userHomeFolder, "files"));
        final UserContext ctx = new UserContext(login, selfManagement, ops,
                configOps);

        session.pageContainer.setPageContent(LoggedInPageset.createPageset(
                credentialsManager, services, session.tracker, ctx,
                this::doLogout));
    }

    /**
     * Shows a login page.
     */
    private void showLogin() {
        session.pageContainer.setPageContent(WelcomePageset.createPageset(
                services, session.tracker, this::tryAuth));
    }

    /**
     * Performs a logout.
     */
    private void doLogout() {
        final String ucookie = CookiesManager
                .getCookie(PERM_AUTH_USER_COOKIE_NAME);
        final String kcookie = CookiesManager
                .getCookie(PERM_AUTH_KEY_COOKIE_NAME);

        if (ucookie != null && kcookie != null) {
            try {
                credentialsManager.destroySecondaryAuthToken(ucookie, kcookie);
            } catch (AuthException e) {
                LOGGER.error("Failed to clean secondary auth token!", e);
            }
        }
        CookiesManager.expireCookie(PERM_AUTH_USER_COOKIE_NAME);
        CookiesManager.expireCookie(PERM_AUTH_KEY_COOKIE_NAME);
        showLogin();
    }

    /**
     * Attempts to authorize user.
     *
     * @param login               user login.
     * @param password            user password.
     * @param createSecondaryAuth secondary authentication data.
     * @throws WrongPasswordException if login or password is wrong.
     */
    private void tryAuth(String login, String password, boolean createSecondaryAuth) throws WrongPasswordException {

        if (!createSecondaryAuth) {
            final AuthorizedOperations ops = credentialsManager
                    .authenticatePrimary(login, password);
            if (ops == null) {
                throw new WrongPasswordException();
            }
            showUserHome(login, ops);
            return;
        }

        /* Complex authentication with a long-living cookies */
        SecondarizationResult supResult;
        try {
            supResult = credentialsManager.generateSecondaryAuth(login,
                    password);
        } catch (AuthException e) {
            LOGGER.info("Error!", e);
            supResult = null;
        }

        if (supResult == null) {
            throw new WrongPasswordException();
        }

        CookiesManager.setCookie(PERM_AUTH_USER_COOKIE_NAME, login);
        CookiesManager.setCookie(PERM_AUTH_KEY_COOKIE_NAME,
                supResult.secondaryToken);

        showUserHome(login, supResult.ops);
    }

    /**
     * Manages a user session.
     *
     * @param services general services, like configs storage, credentials storage, etc.
     * @param session  provided web session.
     */
    public static void manageSession(Preservices services, WebUserSession session) {
        final SessionController ctl = new SessionController(services, session);
        ctl.initSession();
    }
}

package com.taskadapter.webui;

import com.taskadapter.auth.AuthException;
import com.taskadapter.auth.AuthorizedOperations;
import com.taskadapter.auth.AuthorizedOperationsImpl;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.auth.SecondarizationResult;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.webui.auth.PermissionViolationException;
import com.taskadapter.webui.config.ApplicationSettings;
import com.taskadapter.webui.pageset.LoggedInPageset;
import com.taskadapter.webui.service.EditorManager;
import com.taskadapter.webui.service.Preservices;
import com.taskadapter.webui.service.WrongPasswordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;

import java.io.File;

/**
 * Controller for one (and only one!) user session.
 */
public final class SessionController {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SessionController.class);
    private static final String PERM_AUTH_KEY_COOKIE_NAME = "sPermAuth";
    private static final String PERM_AUTH_USER_COOKIE_NAME = "sPermUser";

    // Application config root folder.
    private static File rootFolder = ApplicationSettings.getDefaultRootFolder();

    private static final Preservices services = new Preservices(rootFolder, EditorManager.fromResource("editors.txt"));
    private static final CredentialsManager credentialsManager = services.credentialsManager;

    private static Tracker tracker;

    private static WebUserSession session;

    public static void initSession(WebUserSession newSession, Tracker providedTracker) {
        session = newSession;
        tracker = providedTracker;

        final String ucookie = CookiesManager
                .getCookie(PERM_AUTH_USER_COOKIE_NAME);
        final String kcookie = CookiesManager
                .getCookie(PERM_AUTH_KEY_COOKIE_NAME);

        if (ucookie == null || kcookie == null) {
            showLogin();
            return;
        }
        final AuthorizedOperations ops = services.credentialsManager
                .authenticateSecondary(ucookie, kcookie);
        if (ops == null)
            showLogin();
        else
            showUserHome(ucookie, ops);
    }

    /**
     * Shows a common interface (for logged-in user).
     */
    private static void showUserHome(String login, AuthorizedOperations ops) {
        session.setCurrentUserName(login);

        final SelfManagement selfManagement = new SelfManagement(login,
                credentialsManager);
        File userHomeFolder = new File(services.rootDir, login);
        final ConfigOperations configOps = new ConfigOperations(login, ops,
                credentialsManager,
                services.uiConfigStore,
                new File(userHomeFolder, "files"));
        final UserContext ctx = new UserContext(login, selfManagement, ops,
                configOps);

        session.pageContainer().setPageContent(LoggedInPageset.createPageset(
                credentialsManager, services, ctx,
                SessionController::doLogout));
    }

    /**
     * Shows a login page.
     */
    private static void showLogin() {
        session.pageContainer().setPageContent(WelcomePageset.createPageset(
                services, SessionController::tryAuth));
    }

    /**
     * Performs a logout.
     */
    private static void doLogout() {
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
    private static void tryAuth(String login, String password, boolean createSecondaryAuth) throws WrongPasswordException {

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

    public static void tmpLoginAsAdmin() {
        session.setCurrentUserName("admin");
    }

    public static String getCurrentUserName() {
        return session.getCurrentUserName().getOrElse(() -> "");
    }

    public static Sandbox createSandbox() {
        ConfigOperations configOperations = buildConfigOperations();
        return new Sandbox(services.settingsManager.isTAWorkingOnLocalMachine(), configOperations.syncSandbox());
    }

    public static ConfigOperations buildConfigOperations() {
        Option<String> currentUserName = session.getCurrentUserName();
        if (currentUserName.isEmpty()) {
            throw new PermissionViolationException();
        }
        File userHomeFolder = new File(services.rootDir, currentUserName.get());
        AuthorizedOperations authorizedOperations = new AuthorizedOperationsImpl(getCurrentUserName());
        return new ConfigOperations(currentUserName.get(),
                authorizedOperations,
                services.credentialsManager,
                services.uiConfigStore,
                new File(userHomeFolder, "files"));
    }

    public static Preservices getServices() {
        return services;
    }

    public static Tracker getTracker() {
        return tracker;
    }
}

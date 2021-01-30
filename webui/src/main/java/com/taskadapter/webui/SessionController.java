package com.taskadapter.webui;

import com.google.common.base.Strings;
import com.taskadapter.auth.AuthException;
import com.taskadapter.auth.AuthorizedOperations;
import com.taskadapter.auth.AuthorizedOperationsImpl;
import com.taskadapter.auth.SecondarizationResult;
import com.taskadapter.schedule.ScheduleRunner;
import com.taskadapter.web.event.EventBusImpl;
import com.taskadapter.web.event.SchedulerStatusChanged;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.webui.auth.PermissionViolationException;
import com.taskadapter.webui.config.ApplicationSettings;
import com.taskadapter.webui.service.EditorManager;
import com.taskadapter.webui.service.Preservices;
import com.taskadapter.webui.service.WrongPasswordException;
import com.vaadin.flow.component.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.lang.scala.Subscriber;
import scala.Option;

import java.io.File;
import java.util.Optional;

/**
 * Controller for a single user session.
 */
public final class SessionController {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SessionController.class);
    private static final String PERM_AUTH_KEY_COOKIE_NAME = "sPermAuth";
    private static final String PERM_AUTH_USER_COOKIE_NAME = "sPermUser";

    // Application config root folder.
    private static File rootFolder = ApplicationSettings.getDefaultRootFolder();

    private static final Preservices services = new Preservices(rootFolder, EditorManager.fromResource("editors.txt"));

    // storing the init code for the Vaadin 14 app here for now
    static {
        services.licenseManager.loadInstalledTaskAdapterLicense();

        // TODO 14 this class is also called from project tests. scheduler should not be a part of this
        ScheduleRunner scheduleRunner = new ScheduleRunner(services.uiConfigStore, services.schedulesStorage,
                services.exportResultStorage, services.settingsManager);
        EventBusImpl.observable(SchedulerStatusChanged.class)
                .subscribe(new Subscriber<SchedulerStatusChanged>() {
                    @Override
                    public void onNext(SchedulerStatusChanged value) {
                        if (value.schedulerEnabled()) {
                            scheduleRunner.start();
                        } else {
                            scheduleRunner.stop();
                        }
                    }
                });
        boolean schedulerEnabled = services.settingsManager.schedulerEnabled();
        if (schedulerEnabled) {
            EventBusImpl.post(new SchedulerStatusChanged(schedulerEnabled));
        }
    }

    private static Tracker tracker = new NoOpGATracker();

    private static WebUserSession session = new WebUserSession();

    public static void initSession(WebUserSession newSession) {
        session = newSession;

        final String ucookie = CookiesManager
                .getCookie(PERM_AUTH_USER_COOKIE_NAME);
        final String kcookie = CookiesManager
                .getCookie(PERM_AUTH_KEY_COOKIE_NAME);

//        if (ucookie == null || kcookie == null) {
//            showLogin();
//            return;
//        }
//        final AuthorizedOperations ops = services.credentialsManager
//                .authenticateSecondary(ucookie, kcookie);
//        if (ops == null)
//            showLogin();
//        else
//            showUserHome(ucookie);

        registerLoggedInListeners();
    }

    private static void registerLoggedInListeners() {
        // TODO maybe use this for some logged-in session listeners?
    }

    public static UserContext getUserContext() {
        String login = getCurrentUserName();
        AuthorizedOperationsImpl ops = new AuthorizedOperationsImpl(login);
        final SelfManagement selfManagement = new SelfManagement(login,
                services.credentialsManager);

        final ConfigOperations configOps = buildConfigOperations();

        return new UserContext(login, selfManagement, ops,
                configOps);
    }

    public static void logout(){
        final String ucookie = CookiesManager
                .getCookie(PERM_AUTH_USER_COOKIE_NAME);
        final String kcookie = CookiesManager
                .getCookie(PERM_AUTH_KEY_COOKIE_NAME);

        if (ucookie != null && kcookie != null) {
            try {
                services.credentialsManager.destroySecondaryAuthToken(ucookie, kcookie);
            } catch (AuthException e) {
                LOGGER.error("Failed to clean secondary auth token!", e);
            }
        }
        CookiesManager.expireCookie(PERM_AUTH_USER_COOKIE_NAME);
        CookiesManager.expireCookie(PERM_AUTH_KEY_COOKIE_NAME);
        session.clear();
        UI.getCurrent().navigate("");
    }

    /**
     * Attempts to authorize user.
     *
     * @param login               user login.
     * @param password            user password.
     * @param createSecondaryAuth secondary authentication data.
     * @throws WrongPasswordException if login or password is wrong.
     */
    public static void tryAuth(String login, String password, boolean createSecondaryAuth) throws WrongPasswordException {

        if (!createSecondaryAuth) {
            final AuthorizedOperations ops = services.credentialsManager
                    .authenticatePrimary(login, password);
            if (ops == null) {
                throw new WrongPasswordException();
            }
            return;
        }

        /* Complex authentication with long-living cookies */
        SecondarizationResult supResult;
        try {
            supResult = services.credentialsManager.generateSecondaryAuth(login,
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
    }

    public static String getCurrentUserName() {
        return session.getCurrentUserName().orElse("");
    }

    public static boolean userIsLoggedIn() {
        return !Strings.isNullOrEmpty(getCurrentUserName());
    }

    public static Sandbox createSandbox() {
        ConfigOperations configOperations = buildConfigOperations();
        return new Sandbox(services.settingsManager.isTAWorkingOnLocalMachine(), configOperations.syncSandbox());
    }

    public static ConfigOperations buildConfigOperations() {
        Optional<String> currentUserName = session.getCurrentUserName();
        if (!currentUserName.isPresent()) {
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

    public static void setTracker(Tracker newTracker) {
        tracker = newTracker;
    }
}

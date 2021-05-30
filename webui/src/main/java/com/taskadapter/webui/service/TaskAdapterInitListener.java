package com.taskadapter.webui.service;

import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.ConfigBuilder;
import com.taskadapter.app.GoogleAnalyticsFactory;
import com.taskadapter.app.ProdModeDetector;
import com.taskadapter.reporting.ErrorReporter;
import com.taskadapter.reporting.NoOpErrorReporter;
import com.taskadapter.reporting.RollbarErrorReporter;
import com.taskadapter.web.event.ApplicationActionEvent;
import com.taskadapter.web.event.ApplicationActionEventWithValue;
import com.taskadapter.web.event.EventBusImpl;
import com.taskadapter.web.event.NoOpGATracker;
import com.taskadapter.web.event.Tracker;
import com.taskadapter.webui.MyCustomErrorHandler;
import com.taskadapter.webui.SessionController;
import com.taskadapter.webui.WebUserSession;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * init class for the app.
 * <p>
 * this class is called when a vaadin session is first created. this class is referenced in
 * META-INF/services/com.vaadin.flow.server.VaadinServiceInitListener file and thus is picked by via Java SPI mechanism.
 */
public class TaskAdapterInitListener implements VaadinServiceInitListener {
    private static final Logger logger = LoggerFactory.getLogger(TaskAdapterInitListener.class);

    @Override
    public void serviceInit(ServiceInitEvent event) {
        logger.info("Starting TaskAdapter app initializer");

        var errorReporter = createErrorReporter();
        event.getSource().addUIInitListener(
                initEvent -> {
                    LoggerFactory.getLogger(getClass())
                            .info("A new UI has been initialized!");
                    initAnalyticsTracker(ProdModeDetector.isProdRunMode());
                    SessionController.initSession(new WebUserSession());
                    registerEventListeners();
                    SessionController.setErrorReporter(errorReporter);
                });

        event.getSource().addSessionInitListener(
                sessionInitEvent -> sessionInitEvent.getSession().setErrorHandler(new MyCustomErrorHandler(errorReporter))
        );
    }

    private static ErrorReporter createErrorReporter() {
        var appVersion = TaPropertiesLoader.getCurrentAppVersion();
        var rollbarApiToken = TaPropertiesLoader.getRollbarApiToken();
        if (rollbarApiToken.isPresent()
                // check for default (empty) value in case resource transformation failed
                && !rollbarApiToken.get().isEmpty()) {
            var rollbar = Rollbar.init(ConfigBuilder
                    .withAccessToken(rollbarApiToken.get())
                    .codeVersion(appVersion)
                    .build());
            logger.info("Configuring Rollbar error reporting.");
            return new RollbarErrorReporter(rollbar);
        }
        logger.warn("Rollbar API token is not found. Skipping Rollbar error reporting.");
        return new NoOpErrorReporter();
    }

    private void registerEventListeners() {
        // temporary code to catch and re-throw "tracker" events
        Tracker tracker = SessionController.getTracker();
        EventBusImpl.subscribe(ApplicationActionEvent.class,
                event -> tracker.trackEvent(event.getCategory(), event.getAction(), event.getLabel()));

        EventBusImpl.subscribe(ApplicationActionEventWithValue.class,
                event -> tracker.trackEvent(event.getCategory(), event.getAction(), event.getLabel(), event.getValue()));
    }

    private static void initAnalyticsTracker(boolean prodMode) {
        var tracker = prodMode ? GoogleAnalyticsFactory.create() : new NoOpGATracker();
        SessionController.setTracker(tracker);
// TODO 14 fix or delete this initial event reporting.
//        tracker.trackEvent(WebAppCategory$.MODULE$, "web_app_opened", services.currentTaskAdapterVersion);
    }
}
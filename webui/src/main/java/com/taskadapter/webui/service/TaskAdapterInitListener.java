package com.taskadapter.webui.service;

import com.taskadapter.app.GoogleAnalyticsFactory;
import com.taskadapter.app.ProdModeDetector;
import com.taskadapter.web.event.ApplicationActionEvent;
import com.taskadapter.web.event.ApplicationActionEventWithValue;
import com.taskadapter.web.event.EventBusImpl;
import com.taskadapter.web.event.NoOpGATracker;
import com.taskadapter.webui.SessionController;
import com.taskadapter.web.event.Tracker;
import com.taskadapter.webui.WebUserSession;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.lang.scala.Subscriber;

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

        event.getSource().addUIInitListener(
                initEvent -> {
                    LoggerFactory.getLogger(getClass())
                            .info("A new UI has been initialized!");
                    initAnalyticsTracker(ProdModeDetector.isProdRunMode());
                    SessionController.initSession(new WebUserSession());
                    registerEventListeners();
                });
    }

    private void registerEventListeners() {
        // temporary code to catch and re-throw "tracker" events
        Tracker tracker = SessionController.getTracker();
        EventBusImpl.observable(ApplicationActionEvent.class)
                .subscribe(new Subscriber<>() {
                    @Override
                    public void onNext(ApplicationActionEvent value) {
                        tracker.trackEvent(value.getCategory(), value.getAction(), value.getLabel());
                    }
                });

        EventBusImpl.observable(ApplicationActionEventWithValue.class)
                .subscribe(new Subscriber<>() {
                    @Override
                    public void onNext(ApplicationActionEventWithValue value) {
                        tracker.trackEvent(value.getCategory(), value.getAction(), value.getLabel(), value.getValue());
                    }
                });
    }

    private static void initAnalyticsTracker(boolean prodMode) {
        var tracker = prodMode ? GoogleAnalyticsFactory.create() : new NoOpGATracker();
        SessionController.setTracker(tracker);

        var services = SessionController.getServices();
        var action = services.licenseManager.isSomeValidLicenseInstalled() ?
                "web_app_opened_licensed" : "web_app_opened_trial";
// TODO 14 fix or delete this initial event reporting.
//        tracker.trackEvent(WebAppCategory$.MODULE$, action, services.currentTaskAdapterVersion);
    }
}
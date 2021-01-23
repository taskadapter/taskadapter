package com.taskadapter.webui.pageset;

import com.taskadapter.web.event.ApplicationActionEvent;
import com.taskadapter.web.event.ApplicationActionEventWithValue;
import com.taskadapter.web.event.ConfigCreateCompleted;
import com.taskadapter.web.event.EventBusImpl;
import com.taskadapter.web.event.PageShown;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.web.uiapi.ConfigId;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.ConfigCategory$;
import com.taskadapter.webui.EventTracker;
import com.taskadapter.webui.Header;
import com.taskadapter.webui.SessionController;
import com.taskadapter.webui.TAPageLayout;
import com.taskadapter.webui.Tracker;
import com.taskadapter.webui.UserContext;
import com.taskadapter.webui.export.ExportResultsFragment;
import com.taskadapter.webui.license.LicenseFacade;
import com.taskadapter.webui.pages.AppUpdateNotificationComponent;
import com.taskadapter.webui.results.ExportResultFormat;
import com.taskadapter.webui.results.ExportResultsLayout;
import com.taskadapter.webui.service.Preservices;
//import com.vaadin.server.StreamVariable;
//import com.vaadin.server.VaadinSession;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import rx.lang.scala.Subscriber;
import scala.Option;

public class LoggedInPageset {

    /**
     * Global (app-wide) services.
     */
    private final Preservices services;

    /**
     * Context for current (logged-in) user.
     */
    private final UserContext context;

    private final LicenseFacade license;

    /**
     * Area for the current page.
     */
    private final HorizontalLayout currentComponentArea = new HorizontalLayout();

    // TODO 14 not used
    /**
     * @param services           used services.
     * @param ctx                context for active user.
     */
    private LoggedInPageset(Preservices services, UserContext ctx) {
        this.services = services;
        this.context = ctx;
        this.license = new LicenseFacade(services.licenseManager);

        Component header = Header.render(() -> {}, new Label("dummy"), license.isLicensed());

        TAPageLayout.layoutPage(header, currentComponentArea);
        registerEventListeners();
    }

    // TODO 14 not used
    private void registerEventListeners() {
        // temporary code to catch and re-throw "tracker" events
        Tracker tracker = SessionController.getTracker();
        EventBusImpl.observable(PageShown.class)
                .subscribe(new Subscriber<PageShown>() {
                    @Override
                    public void onNext(PageShown value) {
                        tracker.trackPage(value.pageName());
                    }
                });
        EventBusImpl.observable(ApplicationActionEvent.class)
                .subscribe(new Subscriber<ApplicationActionEvent>() {
                    @Override
                    public void onNext(ApplicationActionEvent value) {
                        tracker.trackEvent(value.category(), value.action(), value.label());
                    }
                });

        EventBusImpl.observable(ApplicationActionEventWithValue.class)
                .subscribe(new Subscriber<ApplicationActionEventWithValue>() {
                    @Override
                    public void onNext(ApplicationActionEventWithValue value) {
                        tracker.trackEvent(value.category(), value.action(), value.label(), value.value());
                    }
                });

        EventBusImpl.observable(ConfigCreateCompleted.class)
                .subscribe(new Subscriber<ConfigCreateCompleted>() {
                    @Override
                    public void onNext(ConfigCreateCompleted value) {
                        Option<UISyncConfig> maybeConfig = context.configOps.getConfig(value.configId());
                        if (maybeConfig.isEmpty()) {
                            throw new RuntimeException("The newly created config with id " + value.configId() +
                                    " cannot be found. This is weird.");
                        }
                        UISyncConfig config = maybeConfig.get();
                        EventTracker.trackEvent(ConfigCategory$.MODULE$, "created",
                                config.connector1().getConnectorTypeId() + " - " + config.connector2().getConnectorTypeId());
//                    showConfigsList();
                    }
                });
    }

    private Sandbox createSandbox() {
        return new Sandbox(services.settingsManager.isTAWorkingOnLocalMachine(), context.configOps.syncSandbox());
    }

/*
    private void dropIn(final UISyncConfig config, final Html5File file) {
        String fileExtension = Files.getFileExtension(file.getFileName());
        final File df = services.tempFileManager.nextFile(fileExtension);
        file.setStreamVariable(new StreamVariable() {
            @Override
            public void streamingStarted(StreamingStartEvent event) {
            }

            @Override
            public void streamingFinished(StreamingEndEvent event) {
                final VaadinSession ss = VaadinSession.getCurrent();
                ss.lock();
                try {
                    final int maxTasks = services.licenseManager
                            .isSomeValidLicenseInstalled() ? Constants.maxTasksToLoad()
                            : LicenseManager.TRIAL_TASKS_NUMBER_LIMIT;
                    EventTracker.trackPage("drop_in");
                    Component component = DropInExportPage.render(
                            services.exportResultStorage,
                            context.configOps, config,
                            maxTasks, services.settingsManager
                                    .isTAWorkingOnLocalMachine(),
                            new Runnable() {
                                @Override
                                public void run() {
                                    df.delete();
                                    showHome();
                                }
                            }, df);
                    applyUI(component);
                } finally {
                    ss.unlock();
                }
            }

            @Override
            public void streamingFailed(StreamingErrorEvent event) {
                final VaadinSession ss = VaadinSession.getCurrent();
                ss.lock();
                try {
                    Notification.show(Page.message("uploadFailure", event.getException().toString()));
                } finally {
                    ss.unlock();
                }
                df.delete();
            }

            @Override
            public void onProgress(StreamingProgressEvent event) {
                log.debug("Safely ignoring 'progress' event. We don't need it.");
            }

            @Override
            public boolean listenProgress() {
                return false;
            }

            @Override
            public boolean isInterrupted() {
                return false;
            }

            @Override
            public OutputStream getOutputStream() {
                try {
                    return new FileOutputStream(df);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("File not found.", e);
                }
            }
        });
    }
*/

    // TODO TA3 file based connector - MSP
  /*  private void processFile(final UISyncConfig config,
            FileBasedConnector connectorTo) {
        if (!connectorTo.fileExists()) {
            exportCommon(config, exportDirection);
            return;
        }

        final String fileName = new File(
                connectorTo.getAbsoluteOutputFileName()).getName();
        final MessageDialog messageDialog = new MessageDialog(
                message("export.chooseOperation"),
                Page.message("export.fileAlreadyExists", fileName),
                Arrays.asList(message("export.update"),
                        message("export.overwrite"),
                        message("button.cancel")),
                new MessageDialog.Callback() {
                    public void onDialogResult(String answer) {
                        processSyncAction(config, answer);
                    }
                });
        messageDialog.setWidth(465, PIXELS);

        ui.getUI().addWindow(messageDialog);
    }

    private void processSyncAction(UISyncConfig config, String action) {
        if (action.equals(message("button.cancel"))) {
            return;
        }
        if (action.equals(message("export.update"))) {
            startUpdateFile(config);
        } else {
            exportCommon(config, exportDirection);
        }
    }
*/

/*
    private void startUpdateFile(UISyncConfig config) {
        tracker.trackPage("update_file");
        final int maxTasks = services.licenseManager
                .isSomeValidLicenseInstalled() ? MAX_TASKS_TO_LOAD
                : LicenseManager.TRIAL_TASKS_NUMBER_LIMIT;
        applyUI(UpdateFilePage.render(context.configOps, config, maxTasks,
                this::showHome));
    }
*/

    private void applyUI(Component ui) {
        currentComponentArea.removeAll();
        currentComponentArea.add(ui);
//        currentComponentArea.setComponentAlignment(ui, Alignment.TOP_CENTER);
    }
}

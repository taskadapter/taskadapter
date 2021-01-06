package com.taskadapter.webui.pageset;

import com.google.common.io.Files;
import com.taskadapter.Constants;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.web.event.ApplicationActionEvent;
import com.taskadapter.web.event.ApplicationActionEventWithValue;
import com.taskadapter.web.event.EventBusImpl;
import com.taskadapter.web.event.NewConfigPageRequested;
import com.taskadapter.web.event.PageShown;
import com.taskadapter.web.event.ShowAllExportResultsRequested;
import com.taskadapter.web.event.ShowConfigPageRequested;
import com.taskadapter.web.event.ShowConfigsListPageRequested;
import com.taskadapter.web.event.ShowSetupsListPageRequested;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.web.uiapi.ConfigId;
import com.taskadapter.web.uiapi.SetupId;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.ConfigCategory$;
import com.taskadapter.webui.ConfigureSystemPage;
import com.taskadapter.webui.EventTracker;
import com.taskadapter.webui.Header;
import com.taskadapter.webui.HeaderMenuBuilder;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.SessionController;
import com.taskadapter.webui.Sizes;
import com.taskadapter.webui.TAPageLayout;
import com.taskadapter.webui.Tracker;
import com.taskadapter.webui.UserContext;
import com.taskadapter.webui.config.EditSetupPage;
import com.taskadapter.webui.config.NewSetupPage;
import com.taskadapter.webui.config.SetupsListPage;
import com.taskadapter.webui.export.ExportResultsFragment;
import com.taskadapter.webui.license.LicenseFacade;
import com.taskadapter.webui.pages.AppUpdateNotificationComponent;
import com.taskadapter.webui.pages.BeforeEvent;
import com.taskadapter.webui.pages.ConfigPage;
import com.taskadapter.webui.pages.ConfigsListPage;
import com.taskadapter.webui.pages.DropInExportPage;
import com.taskadapter.webui.pages.LicenseAgreementPage;
import com.taskadapter.webui.pages.NewConfigPage;
import com.taskadapter.webui.pages.SchedulesListPage;
import com.taskadapter.webui.pages.SupportPage;
import com.taskadapter.webui.pages.UserProfilePage;
import com.taskadapter.webui.results.ExportResultFormat;
import com.taskadapter.webui.results.ExportResultsListPage;
import com.taskadapter.webui.service.Preservices;
import com.vaadin.server.StreamVariable;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.taskadapter.vaadin14shim.HorizontalLayout;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.lang.scala.Subscriber;
import scala.Function0;
import scala.Function1;
import scala.Option;
import scala.collection.Seq;
import scala.runtime.BoxedUnit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static com.taskadapter.webui.Page.message;

/**
 * Pageset for logged-in user.
 */
public class LoggedInPageset {
    private static final Logger log = LoggerFactory.getLogger(LoggedInPageset.class);

    /**
     * Global (app-wide) services.
     */
    private final Preservices services;

    /**
     * Context for current (logged-in) user.
     */
    private final UserContext context;

    /**
     * License facade.
     */
    private final LicenseFacade license;

    /**
     * Ui component.
     */
    private final Component ui;

    /**
     * Area for the current page.
     */
    private final HorizontalLayout currentComponentArea = new HorizontalLayout();

    private final ConfigsListPage configsListPage;

    /**
     * @param services           used services.
     * @param ctx                context for active user.
     */
    private LoggedInPageset(Preservices services, UserContext ctx) {
        this.services = services;
        this.context = ctx;
        this.license = new LicenseFacade(services.licenseManager);

        final Component header = Header.render(this::showHome, createMenu(), createSelfManagementMenu(), license.isLicensed());

        currentComponentArea.setWidth(Sizes.mainWidth());
        this.ui = TAPageLayout.layoutPage(header, new AppUpdateNotificationComponent(), currentComponentArea);
        this.configsListPage = new ConfigsListPage();
        registerEventListeners();
    }

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

        EventBusImpl.observable(ShowConfigsListPageRequested.class)
                .subscribe(new Subscriber<ShowConfigsListPageRequested>() {
                    @Override
                    public void onNext(ShowConfigsListPageRequested value) {
                        showConfigsList();
                    }
                });

        EventBusImpl.observable(ShowSetupsListPageRequested.class)
                .subscribe(new Subscriber<ShowSetupsListPageRequested>() {
                    @Override
                    public void onNext(ShowSetupsListPageRequested value) {
                        showSetupsListPage();
                    }
                });

        EventBusImpl.observable(ShowConfigPageRequested.class)
                .subscribe(new Subscriber<ShowConfigPageRequested>() {
                    @Override
                    public void onNext(ShowConfigPageRequested value) {
                        showConfigPanel(value.configId());
                    }
                });

        EventBusImpl.observable(ShowAllExportResultsRequested.class)
                .subscribe(new Subscriber<ShowAllExportResultsRequested>() {
                    @Override
                    public void onNext(ShowAllExportResultsRequested value) {
                        showExportResults(value.configId());
                    }
                });

        EventBusImpl.observable(NewConfigPageRequested.class)
                .subscribe(new Subscriber<NewConfigPageRequested>() {
                    @Override
                    public void onNext(NewConfigPageRequested value) {
                        createNewConfig();
                    }
                });
    }

    /**
     * Creates a self-management menu.
     */
    private Component createSelfManagementMenu() {
        HorizontalLayout layout = new HorizontalLayout(
                HeaderMenuBuilder.createButton(
                        message("headerMenu.userProfile"),
                        this::showUserProfilePage));
        layout.setSpacing(true);
        return layout;
    }

    private void showUserProfilePage() {
        applyUI(new UserProfilePage());
    }

    private Component createMenu() {
        final HorizontalLayout menu = new HorizontalLayout();
        menu.setSpacing(true);
        menu.addComponent(HeaderMenuBuilder.createButton(message("headerMenu.configs"),
                this::showConfigsList));

        menu.addComponent(HeaderMenuBuilder.createButton(message("headerMenu.schedules"),
                this::showSchedules));

        menu.addComponent(HeaderMenuBuilder.createButton(message("headerMenu.results"),
                this::showAllResults));

        menu.addComponent(HeaderMenuBuilder.createButton(message("headerMenu.configure"),
                this::showSystemConfiguration));
        menu.addComponent(HeaderMenuBuilder.createButton(message("headerMenu.support"),
                this::showSupport));

        return menu;
    }

    private void showAllResults() {
        ExportResultsListPage page = new ExportResultsListPage(showExportResultsScala());
        Seq<ExportResultFormat> results = services.exportResultStorage.getSaveResults();
        page.showResults(results);
        EventTracker.trackPage("all_results");
        applyUI(page.ui());
    }

    private void showSchedules() {
        applyUI(new SchedulesListPage().ui());
    }

    private void showConfigPanel(ConfigId configId) {
        Option<UISyncConfig> maybeConfig = context.configOps.getConfig(configId);
        if (maybeConfig.isEmpty()) {
            log.error("Cannot find config with id " + configId + "to show in the UI. It may have been deleted already");
            return;
        }
        UISyncConfig config = maybeConfig.get();

        ConfigPage page = new ConfigPage();
        page.setParameter(new BeforeEvent(), config.configId().id() + "");
        applyUI(page.ui());
    }

    /**
     * Shows a support page.
     */
    private void showSupport() {
        applyUI(new SupportPage());
    }

    /**
     * Shows a license agreement page.
     */
    private void showLicensePage() {
        EventTracker.trackPage("license_agreement");
        applyUI(LicenseAgreementPage.render(services.settingsManager,
                this::showHome));
    }

    private void showConfigsList() {
        configsListPage.refreshConfigs();
        EventTracker.trackPage("configs_list");
        applyUI(configsListPage);
    }

    private void showResult(ExportResultFormat result) {
        ExportResultsFragment fragment = new ExportResultsFragment(
                services.settingsManager.isTAWorkingOnLocalMachine());
        Component component = fragment.showExportResult(result);
        applyUI(component);
    }

    private void showExportResults(ConfigId configId) {
        ExportResultsListPage exportResultsListPage = new ExportResultsListPage(showExportResultsScala());
        Seq<ExportResultFormat> results = services.exportResultStorage.getSaveResults(configId);
        exportResultsListPage.showResults(results);
        applyUI(exportResultsListPage.ui());
    }

    private Function1<ExportResultFormat, BoxedUnit> showExportResultsScala() {
        return (result) -> {
            showResult(result);
            return BoxedUnit.UNIT;
        };
    }

    private void showHome() {
        showConfigsList();
    }

    public void createNewConfig() {
        EventTracker.trackPage("create_config");
        applyUI(new NewConfigPage(services.editorManager, services.pluginManager, context.configOps, createSandbox(),
                configId -> {
                    Option<UISyncConfig> maybeCconfig = context.configOps.getConfig(configId);
                    UISyncConfig config = maybeCconfig.get();
                    EventTracker.trackEvent(ConfigCategory$.MODULE$, "created",
                            config.connector1().getConnectorTypeId() + " - " + config.connector2().getConnectorTypeId());
                    showConfigsList();
                }).panel());
    }

    private Sandbox createSandbox() {
        return new Sandbox(services.settingsManager.isTAWorkingOnLocalMachine(), context.configOps.syncSandbox());
    }

    private void showSystemConfiguration() {
        applyUI(new ConfigureSystemPage().ui());
    }

    private void showSetupsListPage() {
        applyUI(new SetupsListPage(context.configOps, showEditSetupPage(), showNewSetupPage())
                .ui());
    }

    private Function1<SetupId, BoxedUnit> showEditSetupPage() {
        return (setupId) -> {
            EventTracker.trackPage("edit_setup");
            applyUI(new EditSetupPage(context.configOps, services.editorManager, services.pluginManager,
                    createSandbox(), setupId).ui());
            return BoxedUnit.UNIT;
        };
    }

    private Function0<BoxedUnit> showNewSetupPage() {
        return () -> {
            EventTracker.trackPage("add_setup");
            applyUI(new NewSetupPage(context.configOps, services.editorManager, services.pluginManager,
                    createSandbox()).ui());
            return BoxedUnit.UNIT;
        };
    }

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
        currentComponentArea.setComponentAlignment(ui, Alignment.TOP_CENTER);
    }

    /**
     * Creates a new pageset for logged-in user.
     *
     * @param services       global services.
     * @param ctx            Context for active user.
     * @return pageset UI.
     */
    public static Component createPageset(Preservices services, UserContext ctx) {
        final LoggedInPageset ps = new LoggedInPageset(services,
                ctx);
        if (services.settingsManager.isLicenseAgreementAccepted())
            ps.showHome();
        else
            ps.showLicensePage();
        return ps.ui;
    }
}

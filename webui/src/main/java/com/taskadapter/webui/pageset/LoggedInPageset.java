package com.taskadapter.webui.pageset;

import com.google.common.io.Files;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.web.uiapi.ConfigId;
import com.taskadapter.web.uiapi.SetupId;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.ConfigCategory$;
import com.taskadapter.webui.ConfigureSystemPage;
import com.taskadapter.webui.Header;
import com.taskadapter.webui.HeaderMenuBuilder;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.Sizes;
import com.taskadapter.webui.TAPageLayout;
import com.taskadapter.webui.Tracker;
import com.taskadapter.webui.UserContext;
import com.taskadapter.webui.WebUserSession;
import com.taskadapter.webui.config.EditSetupPage;
import com.taskadapter.webui.config.NewSetupPage;
import com.taskadapter.webui.config.SetupsListPage;
import com.taskadapter.webui.export.ExportResultsFragment;
import com.taskadapter.webui.license.LicenseFacade;
import com.taskadapter.webui.pages.AppUpdateNotificationComponent;
import com.taskadapter.webui.pages.ConfigsPage;
import com.taskadapter.webui.pages.DropInExportPage;
import com.taskadapter.webui.pages.ExportPage;
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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Function0;
import scala.Function1;
import scala.Option;
import scala.collection.JavaConversions;
import scala.collection.Seq;
import scala.runtime.BoxedUnit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.taskadapter.webui.Page.message;

/**
 * Pageset for logged-in user.
 */
public class LoggedInPageset {
    private static final int MAX_TASKS_TO_LOAD = Integer.MAX_VALUE;
    private static final Logger log = LoggerFactory.getLogger(LoggedInPageset.class);

    /**
     * Global (app-wide) services.
     */
    private final Preservices services;

    /**
     * Credentials manager.
     */
    private final CredentialsManager credentialsManager;

    /**
     * Context for current (logged-in) user.
     */
    private final UserContext context;

    /**
     * License facade.
     */
    private final LicenseFacade license;

    private final WebUserSession webUserSession;
    /**
     * Callback to use on logout.
     */
    private final Runnable logoutCallback;

    /**
     * Usage tracer.
     */
    private final Tracker tracker;

    /**
     * Ui component.
     */
    private final Component ui;

    /**
     * Area for the current page.
     */
    private final HorizontalLayout currentComponentArea = new HorizontalLayout();

    private final ConfigsPage configsPage;

    /**
     * Creates a new pageset.
     *
     * @param credentialsManager credentialsManager
     * @param services           used services.
     * @param tracker            usage tracker.
     * @param ctx                context for active user.
     * @param callback           callback to use.
     */
    private LoggedInPageset(CredentialsManager credentialsManager,
                            Preservices services, Tracker tracker, UserContext ctx, WebUserSession webUserSession,
                            Runnable callback) {
        this.services = services;
        this.credentialsManager = credentialsManager;
        this.context = ctx;
        this.tracker = tracker;
        this.webUserSession = webUserSession;
        this.logoutCallback = callback;
        this.license = new LicenseFacade(services.licenseManager);

        final Component header = Header.render(this::showHome, createMenu(), createSelfManagementMenu(), license.isLicensed());

        currentComponentArea.setWidth(Sizes.mainWidth());
        this.ui = TAPageLayout.layoutPage(header, new AppUpdateNotificationComponent(), currentComponentArea);
        this.configsPage = createConfigsPage();
        showConfigsList();
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
        tracker.trackPage("user_profile");
        applyUI(new UserProfilePage(context.name, context.selfManagement::changePassword, logoutCallback,
                showSetupsListPage()).ui());
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
        ExportResultsListPage page = new ExportResultsListPage(this::showHome, showExportResultsScala());
        Seq<ExportResultFormat> results = services.exportResultStorage.getSaveResults();
        page.showResults(results);
        tracker.trackPage("all_results");
        applyUI(page.ui());
    }

    private void showSchedules() {
        SchedulesListPage schedulesListPage = new SchedulesListPage(tracker, services.schedulesStorage,
                context.configOps, services.settingsManager
        );
        schedulesListPage.showSchedules(services.schedulesStorage.getSchedules());
        tracker.trackPage("schedules");
        applyUI(schedulesListPage.ui());
    }

    /**
     * Shows a support page.
     */
    private void showSupport() {
        tracker.trackPage("support");
        applyUI(SupportPage.render(services.currentTaskAdapterVersion, license, tracker));
    }

    /**
     * Shows a license agreement page.
     */
    private void showLicensePage() {
        tracker.trackPage("license_agreement");
        applyUI(LicenseAgreementPage.render(services.settingsManager,
                this::showHome));
    }

    private void showConfigsList() {
        configsPage.refreshConfigs();
        tracker.trackPage("configs_list");
        applyUI(configsPage.ui());
    }

    private void showLastResults(ConfigId configId) {
        Seq<ExportResultFormat> results = services.exportResultStorage.getSaveResults(configId);

        List<ExportResultFormat> javaResults = new ArrayList<>(JavaConversions.seqAsJavaList(results));
        javaResults.sort(Comparator.comparing(ExportResultFormat::dateStarted));
        if (javaResults.isEmpty()) {
            Notification.show(Page.message("error.noLastExportResult"));
        } else {
            ExportResultFormat last = javaResults.get(0);
            showResult(last);
        }
    }

    private void showResult(ExportResultFormat result) {
        ExportResultsFragment fragment = new ExportResultsFragment(this::showHome,
                services.settingsManager.isTAWorkingOnLocalMachine());
        Component component = fragment.showExportResult(result);
        applyUI(component);
    }

    private void showExportResults(ConfigId configId) {
        ExportResultsListPage exportResultsListPage = new ExportResultsListPage(this::showHome, showExportResultsScala());
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

    private boolean needToShowAllConfigs() {
        return services.settingsManager
                .adminCanManageAllConfigs()
                && context.authorizedOps.canManagerPeerConfigs();
    }

    private ConfigsPage createConfigsPage() {
        return new ConfigsPage(tracker, needToShowAllConfigs(),
                new ConfigsPage.Callback() {
                    @Override
                    public void newConfig() {
                        createNewConfig();
                    }

                    @Override
                    public void showAllPreviousResults(ConfigId configId) {
                        showExportResults(configId);
                    }

                    @Override
                    public void showLastExportResult(ConfigId configId) {
                        showLastResults(configId);
                    }

                    @Override
                    public void startExport(UISyncConfig config) {
                        exportCommon(config);
                    }

                    @Override
                    public void forwardDropIn(UISyncConfig config,
                                              Html5File file) {
                        dropIn(config, file);
                    }

                    @Override
                    public void backwardDropIn(UISyncConfig config,
                                               Html5File file) {
                        dropIn(config.reverse(), file);
                    }
                },
                context.configOps,
                webUserSession,
                createSandbox()
        );

    }

    public void createNewConfig() {
        tracker.trackPage("create_config");
        applyUI(new NewConfigPage(services.editorManager, services.pluginManager, context.configOps, createSandbox(),
                configId -> {
                    Option<UISyncConfig> maybeCconfig = context.configOps.getConfig(configId);
                    UISyncConfig config = maybeCconfig.get();
                    tracker.trackEvent(ConfigCategory$.MODULE$, "created",
                            config.connector1().getConnectorTypeId() + " - " + config.connector2().getConnectorTypeId());
                    webUserSession.setCurrentConfigId(configId);
                    showConfigsList();
                }).panel());
    }

    private Sandbox createSandbox() {
        return new Sandbox(services.settingsManager.isTAWorkingOnLocalMachine(), context.configOps.syncSandbox());
    }

    /**
     * Shows a system configuration panel.
     */
    private void showSystemConfiguration() {
        tracker.trackPage("system_configuration");
        applyUI(ConfigureSystemPage.render(credentialsManager,
                services.settingsManager, services.licenseManager.getLicense(),
                context.authorizedOps, tracker));
    }

    private Function0<BoxedUnit> showSetupsListPage() {
        return () -> {
            tracker.trackPage("setups_list");
            applyUI(new SetupsListPage(tracker, context.configOps,
                    showEditSetupPage(), showNewSetupPage()
            ).ui());
            return BoxedUnit.UNIT;
        };
    }

    private Function1<SetupId, BoxedUnit> showEditSetupPage() {
        return (setupId) -> {
            tracker.trackPage("edit_setup");
            applyUI(new EditSetupPage(context.configOps, services.editorManager, services.pluginManager,
                    createSandbox(), setupId, showSetupsListPage()
            ).ui());
            return BoxedUnit.UNIT;
        };
    }

    private Function0<BoxedUnit> showNewSetupPage() {
        return () -> {
            tracker.trackPage("add_setup");
            applyUI(new NewSetupPage(context.configOps, services.editorManager, services.pluginManager,
                    createSandbox(), showSetupsListPage()
            ).ui());
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
                            .isSomeValidLicenseInstalled() ? MAX_TASKS_TO_LOAD
                            : LicenseManager.TRIAL_TASKS_NUMBER_LIMIT;
                    tracker.trackPage("drop_in");
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
                            }, df, tracker);
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

    private void exportCommon(UISyncConfig config) {
        log.info("Starting export from " +
                config.connector1().getConnectorTypeId() +
                " (" + config.connector1().getSourceLocation() + ") "
                + " to " +
                config.connector2().getConnectorTypeId() +
                " (" + config.connector2().getDestinationLocation() + ")");
        tracker.trackPage("export_confirmation");
        final int maxTasks = services.licenseManager
                .isSomeValidLicenseInstalled() ? MAX_TASKS_TO_LOAD
                : LicenseManager.TRIAL_TASKS_NUMBER_LIMIT;
        log.info("License installed? " + services.licenseManager.isSomeValidLicenseInstalled());
        applyUI(new ExportPage(services.exportResultStorage,
                context.configOps, config, maxTasks,
                services.settingsManager.isTAWorkingOnLocalMachine(),
                this::showHome, tracker).ui);
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

    /**
     * Applies a new content.
     *
     * @param ui new content.
     */
    private void applyUI(Component ui) {
        currentComponentArea.removeAllComponents();
        ui.setSizeUndefined();
        currentComponentArea.addComponent(ui);
        currentComponentArea.setComponentAlignment(ui, Alignment.TOP_CENTER);
    }

    /**
     * Creates a new pageset for logged-in user.
     *
     * @param services       global services.
     * @param tracker        context tracker.
     * @param ctx            Context for active user.
     * @param logoutCallback callback to invoke on logout.
     * @return pageset UI.
     */
    public static Component createPageset(CredentialsManager credManager,
                                          Preservices services, Tracker tracker, UserContext ctx, WebUserSession webUserSession,
                                          Runnable logoutCallback) {
        final LoggedInPageset ps = new LoggedInPageset(credManager, services,
                tracker, ctx, webUserSession, logoutCallback);
        if (services.settingsManager.isLicenseAgreementAccepted())
            ps.showHome();
        else
            ps.showLicensePage();
        return ps.ui;
    }
}

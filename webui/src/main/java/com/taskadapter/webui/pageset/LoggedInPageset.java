package com.taskadapter.webui.pageset;

import com.google.common.io.Files;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.config.StorageException;
import com.taskadapter.connector.definition.ExportDirection;
import com.taskadapter.connector.definition.FileBasedConnector;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.web.MessageDialog;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.ConfigureSystemPage;
import com.taskadapter.webui.Header;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.TAPageLayout;
import com.taskadapter.webui.Tracker;
import com.taskadapter.webui.UserContext;
import com.taskadapter.webui.WebUserSession;
import com.taskadapter.webui.config.EditConfigPage;
import com.taskadapter.webui.license.LicenseFacade;
import com.taskadapter.webui.pages.ConfigsPage;
import com.taskadapter.webui.pages.DropInExportPage;
import com.taskadapter.webui.pages.ExportPage;
import com.taskadapter.webui.pages.LicenseAgreementPage;
import com.taskadapter.webui.pages.NewConfigPage;
import com.taskadapter.webui.pages.SupportPage;
import com.taskadapter.webui.pages.UpdateFilePage;
import com.taskadapter.webui.service.Preservices;
import com.taskadapter.webui.user.ChangePasswordDialog;
import com.vaadin.server.StreamVariable;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.BaseTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import static com.taskadapter.webui.Page.message;
import static com.vaadin.server.Sizeable.Unit.PIXELS;

/**
 * Pageset for logged-in user.
 * 
 */
public class LoggedInPageset {
    private static final int MAX_TASKS_TO_LOAD = Integer.MAX_VALUE;
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggedInPageset.class);

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

    /**
     * Creates a new pageset.
     * 
     * @param credentialsManager
     *            credentialsManager
     * 
     * @param services
     *            used services.
     * @param tracker
     *            usage tracker.
     * @param ctx
     *            context for active user.
     * @param callback
     *            callback to use.
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

        ui = TAPageLayout.layoutPage(header, currentComponentArea);
    }

    /**
     * Creates a self-management menu.
     */
    private Component createSelfManagementMenu() {
        final HorizontalLayout panelForLoggedInUsers = new HorizontalLayout();
        panelForLoggedInUsers.setSpacing(true);

        final Button logoutButton = new Button(message("headerMenu.logout"));
        logoutButton.setStyleName(BaseTheme.BUTTON_LINK);
        logoutButton.addStyleName("personalMenuItem");
        logoutButton.addClickListener((Button.ClickListener) event -> logoutCallback.run());
        panelForLoggedInUsers.addComponent(logoutButton);

        Button setPasswordButton = new Button(message("headerMenu.changePassword"));
        setPasswordButton.setStyleName(BaseTheme.BUTTON_LINK);
        setPasswordButton.addStyleName("personalMenuItem");
        setPasswordButton.addClickListener((Button.ClickListener) event -> showChangePasswordDialog());
        panelForLoggedInUsers.addComponent(setPasswordButton);

        return panelForLoggedInUsers;
    }

    /**
     * Attempts to change password for the current user.
     */
    private void showChangePasswordDialog() {
        ChangePasswordDialog.showDialog(ui.getUI(), context.name,
                context.selfManagement::changePassword);
    }

    private Component createMenu() {
        final HorizontalLayout menu = new HorizontalLayout();
        menu.setSpacing(true);

        final Button configureButton = new Button(message("headerMenu.configure"));
        configureButton.setStyleName(BaseTheme.BUTTON_LINK);
        configureButton.addStyleName("menu");
        configureButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                showSystemConfiguration();
            }
        });
        menu.addComponent(configureButton);

        final Button supportButton = ButtonBuilder.createSupportButton();
        supportButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                showSupport();
            }
        });
        menu.addComponent(supportButton);
        return menu;
    }

    /**
     * Shows a support page.
     */
    private void showSupport() {
        tracker.trackPage("support");
        applyUI(SupportPage.render(services.currentTaskAdapterVersion, license));
    }

    /**
     * Shows a license agreement page.
     */
    private void showLicensePage() {
        tracker.trackPage("license_agreement");
        applyUI(LicenseAgreementPage.render(services.settingsManager,
                this::showHome));
    }

    /**
     * Shows a home page.
     */
    private void showHome() {
        final boolean showAll = services.settingsManager
                .adminCanManageAllConfigs()
                && context.authorizedOps.canManagerPeerConfigs();
        final List<UISyncConfig> configs = showAll ? context.configOps
                .getManageableConfigs() : context.configOps.getOwnedConfigs();

        if (webUserSession.getCurrentConfig() == null) {
            showConfigsList(showAll, configs);
        } else {
            showConfigEditor(webUserSession.getCurrentConfig(), null);
        }
    }

    private void showConfigsList(boolean showAll, List<UISyncConfig> configs) {
        tracker.trackPage("configs_list");
        Component component = ConfigsPage.render(context.name, configs,
        showAll ? ConfigsPage.DisplayMode.ALL_CONFIGS
                : ConfigsPage.DisplayMode.OWNED_CONFIGS,
        new ConfigsPage.Callback() {
            @Override
            public void newConfig() {
                createNewConfig();
            }

            @Override
            public void forwardSync(UISyncConfig config) {
                sync(config);
            }

            @Override
            public void backwardSync(UISyncConfig config) {
                sync(config.reverse());
            }

            @Override
            public void edit(UISyncConfig config) {
                showConfigEditor(config, null);
            }

            @Override
            public void forwardDropIn(UISyncConfig config,
                                      Html5File file) {
                // TODO TA3 drop-in
//                dropIn(config, file);
            }

            @Override
            public void backwardDropIn(UISyncConfig config,
                                       Html5File file) {
                // TODO TA3 drop-in
//                dropIn(config.reverse(), file);
            }
        });
        applyUI(component);
    }

    /**
     * Creates a new config.
     */
    public void createNewConfig() {
        tracker.trackPage("create_config");
        applyUI(NewConfigPage.render(services.pluginManager, context.configOps,
                config -> {
                    tracker.trackEvent("config", "created",
                            config.connector1().getConnectorTypeId() + " - " + config.connector2().getConnectorTypeId());
                    showConfigEditor(config, null);
                }));
    }

    /**
     * Shows a system configuration panel.
     */
    private void showSystemConfiguration() {
        tracker.trackPage("system_configuration");
        applyUI(ConfigureSystemPage.render(credentialsManager,
                services.settingsManager, services.licenseManager.getLicense(),
                context.authorizedOps));
    }

    /**
     * Shows a config editor page.
     */
    private void showConfigEditor(UISyncConfig config, String error) {
        tracker.trackPage("edit_config");
        webUserSession.setCurrentConfig(config);
        applyUI(getConfigEditor(config, error));
    }

    private Component getConfigEditor(UISyncConfig config, String error) {
        return EditConfigPage.render(config, context.configOps,
                services.settingsManager.isTAWorkingOnLocalMachine(), error,
                new EditConfigPage.Callback() {
                    @Override
                    public void forwardSync(UISyncConfig config) {
                        sync(config);
                    }

                    @Override
                    public void backwardSync(UISyncConfig config) {
                        sync(config.reverse());
                    }

                    @Override
                    public void back() {
                        clearCurrentConfigInSession();
                        showHome();
                    }
                });
    }

    private void clearCurrentConfigInSession() {
        webUserSession.setCurrentConfig(null);
    }

    /**
     * Performs a synchronization operation from first connector to second.
     * 
     * @param config
     *            base config. May be saved!
     */
    private void sync(UISyncConfig config) {
        if (!prepareForConversion(config))
            return;
//        final NewConnector destinationConnector = config.getConnector2().createConnectorInstance();
        // TODO TA3 file based connector - MSP
//        if (destinationConnector instanceof FileBasedConnector) {
//            processFile(config, (FileBasedConnector) destinationConnector);
//        } else {
            exportCommon(config);
//        }
    }

    private void dropIn(final UISyncConfig config, PreviouslyCreatedTasksResolver resolver, final Html5File file) {
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
                    Component component = DropInExportPage.render(context.configOps, config,
                            resolver,
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
                LOGGER.debug("Safely ignoring 'progress' event. We don't need it.");
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
        tracker.trackPage("export_confirmation");
        final int maxTasks = services.licenseManager
                .isSomeValidLicenseInstalled() ? MAX_TASKS_TO_LOAD
                : LicenseManager.TRIAL_TASKS_NUMBER_LIMIT;
        applyUI(ExportPage.render(context.configOps, config, maxTasks,
                services.settingsManager.isTAWorkingOnLocalMachine(),
                this::showHome));
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
     * Prepares config for conversion.
     * 
     * @param config
     *            config to prepare.
     * @return true iff conversion could be performed, false otherwise.
     */
    private boolean prepareForConversion(UISyncConfig config) {
        final UIConnectorConfig from = config.getConnector1();
        final UIConnectorConfig to = config.getConnector2();

        try {
            from.validateForLoad();
        } catch (BadConfigException e) {
            showConfigEditor(config, from.decodeException(e));
            return false;
        }

        final boolean updated;
        try {
            updated = to.updateForSave(new Sandbox(services.settingsManager.isTAWorkingOnLocalMachine(),
                    context.configOps.syncSandbox));
        } catch (BadConfigException e) {
            showConfigEditor(config, to.decodeException(e));
            return false;
        }

        // If config was changed - save it
        if (updated) {
            try {
                context.configOps.saveConfig(config);
            } catch (StorageException e1) {
                final String message = Page.message("export.troublesSavingConfig", e1.getMessage());
                LOGGER.error(message, e1);
                Notification.show(message, Notification.Type.ERROR_MESSAGE);
            }
        }
        return true;
    }

    /**
     * Applies a new content.
     * 
     * @param ui
     *            new content.
     */
    private void applyUI(Component ui) {
        currentComponentArea.removeAllComponents();
        ui.setSizeUndefined();
        currentComponentArea.addComponent(ui);
        currentComponentArea.setComponentAlignment(ui, Alignment.TOP_LEFT);
    }

    /**
     * Creates a new pageset for logged-in user.
     * 
     * @param services
     *            global services.
     * @param tracker
     *            context tracker.
     * @param ctx
     *            Context for active user.
     * @param logoutCallback
     *            callback to invoke on logout.
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

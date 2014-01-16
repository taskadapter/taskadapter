package com.taskadapter.webui.pageset;

import static com.vaadin.server.Sizeable.Unit.PIXELS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taskadapter.auth.AuthException;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.config.StorageException;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.FileBasedConnector;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
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
import com.taskadapter.webui.service.WrongPasswordException;
import com.taskadapter.webui.user.ChangePasswordDialog;
import com.vaadin.server.StreamVariable;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

/**
 * Pageset for logged-in user.
 * 
 */
public class LoggedInPageset {
    private static final int MAX_TASKS_TO_LOAD = Integer.MAX_VALUE;
    private static final Logger LOGGER = LoggerFactory
            .getLogger(LoggedInPageset.class);

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
     * @param logoutCallback
     *            callback to use.
     */
    private LoggedInPageset(CredentialsManager credentialsManager,
            Preservices services, Tracker tracker, UserContext ctx,
            Runnable callback) {
        this.services = services;
        this.credentialsManager = credentialsManager;
        this.context = ctx;
        this.tracker = tracker;
        this.logoutCallback = callback;
        this.license = new LicenseFacade(services.licenseManager);

        final Component header = Header.render(new Runnable() {
            @Override
            public void run() {
                showHome();
            }
        }, createMenu(), createSelfManagementMenu(), license.isLicensed());

        ui = TAPageLayout.layoutPage(header, currentComponentArea);
    }

    /**
     * Creates a self-management menu.
     */
    private Component createSelfManagementMenu() {
        final HorizontalLayout panelForLoggedInUsers = new HorizontalLayout();
        panelForLoggedInUsers.setSpacing(true);

        final Button logoutButton = new Button("Logout");
        logoutButton.setStyleName(BaseTheme.BUTTON_LINK);
        logoutButton.addStyleName("personalMenuItem");
        logoutButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                logoutCallback.run();
            }
        });
        panelForLoggedInUsers.addComponent(logoutButton);

        Button setPasswordButton = new Button("Change password");
        setPasswordButton.setStyleName(BaseTheme.BUTTON_LINK);
        setPasswordButton.addStyleName("personalMenuItem");
        setPasswordButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                changePassword();
            }
        });
        panelForLoggedInUsers.addComponent(setPasswordButton);

        return panelForLoggedInUsers;
    }

    /**
     * Attempts to change password for the current user.
     */
    private void changePassword() {
        ChangePasswordDialog.showDialog(ui.getUI(), context.name,
                new ChangePasswordDialog.Callback() {
                    @Override
                    public void changePassword(String oldPassword,
                            String newPassword) throws AuthException,
                            WrongPasswordException {
                        context.selfManagement.changePassword(oldPassword,
                                newPassword);
                    }
                });
    }

    /**
     * Creates a menu panel.
     * 
     * @return menu panel.
     */
    private Component createMenu() {
        final HorizontalLayout menu = new HorizontalLayout();
        menu.setSpacing(true);

        final Button configureButton = new Button("Configure");
        configureButton.setStyleName(BaseTheme.BUTTON_LINK);
        configureButton.addStyleName("menu");
        configureButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                showSystemConfiguration();
            }
        });
        menu.addComponent(configureButton);

        final Button supportButton = new Button("Support");
        supportButton.setStyleName(BaseTheme.BUTTON_LINK);
        supportButton.addStyleName("menu");
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
                new Runnable() {
                    @Override
                    public void run() {
                        showHome();
                    }
                }));
    }

    /**
     * Shows a home page.
     */
    private void showHome() {
        tracker.trackPage("home");
        final boolean showAll = services.settingsManager
                .adminCanManageAllConfigs()
                && context.authorizedOps.canManagerPeerConfigs();
        final List<UISyncConfig> configs = showAll ? context.configOps
                .getManageableConfigs() : context.configOps.getOwnedConfigs();

        applyUI(ConfigsPage.render(context.name, configs,
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
                        dropIn(config, file);
                    }

                    @Override
                    public void backwardDropIn(UISyncConfig config,
                            Html5File file) {
                        dropIn(config.reverse(), file);
                    }
                }));
    }

    /**
     * Creates a new config.
     */
    public void createNewConfig() {
        tracker.trackPage("create_config");
        applyUI(NewConfigPage.render(services.pluginManager, context.configOps,
                new NewConfigPage.Callback() {
                    @Override
                    public void configCreated(UISyncConfig config) {
                        showConfigEditor(config, null);
                    }
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
        applyUI(EditConfigPage.render(config, context.configOps,
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
                        showHome();
                    }
                }));
    }

    /**
     * Performs a synchronization operation from first connector to second.
     * 
     * @param config
     *            base config. May be saved!
     * @param from
     *            source config.
     * @param to
     *            destination config.
     */
    private void sync(UISyncConfig config) {
        if (!prepareForConversion(config))
            return;
        final Connector<?> destinationConnector = config.getConnector2()
                .createConnectorInstance();
        if (destinationConnector instanceof FileBasedConnector) {
            processFile(config, (FileBasedConnector) destinationConnector);
        } else {
            exportCommon(config);
        }
    }

    /**
     * Performs a drop-in.
     * 
     * @param config
     *            config.
     * @param file
     *            dropped file.
     */
    private void dropIn(final UISyncConfig config, final Html5File file) {
        final File df = services.tempFileManager.nextFile();
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
                    applyUI(DropInExportPage.render(context.configOps, config,
                            maxTasks, services.settingsManager
                                    .isTAWorkingOnLocalMachine(),
                            new Runnable() {
                                @Override
                                public void run() {
                                    df.delete();
                                    showHome();
                                }
                            }, df));
                } finally {
                    ss.unlock();
                }
            }

            @Override
            public void streamingFailed(StreamingErrorEvent event) {
                final VaadinSession ss = VaadinSession.getCurrent();
                ss.lock();
                try {
                    Notification.show("Fatal Upload Failure "
                            + event.getException());
                } finally {
                    ss.unlock();
                }
                df.delete();
            }

            @Override
            public void onProgress(StreamingProgressEvent event) {
                LOGGER.error("We don't need no progress! Vaadin is not good.");
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
                    throw new RuntimeException(
                            "Vaadin is really bad (file drop handler API)!", e);
                }
            }
        });
    }

    /**
     * Perofrms an export.
     * 
     * @param config
     *            config to export.
     * @param maxTasks
     *            number of tasks.
     */
    private void exportCommon(UISyncConfig config) {
        tracker.trackPage("export_confirmation");
        final int maxTasks = services.licenseManager
                .isSomeValidLicenseInstalled() ? MAX_TASKS_TO_LOAD
                : LicenseManager.TRIAL_TASKS_NUMBER_LIMIT;
        applyUI(ExportPage.render(context.configOps, config, maxTasks,
                services.settingsManager.isTAWorkingOnLocalMachine(),
                new Runnable() {
                    @Override
                    public void run() {
                        showHome();
                    }
                }));
    }

    private void processFile(final UISyncConfig config,
            FileBasedConnector connectorTo) {
        if (!connectorTo.fileExists()) {
            exportCommon(config);
            return;
        }

        final String fileName = new File(
                connectorTo.getAbsoluteOutputFileName()).getName();
        final MessageDialog messageDialog = new MessageDialog(
                Page.MESSAGES.get("export.chooseOperation"),
                Page.MESSAGES.format("export.fileAlreadyExists", fileName),
                Arrays.asList(Page.MESSAGES.get("export.update"),
                        Page.MESSAGES.get("export.overwrite"),
                        Page.MESSAGES.get("button.cancel")),
                new MessageDialog.Callback() {
                    public void onDialogResult(String answer) {
                        processSyncAction(config, answer);
                    }
                });
        messageDialog.setWidth(465, PIXELS);

        ui.getUI().addWindow(messageDialog);
    }

    private void processSyncAction(UISyncConfig config, String action) {
        if (action.equals(Page.MESSAGES.get("button.cancel")))
            return;
        if (action.equals(Page.MESSAGES.get("export.update"))) {
            startUpdateFile(config);
        } else
            exportCommon(config);
    }

    /**
     * Processes a file action.
     */
    private void startUpdateFile(UISyncConfig config) {
        tracker.trackPage("update_file");
        final int maxTasks = services.licenseManager
                .isSomeValidLicenseInstalled() ? MAX_TASKS_TO_LOAD
                : LicenseManager.TRIAL_TASKS_NUMBER_LIMIT;
        applyUI(UpdateFilePage.render(context.configOps, config, maxTasks,
                new Runnable() {
                    @Override
                    public void run() {
                        showHome();
                    }
                }));
    }

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
            updated = to
                    .updateForSave(new Sandbox(services.settingsManager
                            .isTAWorkingOnLocalMachine(),
                            context.configOps.syncSandbox));
        } catch (BadConfigException e) {
            showConfigEditor(config, to.decodeException(e));
            return false;
        }

        /* If config was changed - show it! */
        if (updated) {
            try {
                context.configOps.saveConfig(config);
            } catch (StorageException e1) {
                final String message = Page.MESSAGES.format(
                        "export.troublesSavingConfig", e1.getMessage());
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
            Preservices services, Tracker tracker, UserContext ctx,
            Runnable logoutCallback) {
        final LoggedInPageset ps = new LoggedInPageset(credManager, services,
                tracker, ctx, logoutCallback);
        if (services.settingsManager.isLicenseAgreementAccepted())
            ps.showHome();
        else
            ps.showLicensePage();
        return ps.ui;
    }
}

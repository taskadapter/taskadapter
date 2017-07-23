package com.taskadapter.webui.pages;

import static com.taskadapter.license.LicenseManager.TRIAL_MESSAGE;
import static com.taskadapter.webui.Page.message;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.SaveResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taskadapter.connector.definition.TaskError;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.web.configeditor.file.FileDownloadResource;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.webui.MonitorWrapper;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.export.ConfirmExportFragment;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.VerticalLayout;
import scala.collection.JavaConversions;

/**
 * Export page and export handler.
 * 
 */
public final class ExportPage {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportPage.class);

    /**
     * Config operations.
     */
    private final ConfigOperations configOps;

    /**
     * Sync config.
     */
    private final UISyncConfig config;

    /**
     * Maximal number of transferred tasks.
     */
    private final int taskLimit;

    /**
     * "Process complete" handler.
     */
    private final Runnable onDone;

    /**
     * "Show file path" flag.
     */
    private final boolean showFilePath;

    private final VerticalLayout ui;
    private final Label errorMessage;
    private final VerticalLayout content;

    private ExportPage(ConfigOperations configOps, UISyncConfig config,
                       int taskLimit, boolean showFilePath, Runnable onDone) {
        this.config = config;
        this.onDone = onDone;
        this.taskLimit = taskLimit;
        this.showFilePath = showFilePath;
        this.configOps = configOps;

        ui = new VerticalLayout();
        errorMessage = new Label("");
        errorMessage.addStyleName("errorMessage");
        errorMessage.setVisible(false);
        errorMessage.setWidth(500, Unit.PIXELS);
        ui.addComponent(errorMessage);

        content = new VerticalLayout();
        ui.addComponent(content);
        startLoading();
    }

    /**
     * Starts data loading.
     */
    private void startLoading() {
        setContent(SyncActionComponents.renderLoadIndicator(config
                .getConnector1()));

        if (taskLimit < Integer.MAX_VALUE)
            LOGGER.info(TRIAL_MESSAGE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<GTask> tasks = config.loadTasks(taskLimit);
                    if (tasks.isEmpty())
                        showNoDataLoaded();
                    else
                        showConfirmation(tasks);
                } catch (CommunicationException e) {
                    final String message = config.getConnector1()
                            .decodeException(e);
                    showLoadErrorMessage(message);
                    LOGGER.error("transport error: " + message, e);
                } catch (ConnectorException e) {
                    showLoadErrorMessage(config.getConnector1()
                            .decodeException(e));
                    LOGGER.error(e.getMessage(), e);
                } catch (RuntimeException e) {
                    showLoadErrorMessage("Internal error: " + e.getMessage());
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }).start();
    }

    /**
     * Shows tasks confirmation dialog.
     * 
     * @param tasks
     *            tasks to confirm.
     */
    private void showConfirmation(List<GTask> tasks) {
        final Component component = ConfirmExportFragment.render(configOps,
                config, config.getPreviouslyCreatedTasksResolver(), tasks, new ConfirmExportFragment.Callback() {
                    @Override
                    public void onTasks(List<GTask> selectedTasks) {
                        performExport(selectedTasks);
                    }

                    @Override
                    public void onCancel() {
                        onDone.run();
                    }
                });

        VaadinSession.getCurrent().lock();
        try {
            setContent(component);
            content.setExpandRatio(component, 1f);
        } finally {
            VaadinSession.getCurrent().unlock();
        }
    }

    /**
     * Launches export.
     * 
     * @param selectedTasks list of selected tasks.
     */
    private void performExport(final List<GTask> selectedTasks) {
        if (selectedTasks.isEmpty()) {
            Notification.show(message("action.pleaseSelectTasks"));
            return;
        }

        final ProgressIndicator saveProgress = SyncActionComponents
                .renderSaveIndicator(config.getConnector2());
        saveProgress.setValue(0f);
        setContent(saveProgress);

        final MonitorWrapper wrapper = new MonitorWrapper(saveProgress);

        new Thread(new Runnable() {
            @Override
            public void run() {
                showExportResult(config.saveTasks(selectedTasks, wrapper));
            }
        }).start();
    }

    /**
     * Shows task export result.
     * 
     * @param res
     *            operation result.
     */
    private void showExportResult(SaveResult res) {
        final VerticalLayout ui = new VerticalLayout();

        final VerticalLayout donePanel = new VerticalLayout();
        donePanel.setWidth("800px");
        donePanel.setStyleName("export-panel");

        // TODO format inside MESSAGES formatter, not here.
        final String time = new SimpleDateFormat("MMMM dd, yyyy  HH:mm")
                .format(Calendar.getInstance().getTime());
        final Label label = new Label(Page.message("export.exportCompletedOn", time));
        label.setContentMode(ContentMode.HTML);

        donePanel.addComponent(label);

        String sourceLocation = config.getConnector1().getSourceLocation();
        String targetLocation = config.getConnector2().getSourceLocation();
        donePanel.addComponent(SyncActionComponents.createdExportResultLabel(
                message("export.from"), sourceLocation));
        donePanel.addComponent(SyncActionComponents.createdExportResultLabel(
                message("export.to"), targetLocation));

        final String resultFile = res.getTargetFileAbsolutePath();
        if (resultFile != null && !showFilePath) {
            donePanel.addComponent(createDownloadButton(resultFile));
        }

        donePanel.addComponent(SyncActionComponents.createdExportResultLabel(
                message("export.createdTasks"),
                String.valueOf(res.getCreatedTasksNumber())));
        donePanel.addComponent(SyncActionComponents.createdExportResultLabel(
                message("export.updatedTasks"),
                String.valueOf(res.getUpdatedTasksNumber())
                        + "<br/><br/>"));

        if (!Strings.isNullOrEmpty(resultFile) && showFilePath) {
            final Label flabel = new Label(Page.message("export.pathToExportFile", resultFile));
            flabel.setContentMode(ContentMode.HTML);
            donePanel.addComponent(flabel);
        }

        SyncActionComponents.addErrors(donePanel, config.getConnector2(),
                res.getGeneralErrors(),
                res.getTaskErrors());
        // TODO TA3 check remote id
/*
        if (res.remoteIdUpdateException != null)
            SyncActionComponents
                    .addErrors(
                            donePanel,
                            config.getConnector1(),
                            JavaConversions.asScalaBuffer(
                                    Collections.<Throwable>singletonList(res.remoteIdUpdateException))
                                    .toList(),
                            JavaConversions.asScalaBuffer(Collections.<TaskError<Throwable>>emptyList())
                                    .toList()
                    );

*/
        ui.addComponent(donePanel);

        final Button button = new Button(message("action.acknowledge"));
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                onDone.run();
            }
        });
        ui.addComponent(button);

        VaadinSession.getCurrent().lock();
        try {
            setContent(ui);
        } finally {
            VaadinSession.getCurrent().unlock();
        }
    }

    /**
     * Creates "download file" button.
     * 
     * @param targetFileAbsolutePath
     *            target path.
     */
    private Component createDownloadButton(final String targetFileAbsolutePath) {
        final Button downloadButton = new Button(message("export.downloadFile"));
        File file = new File(targetFileAbsolutePath);
        final FileDownloadResource resource = new FileDownloadResource(file);
        final FileDownloader downloader = new FileDownloader(resource);
        downloader.extend(downloadButton);
        return downloadButton;
    }

    /**
     * Shows "no data loaded" content.
     */
    private void showNoDataLoaded() {
        final VerticalLayout res = new VerticalLayout();
        final Label msg = new Label(message("export.noDataWasLoaded"));
        msg.setWidth(800, Unit.PIXELS);

        final Button backButton = new Button(message("button.back"));
        backButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                onDone.run();
            }
        });
        res.addComponent(msg);
        res.addComponent(backButton);

        setContent(res);

    }

    /**
     * Shows load error message.
     * 
     * @param message
     *            message to show.
     */
    private void showLoadErrorMessage(String message) {
        VaadinSession.getCurrent().lock();
        try {
            showErrorMessage(message);
            showNoDataLoaded();
        } finally {
            VaadinSession.getCurrent().unlock();
        }
    }

    /**
     * Shows or hides an error message.
     * 
     * @param message
     *            error message. If null, errors are hidden.
     */
    private void showErrorMessage(String message) {
        final boolean haveMessage = message != null;
        errorMessage.setValue(haveMessage ? message : "");
        errorMessage.setVisible(haveMessage);
    }

    private void setContent(Component comp) {
        content.removeAllComponents();
        content.addComponent(comp);
    }

    public static Component render(ConfigOperations configOps,
                                   UISyncConfig config,
                                   int taskLimit, boolean showFilePath,
                                   Runnable onDone) {
        return new ExportPage(configOps, config, taskLimit, showFilePath, onDone).ui;
    }
}

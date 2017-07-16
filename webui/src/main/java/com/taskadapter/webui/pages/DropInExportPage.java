package com.taskadapter.webui.pages;

import static com.taskadapter.license.LicenseManager.TRIAL_MESSAGE;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taskadapter.connector.definition.TaskError;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.web.configeditor.file.FileDownloadResource;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.web.uiapi.UISyncConfig.TaskExportResult;
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
 */
public final class DropInExportPage {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DropInExportPage.class);

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

    /**
     * Temp file.
     */
    private final File tempFile;

    private final VerticalLayout ui;
    private final Label errorMessage;
    private final VerticalLayout content;

    private DropInExportPage(ConfigOperations configOps, UISyncConfig config,
            int taskLimit, boolean showFilePath, Runnable onDone, File tempFile) {
        this.config = config;
        this.onDone = onDone;
        this.taskLimit = taskLimit;
        this.showFilePath = showFilePath;
        this.configOps = configOps;
        this.tempFile = tempFile;

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
        setContent(SyncActionComponents.renderLoadIndicator(Page.message("export.dropInFile")));

        if (taskLimit < Integer.MAX_VALUE)
            LOGGER.info(TRIAL_MESSAGE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<GTask> tasks = config.loadDropInTasks(tempFile,
                            taskLimit);
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
                config, tasks, new ConfirmExportFragment.Callback() {
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
     * Perofrms an export.
     * 
     * @param selectedTasks
     *            list of selected tasks.
     */
    private void performExport(final List<GTask> selectedTasks) {
        if (selectedTasks.isEmpty()) {
            Notification.show(Page.message("action.pleaseSelectTasks"));
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
                showExportResult(config.onlySaveTasks(selectedTasks, wrapper));
            }
        }).start();
    }

    /**
     * Shows task export result.
     * 
     * @param res
     *            operation result.
     */
    private void showExportResult(TaskExportResult res) {
        final VerticalLayout ui = new VerticalLayout();

        final VerticalLayout donePanel = new VerticalLayout();
        donePanel.setWidth("600px");
        donePanel.setStyleName("export-panel");

        final String time = new SimpleDateFormat("MMMM dd, yyyy  HH:mm")
                .format(Calendar.getInstance().getTime());
        final Label label = new Label(
                "<strong>Export completed on</strong> <em>" + time + "</em>");
        label.setContentMode(ContentMode.HTML);

        donePanel.addComponent(label);

        donePanel.addComponent(SyncActionComponents.createdExportResultLabel(
                "From", config.getConnector1().getSourceLocation()));

        final String resultFile = res.saveResult.getTargetFileAbsolutePath();
        if (resultFile != null && !showFilePath) {
            donePanel.addComponent(createDownloadButton(resultFile));
        }

        donePanel.addComponent(SyncActionComponents.createdExportResultLabel(
                "Created tasks",
                String.valueOf(res.saveResult.getCreatedTasksNumber())));
        donePanel.addComponent(SyncActionComponents.createdExportResultLabel(
                "Updated tasks",
                String.valueOf(res.saveResult.getUpdatedTasksNumber())
                        + "<br/><br/>"));

        if (resultFile != null && showFilePath) {
            final Label flabel = new Label(
                    "<strong>Path to export file:</strong> <em>" + resultFile
                            + "</em>");
            flabel.setContentMode(ContentMode.HTML);
            donePanel.addComponent(flabel);
        }

        SyncActionComponents.addErrors(donePanel, config.getConnector2(),
                res.saveResult.getGeneralErrors(),
                res.saveResult.getTaskErrors());
        if (res.remoteIdUpdateException != null)
            SyncActionComponents.addErrors(
                    donePanel,
                    config.getConnector1(),
                    JavaConversions.asScalaBuffer(
                            Collections.<Throwable>singletonList(res.remoteIdUpdateException))
                            .toList(),
                    JavaConversions.asScalaBuffer(Collections.<TaskError<Throwable>>emptyList())
                            .toList()
            );

        ui.addComponent(donePanel);

        final Button button = new Button(
                Page.message("action.backToHomePage"));
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
     * Creates a download button.
     * 
     * @param targetFileAbsolutePath
     *            target path.
     */
    private Component createDownloadButton(final String targetFileAbsolutePath) {
        final Button downloadButton = new Button("Download file");
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
        final Label msg = new Label(
                "No data was loaded using the given criteria.");
        msg.setWidth(800, Unit.PIXELS);

        final Button backButton = new Button(Page.message("button.back"));
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
        final boolean hasMessage = message != null;
        errorMessage.setValue(hasMessage ? message : "");
        errorMessage.setVisible(hasMessage);
    }

    /**
     * Sets new page content.
     * 
     * @param comp
     *            page content.
     */
    private void setContent(Component comp) {
        content.removeAllComponents();
        content.addComponent(comp);
    }

    /**
     * Renders an export page.
     * 
     * @param configOps
     *            config operations.
     * @param config
     *            config to export.
     * @param onDone
     *            "done" handler.
     * @param tempFile
     *            temporary file.
     * @return UI component.
     */
    public static Component render(ConfigOperations configOps,
            UISyncConfig config, int taskLimit, boolean showFilePath,
            final Runnable onDone, final File tempFile) {
        return new DropInExportPage(configOps, config, taskLimit, showFilePath,
                new Runnable() {
                    @Override
                    public void run() {
                        tempFile.delete();
                        onDone.run();
                    }
                }, tempFile).ui;
    }
}

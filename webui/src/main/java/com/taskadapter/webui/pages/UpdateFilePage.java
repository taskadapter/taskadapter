package com.taskadapter.webui.pages;

import static com.taskadapter.license.LicenseManager.TRIAL_MESSAGE;
import static com.taskadapter.webui.Page.message;

import java.util.List;

import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.webui.export.ConfirmExportFragment;

public final class UpdateFilePage {

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

    private final VerticalLayout ui;
    private final Label errorMessage;
    private final VerticalLayout content;

    private UpdateFilePage(ConfigOperations configOps, UISyncConfig config,
                           int taskLimit, Runnable onDone) {
        this.config = config;
        this.onDone = onDone;
        this.taskLimit = taskLimit;
        this.configOps = configOps;

        ui = new VerticalLayout();
        errorMessage = new Label("");
        errorMessage.addClassName("errorMessage");
        errorMessage.setVisible(false);
//        errorMessage.setWidth(500, Unit.PIXELS);
        ui.add(errorMessage);

        content = new VerticalLayout();
        ui.add(content);
        startLoading();
    }

    private void startLoading() {
        setContent(SyncActionComponents.renderLoadIndicator(config
                .getConnector1()));

        if (taskLimit < Integer.MAX_VALUE)
            LOGGER.info(TRIAL_MESSAGE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<GTask> tasks = config.loadTasksForUpdate();
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
        final Component component = ConfirmExportFragment.render(
                configOps,
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
//            content.setExpandRatio(component, 1f);
        } finally {
            VaadinSession.getCurrent().unlock();
        }
    }

    private void performExport(final List<GTask> selectedTasks) {
        if (selectedTasks.isEmpty()) {
            Notification.show(message("action.pleaseSelectTasks"));
            return;
        }

/*
        final ProgressIndicator saveProgress = SyncActionComponents
                .renderSaveIndicator(config.getConnector2());
        saveProgress.setProgress(0f);
        setContent(saveProgress);
*/


//        final MonitorWrapper wrapper = new MonitorWrapper(ProgressMonitorUtils.DUMMY_MONITOR);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    showExportResult(config.updateTasks(selectedTasks, ProgressMonitorUtils.DUMMY_MONITOR));
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
     * Shows task export result.
     */
    private void showExportResult(int updatedTasks) {
        final VerticalLayout ui = new VerticalLayout();

        final String text = message("updatePage.result",
                updatedTasks+"", config.getConnector2().getDestinationLocation(),
                config.getConnector1().getSourceLocation());
        ui.add(new Label(text));

        Button button = new Button(message("action.backToHomePage"), event -> onDone.run());
        ui.add(button);

        VaadinSession.getCurrent().lock();
        try {
            setContent(ui);
        } finally {
            VaadinSession.getCurrent().unlock();
        }
    }

    /**
     * Shows "no data loaded" content.
     */
    private void showNoDataLoaded() {
        final VerticalLayout res = new VerticalLayout();
        final Label label = new Label(message("updatePage.noDataWasLoaded"));
//        label.setWidth(800, Unit.PIXELS);

        Button backButton = new Button(message("button.back"), event -> onDone.run());
        res.add(label);
        res.add(backButton);

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
//        errorMessage.setValue(haveMessage ? message : "");
        errorMessage.setVisible(haveMessage);
    }

    /**
     * Sets new page content.
     * 
     * @param comp
     *            page content.
     */
    private void setContent(Component comp) {
        content.removeAll();
        content.add(comp);
    }

    /**
     * Renders an "update file" page.
     * 
     * @param configOps
     *            config operations.
     * @param config
     *            current config.
     * @param maxTasks
     *            maximal number of tasks.
     * @param onExit
     *            exit handler.
     * @return operation UI.
     */
    public static Component render(ConfigOperations configOps,
                                   UISyncConfig config,
                                   int maxTasks, Runnable onExit) {
        return new UpdateFilePage(configOps, config, maxTasks, onExit).ui;
    }

}

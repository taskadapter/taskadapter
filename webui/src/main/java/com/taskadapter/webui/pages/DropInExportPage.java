package com.taskadapter.webui.pages;

import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.Tracker;
import com.taskadapter.webui.results.ExportResultStorage;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import static com.taskadapter.license.LicenseManager.TRIAL_MESSAGE;

/**
 * Export page and export handler.
 */
public final class DropInExportPage {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DropInExportPage.class);

    /**
     * Sync config.
     */
    private final UISyncConfig config;

    /**
     * Maximal number of transferred tasks.
     */
    private final int taskLimit;

    /**
     * Temp file.
     */
    private final File tempFile;
    private final ExportHelper exportHelper;

    private final VerticalLayout ui;
    private final Label errorMessage;
    private final VerticalLayout content;

    private DropInExportPage(ExportResultStorage exportResultStorage, ConfigOperations configOps, UISyncConfig config,
                             int taskLimit, boolean showFilePath, Runnable onDone, File tempFile, Tracker tracker) {
        this.config = config;
        this.taskLimit = taskLimit;
        this.tempFile = tempFile;

        ui = new VerticalLayout();
        errorMessage = new Label("");
        errorMessage.addStyleName("errorMessage");
        errorMessage.setVisible(false);
        errorMessage.setWidth(500, Unit.PIXELS);
        ui.addComponent(errorMessage);

        content = new VerticalLayout();
        ui.addComponent(content);
        exportHelper = new ExportHelper(exportResultStorage, onDone, showFilePath, content, config);
        startLoading();
    }

    /**
     * Starts data loading.
     */
    private void startLoading() {
        setContent(SyncActionComponents.renderLoadIndicator(Page.message("export.dropInFile")));

        if (taskLimit < Integer.MAX_VALUE)
            LOGGER.info(TRIAL_MESSAGE);

        new Thread(() -> {
            try {
                final List<GTask> tasks = config.loadDropInTasks(tempFile,
                        taskLimit);
                exportHelper.onTasksLoaded(tasks);
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
        }).start();
    }

    /**
     * Shows load error message.
     *
     * @param message message to show.
     */
    private void showLoadErrorMessage(String message) {
        VaadinSession.getCurrent().lock();
        try {
            showErrorMessage(message);
            exportHelper.showNoDataLoaded();
        } finally {
            VaadinSession.getCurrent().unlock();
        }
    }

    /**
     * Shows or hides an error message.
     *
     * @param message error message. If null, errors are hidden.
     */
    private void showErrorMessage(String message) {
        final boolean hasMessage = message != null;
        errorMessage.setValue(hasMessage ? message : "");
        errorMessage.setVisible(hasMessage);
    }

    /**
     * Sets new page content.
     *
     * @param comp page content.
     */
    private void setContent(Component comp) {
        content.removeAllComponents();
        content.addComponent(comp);
    }

    public static Component render(ExportResultStorage exportResultStorage, ConfigOperations configOps,
                                   UISyncConfig config, int taskLimit, boolean showFilePath,
                                   final Runnable onDone, final File tempFile, Tracker tracker) {
        return new DropInExportPage(exportResultStorage, configOps, config, taskLimit, showFilePath,
                () -> {
                    tempFile.delete();
                    onDone.run();
                }, tempFile, tracker).ui;
    }
}

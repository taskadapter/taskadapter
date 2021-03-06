package com.taskadapter.webui.pages;

import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.reporting.ErrorReporter;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.webui.results.ExportResultStorage;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Export page and export handler.
 */
public final class DropInExportPage extends VerticalLayout {
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

    private final Label errorMessage;
    private final VerticalLayout content;

    private DropInExportPage(ExportResultStorage exportResultStorage, ConfigOperations configOps, UISyncConfig config,
                             int taskLimit, boolean showFilePath, Runnable onDone, File tempFile,
                             ErrorReporter errorReporter) {
        this.config = config;
        this.taskLimit = taskLimit;
        this.tempFile = tempFile;

        errorMessage = new Label("");
        errorMessage.addClassName("errorMessage");
        errorMessage.setVisible(false);
        errorMessage.setWidth("500px");
        add(errorMessage);

        content = new VerticalLayout();
        add(content);
        exportHelper = new ExportHelper(exportResultStorage, onDone, showFilePath, content, config, configOps,
                errorReporter);
        startLoading();
    }

    /**
     * Starts data loading.
     */
    private void startLoading() {
//        setContent(SyncActionComponents.renderLoadIndicator(Page.message("export.dropInFile")));

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
        errorMessage.setText(hasMessage ? message : "");
        errorMessage.setVisible(hasMessage);
    }

    private void setContent(Component comp) {
        content.removeAll();
        content.add(comp);
    }

    public static Component render(UI ui, ExportResultStorage exportResultStorage, ConfigOperations configOps,
                                   UISyncConfig config, int taskLimit, boolean showFilePath,
                                   final Runnable onDone, final File tempFile, ErrorReporter errorReporter) {
        return new DropInExportPage(exportResultStorage, configOps, config, taskLimit, showFilePath,
                () -> {
                    tempFile.delete();
                    onDone.run();
                }, tempFile, errorReporter);
    }
}

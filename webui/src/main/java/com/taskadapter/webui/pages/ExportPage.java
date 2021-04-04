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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.taskadapter.license.LicenseManager.TRIAL_MESSAGE;

/**
 * Export page and export handler.
 */
public final class ExportPage extends VerticalLayout {
    private static final Logger log = LoggerFactory.getLogger(ExportPage.class);

    private final ExportHelper exportHelper;

    private final UI ui;
    /**
     * Sync config.
     */
    private final UISyncConfig config;

    /**
     * Maximal number of transferred tasks.
     */
    private final int taskLimit;
    private final ErrorReporter errorReporter;

    private final Label errorMessage;
    private final VerticalLayout content;

    public ExportPage(UI ui, ExportResultStorage exportResultStorage, UISyncConfig config,
                      int taskLimit, boolean showFilePath, Runnable onDone,
                      ConfigOperations configOps,
                      ErrorReporter errorReporter) {
        this.ui = ui;
        this.config = config;
        this.taskLimit = taskLimit;
        this.errorReporter = errorReporter;

        errorMessage = new Label("");
        errorMessage.addClassName("errorMessage");
        errorMessage.setVisible(false);
        errorMessage.setWidth("500px");
        add(errorMessage);

        content = new VerticalLayout();
        add(errorMessage, content);
        exportHelper = new ExportHelper(exportResultStorage, onDone, showFilePath, content, config, configOps,
                errorReporter);
    }

    public void startLoading() {
        Component renderLoadIndicator = SyncActionComponents.renderLoadIndicator(config.getConnector1());
        content.add(renderLoadIndicator);

        if (taskLimit < Integer.MAX_VALUE)
            log.info(TRIAL_MESSAGE);

        new Thread(() -> {
            try {
                log.info("Loading from " + config.getConnector1().getConnectorTypeId()
                        + " " + config.getConnector1().getSourceLocation());
                final List<GTask> tasks = UISyncConfig.loadTasks(config, taskLimit);
                log.info("Loaded " + tasks.size() + " tasks");
                exportHelper.onTasksLoaded(tasks);
            } catch (CommunicationException e) {
                final String message = config.getConnector1().decodeException(e);
                showLoadErrorMessage(ui, message);
                errorReporter.reportIfAllowed(config, e);
                log.error("transport error: " + message, e);
            } catch (ConnectorException e) {
                showLoadErrorMessage(ui, config.getConnector1().decodeException(e));
                errorReporter.reportIfAllowed(config, e);
                log.error(e.getMessage(), e);
            } catch (RuntimeException e) {
                showLoadErrorMessage(ui, "Internal error: " + e.getMessage());
                errorReporter.reportIfAllowed(config, e);
                log.error(e.getMessage(), e);
            }
        }).start();
    }

    /**
     * Shows load error message.
     *
     * @param message message to show.
     */
    private void showLoadErrorMessage(UI ui, String message) {
        ui.access(() -> {
            showErrorMessage(message);
            exportHelper.showNoDataLoaded();
        });
    }

    /**
     * Shows or hides an error message.
     *
     * @param message error message. If null, errors are hidden.
     */
    private void showErrorMessage(String message) {
        final boolean haveMessage = message != null;
        errorMessage.setText(haveMessage ? message : "");
        errorMessage.setVisible(haveMessage);
    }
}

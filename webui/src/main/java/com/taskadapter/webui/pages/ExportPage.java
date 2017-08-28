package com.taskadapter.webui.pages;

import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.webui.Tracker;
import com.taskadapter.webui.results.ExportResultStorage;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.taskadapter.license.LicenseManager.TRIAL_MESSAGE;

/**
 * Export page and export handler.
 */
public final class ExportPage {
    private static final Logger log = LoggerFactory.getLogger(ExportPage.class);

    private final ExportHelper exportHelper;

    /**
     * Sync config.
     */
    private final UISyncConfig config;

    /**
     * Maximal number of transferred tasks.
     */
    private final int taskLimit;

    public final VerticalLayout ui;
    private final Label errorMessage;
    private final VerticalLayout content;

    public ExportPage(ExportResultStorage exportResultStorage, ConfigOperations configOps, UISyncConfig config,
                      int taskLimit, boolean showFilePath, Runnable onDone, Tracker tracker) {
        this.config = config;
        this.taskLimit = taskLimit;

        ui = new VerticalLayout();
        errorMessage = new Label("");
        errorMessage.addStyleName("errorMessage");
        errorMessage.setVisible(false);
        errorMessage.setWidth(500, Unit.PIXELS);
        ui.addComponent(errorMessage);

        content = new VerticalLayout();
        ui.addComponent(content);
        exportHelper = new ExportHelper(configOps, exportResultStorage, tracker, onDone, showFilePath, content, config);

        startLoading();
    }

    /**
     * Starts data loading.
     */
    private void startLoading() {
        setContent(SyncActionComponents.renderLoadIndicator(config.getConnector1()));

        if (taskLimit < Integer.MAX_VALUE)
            log.info(TRIAL_MESSAGE);

        new Thread(() -> {
            try {
                log.info("Loading from " + config.connector1().getConnectorTypeId()
                        + " " + config.getConnector1().getSourceLocation());
                final List<GTask> tasks = UISyncConfig.loadTasks(config, taskLimit);
                log.info("Loaded " + tasks.size() + " tasks");
                exportHelper.onTasksLoaded(tasks);
            } catch (CommunicationException e) {
                final String message = config.getConnector1()
                        .decodeException(e);
                showLoadErrorMessage(message);
                log.error("transport error: " + message, e);
            } catch (ConnectorException e) {
                showLoadErrorMessage(config.getConnector1()
                        .decodeException(e));
                log.error(e.getMessage(), e);
            } catch (RuntimeException e) {
                showLoadErrorMessage("Internal error: " + e.getMessage());
                log.error(e.getMessage(), e);
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
        final boolean haveMessage = message != null;
        errorMessage.setValue(haveMessage ? message : "");
        errorMessage.setVisible(haveMessage);
    }

    private void setContent(Component comp) {
        content.removeAllComponents();
        content.addComponent(comp);
    }
}

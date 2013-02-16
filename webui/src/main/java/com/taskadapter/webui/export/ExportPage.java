package com.taskadapter.webui.export;

import com.google.common.base.Strings;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.TaskError;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.core.RemoteIdUpdater;
import com.taskadapter.core.TaskLoader;
import com.taskadapter.core.TaskSaver;
import com.taskadapter.model.GTask;
import com.taskadapter.web.configeditor.file.FileDownloadResource;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.MonitorWrapper;
import com.taskadapter.webui.data.ExceptionFormatter;
import com.vaadin.server.FileDownloader;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static com.taskadapter.license.LicenseManager.TRIAL_MESSAGE;
import static com.taskadapter.license.LicenseManager.TRIAL_TASKS_NUMBER_LIMIT;

public class ExportPage extends ActionPage {

    private static final String TRANSPORT_ERROR = "There was a problem communicating with the server. " +
            "Please check that the server name is valid and the server is accessible.";

    private static final int MAX_TASKS_TO_LOAD = 99999999;

    private final Logger logger = LoggerFactory.getLogger(ExportPage.class);

    private VerticalLayout donePanel = new VerticalLayout();

    private TaskSaveResult result;

    public ExportPage(UISyncConfig config) {
        super(config);
    }

    @Override
    public String getPageGoogleAnalyticsID() {
        return "export_confirmation";
    }

    @Override
    protected void loadData() {
        try {
            Connector<?> sourceConnector = config.getConnector1().createConnectorInstance();

            int maxTasksToLoad;
            if (services.getLicenseManager().isSomeValidLicenseInstalled()) {
                maxTasksToLoad = MAX_TASKS_TO_LOAD;
            } else {
                logger.info(TRIAL_MESSAGE);
                maxTasksToLoad = TRIAL_TASKS_NUMBER_LIMIT;
            }

            this.loadedTasks = TaskLoader.loadTasks(
                    maxTasksToLoad, sourceConnector,
                    config.getConnector1().getLabel(),
                    config.generateSourceMappings(),
                    ProgressMonitorUtils.getDummyMonitor());
        } catch (CommunicationException e) {
            String message = getErrorMessageForSourceException(e);
            showErrorMessageOnPage(message);
            logger.error("transport error: " + message, e);
        } catch (ConnectorException e) {
            showErrorMessageOnPage(ExceptionFormatter.format(e));
            logger.error(e.getMessage(), e);
        } catch (RuntimeException e) {
            showErrorMessageOnPage("Internal error: " + e.getMessage());
            logger.error(e.getMessage(), e);
        }
    }

    private String getErrorMessageForSourceException(CommunicationException e) {
        return config.getConnector1().decodeException(e);
    }

    private void showErrorMessageOnPage(String errorMessage) {
        Label errorLabel = new Label(errorMessage);
        errorLabel.addStyleName("errorMessage");
        mainPanel.addComponent(errorLabel);
    }

    @Override
    protected String getInitialText() {
        return "Will load data from "
                + config.getConnector1().getSourceLocation() + " ("
                + config.getConnector1().getLabel() + ")";
    }

    @Override
    protected String getNoDataLoadedText() {
        return "No data was loaded using the given criteria.";
    }

    // TODO Alexey: move this "done" thing into a separate Page class
    @Override
    protected VerticalLayout getDoneInfoPanel() {
        donePanel.setWidth("600px");
        donePanel.setStyleName("export-panel");

        addDateTimeInfo();
        addFromToPanel();
        final TaskSaveResult saveResult = result;
        if (saveResult != null) {
            addDownloadButtonIfServerMode(saveResult.getTargetFileAbsolutePath());
            addExportNumbersStats(saveResult);
            addFileInfoIfNeeded(saveResult);
        }

        if (!result.getGeneralErrors().isEmpty() || !result.getTaskErrors().isEmpty()) {
            addErrors(config.getConnector2(), result.getGeneralErrors(), result.getTaskErrors());
        }
        return donePanel;
    }

    private void addDateTimeInfo() {
        String time = new SimpleDateFormat("MMMM dd, yyyy  HH:mm").format(Calendar.getInstance().getTime());
        // TODO externalize, i18n
        Label label = new Label("<strong>Export completed on</strong> <em>" + time + "</em>");
        label.setContentMode(ContentMode.HTML);

        donePanel.addComponent(label);
    }

    private void addFromToPanel() {
        donePanel.addComponent(createdExportResultLabel("From", config.getConnector1().getSourceLocation()));
    }

    private void addExportNumbersStats(TaskSaveResult result) {
        donePanel.addComponent(createdExportResultLabel("Created tasks", String.valueOf(result.getCreatedTasksNumber())));
        donePanel.addComponent(createdExportResultLabel("Updated tasks", String.valueOf(result.getUpdatedTasksNumber()) + "<br/><br/>"));
    }

    private HorizontalLayout createdExportResultLabel(String labelName, String labelValue) {
        Label lName = new Label("<strong>" + labelName + ":</strong>");
        lName.setContentMode(ContentMode.HTML);
        lName.setWidth("98px");

        Label lValue = new Label("<em>" + labelValue + "</em>");
        lValue.setContentMode(ContentMode.HTML);

        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(lName);
        hl.addComponent(lValue);
        hl.addStyleName("export-result");

        return hl;
    }
    
    private void addErrors(UIConnectorConfig connector, List<Throwable> generalErrors, List<TaskError<Throwable>> taskErrors) {
        donePanel.addComponent(new Label("There were some problems during export:"));
        String errorText = "";
        for (Throwable e : generalErrors) {
            errorText += quot(connector.decodeException(e)) + "<br/>";
        }
        for (TaskError<Throwable> error : taskErrors) {
            errorText += "Task " + error.getTask().getId() + " (\""
                    + error.getTask().getSummary() + "\"): "
                    + connector.decodeException(error.getErrors());
        }
        final Label errorTextLabel = new Label(errorText);
        errorTextLabel.addStyleName("errorMessage");
        errorTextLabel.setContentMode(ContentMode.HTML);
        donePanel.addComponent(errorTextLabel);
    }

    private static String quot(String str) {
        return str.replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private void addFileInfoIfNeeded(TaskSaveResult result) {
        if (result.getTargetFileAbsolutePath() != null && (services.getSettingsManager().isTAWorkingOnLocalMachine())) {
            Label label = new Label("<strong>Path to export file:</strong> <em>" + result.getTargetFileAbsolutePath() + "</em>");
            label.setContentMode(ContentMode.HTML);

            donePanel.addComponent(label);
        }
    }

    private void addDownloadButtonIfServerMode(String targetFileAbsolutePath) {
        if (!services.getSettingsManager().isTAWorkingOnLocalMachine() && !Strings.isNullOrEmpty(targetFileAbsolutePath)) {
            addDownloadButton(targetFileAbsolutePath);
        }
    }

    private void addDownloadButton(final String targetFileAbsolutePath) {
        Button downloadButton = new Button("Download file");
        File file = new File(targetFileAbsolutePath);
        FileDownloadResource resource = new FileDownloadResource(file);
        FileDownloader downloader = new FileDownloader(resource);
        downloader.extend(downloadButton);
        donePanel.addComponent(downloadButton);
    }

    @Override
    protected void saveData(List<GTask> tasks) throws ConnectorException {
        saveProgress.setValue(0f);
        final MonitorWrapper wrapper = new MonitorWrapper(saveProgress);

        final Connector<?> destinationConnector = config.getConnector2().createConnectorInstance();
        try {
            result = TaskSaver.save(destinationConnector, config.getConnector2().getDestinationLocation(),
                    config.generateTargetMappings(),
                    tasks,
                    wrapper);
            RemoteIdUpdater.updateRemoteIds(result.getIdToRemoteKeyMap(), 
                    config.generateSourceMappings(),
                    config.getConnector1().createConnectorInstance());
        } catch (ConnectorException e) {
            showErrorMessageOnPage(ExceptionFormatter.format(e));
            logger.error(e.getMessage(), e);
        }
    }

}

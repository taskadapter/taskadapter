package com.taskadapter.webui;

import com.google.common.base.Strings;
import com.taskadapter.config.TAFile;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.common.TransportException;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.connector.definition.TaskError;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.core.SyncRunner;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.file.FileDownloadResource;
import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ExportPage extends ActionPage {

    private final Logger logger = LoggerFactory.getLogger(ExportPage.class);

    private VerticalLayout donePanel = new VerticalLayout();

    // TODO i18n
    private static final String TRANSPORT_ERROR = "There was a problem communicating with the server. " +
            "Please check that the server name is valid and the server is accessible.";

    private SyncRunner runner;
    private SyncResult result;

    public ExportPage(Connector connectorFrom, Connector connectorTo, TAFile taFile) {
        super(connectorFrom, connectorTo, taFile);
    }

    @Override
    public String getPageGoogleAnalyticsID() {
        return "export_confirmation";
    }

    @Override
    protected void loadData() {
        runner = new SyncRunner(services.getLicenseManager());
        runner.setConnectorFrom(connectorFrom);
        try {
            this.loadedTasks = runner.load(ProgressMonitorUtils.getDummyMonitor());
        } catch (CommunicationException e) {
            String message = getErrorMessageForException(e);
            showErrorMessageOnPage(message);
            logger.error("transport error: " + message, e);
        } catch (ConnectorException e) {
            showErrorMessageOnPage(e.getMessage());
            logger.error(e.getMessage(), e);
        } catch (RuntimeException e) {
            showErrorMessageOnPage("Internal error: " + e.getMessage());
            logger.error(e.getMessage(), e);
        }
    }

    private String getErrorMessageForException(CommunicationException e) {
        if (EditorUtil.getRoot(e) instanceof UnknownHostException) {
            return "Unknown host";
        } else {
            return TRANSPORT_ERROR;
        }
    }

    private void showErrorMessageOnPage(String errorMessage) {
        Label errorLabel = new Label(errorMessage);
        errorLabel.addStyleName("errorMessage");
        mainPanel.addComponent(errorLabel);
    }

    @Override
    protected String getInitialText() {
        return "Will load data from " + connectorFrom.getConfig().getSourceLocation() + " (" + connectorFrom.getConfig().getLabel() + ")";
    }

    @Override
    protected String getNoDataLoadedText() {
        return "No data was loaded using the given criteria.";
    }

    // TODO Alexey: move this "done" thing into a separate Page class
    @Override
    protected VerticalLayout getDoneInfoPanel() {
        donePanel.setWidth("600px");

        addDateTimeInfo();
        addFromToPanel();
        addDownloadButtonIfServerMode(result.getTargetFileAbsolutePath());
        addExportNumbersStats();
        addFileInfoIfNeeded();

        if (result.hasErrors()) {
            addErrors();
        }
        return donePanel;
    }

    private void addDateTimeInfo() {
        String LOG_DATE_FORMAT = "d/MMM HH:mm";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(LOG_DATE_FORMAT);

        String time = dateFormatter.format(Calendar.getInstance().getTime());
        donePanel.addComponent(new Label("Export completed on " + time));
    }

    private void addFromToPanel() {
        Label infoLabel = new Label("From: " + connectorFrom.getConfig().getSourceLocation()
                + "<br>To: " + connectorTo.getConfig().getTargetLocation());
        infoLabel.addStyleName("export-result");
        infoLabel.setContentMode(Label.CONTENT_XHTML);
        donePanel.addComponent(infoLabel);
    }

    private void addExportNumbersStats() {
        Label infoLabel = new Label("Created tasks: " + result.getCreateTasksNumber()
                + "<br>Updated tasks: " + result.getUpdatedTasksNumber() + "<br><br>");
        infoLabel.addStyleName("export-result");
        infoLabel.setContentMode(Label.CONTENT_XHTML);
        donePanel.addComponent(infoLabel);
    }

    private void addErrors() {
        donePanel.addComponent(new Label("There were some problems during export:"));
        String errorText = "";
        for (String e : result.getGeneralErrors()) {
            errorText += e + "<br>";
        }
        for (TaskError e : result.getErrors()) {
            errorText += getMessageForTask(e) + "<br>";
        }
        Label errorTextLabel = new Label(errorText);
        errorTextLabel.addStyleName("errorMessage");
        errorTextLabel.setContentMode(Label.CONTENT_XHTML);
        donePanel.addComponent(errorTextLabel);
    }

    private void addFileInfoIfNeeded() {
        if (result.getTargetFileAbsolutePath() != null && (services.getSettingsManager().isTAWorkingOnLocalMachine())) {
            donePanel.addComponent(new Label("File absolute path: " + result.getTargetFileAbsolutePath()));
        }
    }

    private void addDownloadButtonIfServerMode(String targetFileAbsolutePath) {
        if (!services.getSettingsManager().isTAWorkingOnLocalMachine() && !Strings.isNullOrEmpty(targetFileAbsolutePath)) {
            addDownloadButton(targetFileAbsolutePath);
        }
    }

    private void addDownloadButton(final String targetFileAbsolutePath) {
        Button downloadButton = new Button("Download");
        downloadButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                File file = new File(targetFileAbsolutePath);
                Application application = navigator.getApplication();
                FileDownloadResource resource = new FileDownloadResource(file, application);
                application.getMainWindow().open(resource);
            }
        });
        donePanel.addComponent(downloadButton);
    }

    private String getMessageForTask(TaskError e) {
        return "Task " + e.getTask().getId() + " (\"" + e.getTask().getSummary() + "\"): " + e.getErrors();
    }

    @Override
    protected void saveData() throws ConnectorException {
        saveProgress.setValue(0);
        MonitorWrapper wrapper = new MonitorWrapper(saveProgress);
		runner.setDestination(connectorTo);
        result = runner.save(wrapper);
    }

}

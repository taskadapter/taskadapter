package com.taskadapter.webui;

import com.taskadapter.config.TAFile;
import com.taskadapter.connector.common.TaskSaver;
import com.taskadapter.connector.common.TransportException;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.connector.definition.TaskError;
import com.taskadapter.core.SyncRunner;
import com.taskadapter.web.configeditor.EditorUtil;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Alexey Skorokhodov
 */
public class ExportPage extends ActionPage {
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
            this.loadedTasks = runner.load(null);
        } catch (TransportException e) {
            String message = getErrorMessageForException(e);
            showErrorMessageOnPage(message);
            e.printStackTrace();
        } catch (RuntimeException e) {
            showErrorMessageOnPage(e.getMessage());
            e.printStackTrace();
        }
    }

    private String getErrorMessageForException(TransportException e) {
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
        return "Will load data from " + connectorFrom.getConfig().getSourceLocation() + " (" + connectorFrom.getDescriptor().getLabel() + ")";
    }

    @Override
    protected String getNoDataLoadedText() {
        return "No data was loaded using the given criteria.";
    }

    // TODO Alexey: maybe move this "done" thing into a separate "Page" class?
    @Override
    protected VerticalLayout getDoneInfoPanel() {
        VerticalLayout donePanel = new VerticalLayout();
        donePanel.setWidth("600px");

        String LOG_DATE_FORMAT = "d/MMM HH:mm";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(LOG_DATE_FORMAT);

        String time = dateFormatter.format(Calendar.getInstance().getTime());
        donePanel.addComponent(new Label("Completed export at " + time));

        Label infoLabel = new Label("From: " + connectorFrom.getConfig().getSourceLocation()
                + "<br>To: " + connectorTo.getConfig().getTargetLocation());
        infoLabel.addStyleName("export-result");
        infoLabel.setContentMode(Label.CONTENT_XHTML);
        donePanel.addComponent(infoLabel);

        if (result.getMessage() != null) {
            donePanel.addComponent(new Label(result.getMessage()));
        }

        infoLabel = new Label("Created tasks: " + result.getCreateTasksNumber()
                + "<br>Updated tasks: " + result.getUpdatedTasksNumber() + "<br><br>");
        infoLabel.addStyleName("export-result");
        infoLabel.setContentMode(Label.CONTENT_XHTML);
        donePanel.addComponent(infoLabel);

        if (result.hasErrors()) {
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
        return donePanel;
    }

    private String getMessageForTask(TaskError e) {
        return "Task " + e.getTask().getId() + " (\"" + e.getTask().getSummary() + "\"): " + e.getErrors();
    }

    @Override
    protected void saveData() {
        saveProgress.setValue(0);
        MonitorWrapper wrapper = new MonitorWrapper(saveProgress);
        final TaskSaver taskSaver = connectorTo.getDescriptor().getTaskSaver(connectorTo.getConfig());
        runner.setTaskSaver(taskSaver);
        result = runner.save(wrapper);
    }

}

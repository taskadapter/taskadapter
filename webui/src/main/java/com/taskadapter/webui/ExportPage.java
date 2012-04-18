package com.taskadapter.webui;

import com.taskadapter.config.TAFile;
import com.taskadapter.connector.common.TaskSaver;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.connector.definition.TaskError;
import com.taskadapter.core.SyncRunner;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Alexey Skorokhodov
 */
public class ExportPage extends ActionPage {
    private SyncRunner runner;
    private SyncResult result;

    public ExportPage(Connector connectorFrom, Connector connectorTo, TAFile taFile) {
        super(connectorFrom, connectorTo, taFile);
    }

    @Override
    public String getPageTitle() {
        return "Export confirmation: from " + connectorFrom.getConfig().getLabel() + " to " + connectorTo.getConfig().getLabel();
    }

    @Override
    protected void loadData() {
        final TaskSaver taskSaver = connectorTo.getDescriptor()
                .getTaskSaver(connectorTo.getConfig());
        runner = new SyncRunner();
        runner.setConnectorFrom(connectorFrom);
        runner.setTaskSaver(taskSaver);
        try {
            this.loadedTasks = runner.load(null);
        } catch (RuntimeException e) {
            mainPanel.addComponent(new Label("Can't load data. " + e.toString()));
            // TODO log properly
            e.printStackTrace();
        }
    }

    @Override
    protected String getInitialText() {
        return "Will load data from " + connectorFrom.getConfig().getSourceLocation() + " (" + connectorFrom.getDescriptor().getLabel() + ")";
    }

    @Override
    protected String getNoDataLoadedText() {
        return "No data was loaded using the given criteria";
    }

    @Override
    protected VerticalLayout getDoneInfoPanel() {
        VerticalLayout donePanel = new VerticalLayout();

        String LOG_DATE_FORMAT = "d/MMM HH:mm";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(LOG_DATE_FORMAT);
        String msg = "";

        String time = dateFormatter.format(Calendar.getInstance().getTime());
        donePanel.addComponent(new Label(time + ": Completed export from " + connectorFrom.getConfig().getSourceLocation() +
                " to " + connectorTo.getConfig().getTargetLocation() + "."));

        if (result.getMessage() != null) {
            msg += " " + result.getMessage();
        }
        msg += "\nCreated tasks: " + result.getCreateTasksNumber()
                + "  Updated tasks: " + result.getUpdatedTasksNumber();

        donePanel.addComponent(new Label(msg));

        if (result.hasErrors()) {
            msg = "Server reported some errors:";
            for (String e : result.getGeneralErrors()) {
                msg += "\n  " + e;
            }
            for (TaskError e : result.getErrors()) {
                msg += getMessageForTask(e);
            }

            donePanel.addComponent(new Label(msg));
        }

        return donePanel;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private String getMessageForTask(TaskError e) {
        return "\n Task " + e.getTask().getId() + " (\"" + e.getTask().getSummary() + "\"): " + e.getErrors();
    }

    @Override
    protected void saveData() {
        saveProgress.setValue(0);
        runner.setTasks(loadedTasks);
        MonitorWrapper wrapper = new MonitorWrapper(saveProgress);
        result = runner.save(wrapper);
    }

}

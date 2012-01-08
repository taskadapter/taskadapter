package com.taskadapter.webui.action;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.core.Updater;
import com.taskadapter.model.GTask;
import com.taskadapter.webui.MonitorWrapper;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Window;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class UpdateFileBasedConnectorAction extends ConnectorAction {

    public UpdateFileBasedConnectorAction(Window window,
                                          Connector remoteConnector, Connector fileConnector) {
        super(window, remoteConnector, fileConnector);
    }

    // TODO refactor this huge horrible method
    @Override
    public void startExport() {

        final Updater updater = new Updater(connectorTo, connectorFrom);

        final AtomicReference<List<GTask>> result = new AtomicReference<List<GTask>>();
        try {
            ProgressIndicator progressIndicator = new ProgressIndicator();
            try {
                MonitorWrapper wrapper = new MonitorWrapper(progressIndicator);
                updater.loadTasksFromFile(wrapper);
                updater.removeTasksWithoutRemoteIds();
                result.set(updater.getExistingTasks());
            } catch (Exception e) {
                showAndLogError(e);
            }

            List<GTask> tree = result.get();
            String mspLocation = connectorFrom.getConfig().getTargetLocation();

            // tree can be NULL if there was an exception above. e.g. when a
            // file wasn't found
            if (tree != null) {
                if (!tree.isEmpty()) {

                    // CONFIRM

                    String dialogTitle = "Update file \"" + mspLocation + "\"";
                    List<GTask> confirmedTasks = confirm(tree, dialogTitle);
                    if (!confirmedTasks.isEmpty()) {
                        updater.setConfirmedTasks(confirmedTasks);

                        MonitorWrapper wrapper = new MonitorWrapper(progressIndicator);
                        updater.setMonitor(wrapper);
                        updater.loadExternalTasks();
                        updater.saveFile();

                    }
                }
            }
        } catch (Exception e) {
            showAndLogError(e);
        }
    }
}

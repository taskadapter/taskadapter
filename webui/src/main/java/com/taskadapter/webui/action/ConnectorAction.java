package com.taskadapter.webui.action;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.model.GTask;
import com.vaadin.ui.Window;

import java.text.SimpleDateFormat;
import java.util.List;

public abstract class ConnectorAction {
    private static final String LOG_DATE_FORMAT = "d/MMM HH:mm";
    protected SimpleDateFormat dateFormatter = new SimpleDateFormat(LOG_DATE_FORMAT);

    protected Window window;
    protected final Connector connectorFrom;
    protected final Connector connectorTo;

    public ConnectorAction(Window window, Connector connectorFrom, Connector connectorTo) {
        this.window = window;
        this.connectorFrom = connectorFrom;
        this.connectorTo = connectorTo;
    }

    public abstract void startExport();

    // TODO delete this
    protected List<GTask> confirm(List<GTask> treeToSave, String title) {
        ConfirmationPage dlg = new ConfirmationPage(treeToSave, connectorTo, null);
        Window dlgWindow = new Window();
//        dlgWindow.setContent(dlg);
        window.addWindow(dlgWindow);

//        if (dlg.isOK()) {
//            if (!dlg.getFieldsMapping().equals(
//                    connectorTo.getPartialConfig().getFieldsMapping())) {
//                connectorTo.getPartialConfig()
//                        .setFieldsMapping(dlg.getFieldsMapping());
//                editor.doSave(null);
//            }
//        }
        return dlg.getSelectedRootLevelTasks();
    }

    protected void showAndLogError(Exception e) {
        e.printStackTrace();
    }
}

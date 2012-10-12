package com.taskadapter.webui.export;


import com.taskadapter.config.StorageException;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.FileBasedConnector;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPOutputFileNameNotSetException;
import com.taskadapter.web.MessageDialog;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.Navigator;

import java.io.File;
import java.util.Arrays;

/**
 * Data exporter. Always exports from connector1 to connector2.
 *
 */
public class Exporter {

    private static final String UPDATE = "Only update tasks present in the file";
    private static final String OVERWRITE = "Overwrite";
    private static final String CREATE = "Create";
    private static final String CANCEL = "Cancel";

    private final Navigator navigator;
    private final UISyncConfig syncConfig;

    public Exporter(Navigator navigator, UISyncConfig syncConfig) {
        this.navigator = navigator;
        this.syncConfig = syncConfig;
    }

    public void export() {
//        String dataHolderLabel = null;
        String errorMessage = null;
        boolean valid = true;

        try {
            syncConfig.getConnector1().validateForLoad();
        } catch (ValidationException e) {
//            dataHolderLabel = resolver.getSourceConfig().getLabel();
            errorMessage = e.getMessage();
            valid = false;
        }

        // TODO refactor these if (valid), if (valid) checks
        if (valid) {
            try {
                syncConfig.getConnector2().validateForSave();

            } catch (MSPOutputFileNameNotSetException e) {
                // TODO !!! added for Maxim K:
                // catch (MSPOutputFileNameNotSetException e) { ... } - autofixConfigForSave

                // auto generate output file name (for MSP local mode)
                final Services services = navigator.getServices();
                final String userName = services.getAuthenticator().getUserName();
                String absoluteFileName = services.getFileManager().createDefaultMSPFileName(userName);

                ((MSPConfig) syncConfig.getConnector2().getRawConfig()).setOutputAbsoluteFilePath(absoluteFileName);
                ((MSPConfig) syncConfig.getConnector2().getRawConfig()).setInputAbsoluteFilePath(absoluteFileName);
                try {
                    services.getUIConfigStore().saveConfig(userName, syncConfig);
                } catch (StorageException e1) {
                    // FIXME:
                    // TODO log and report error instead of printing stacktrace
                    e1.printStackTrace();
                }

            } catch (ValidationException e) {
                errorMessage = e.getMessage();
                valid = false;
            }
        }

        if (valid) {
            processBasedOnDestinationConnectorType();
        } else {
            navigator.showConfigureTaskPage(syncConfig, errorMessage);
        }
    }

    private void processBasedOnDestinationConnectorType() {
        Connector<?> destinationConnector = syncConfig.getConnector2().createConnectorInstance();
        if (destinationConnector instanceof FileBasedConnector) {
            processFile((FileBasedConnector) destinationConnector);
        } else {
            startRegularExport();
        }
    }

    private void processFile(FileBasedConnector connectorTo) {
        if (connectorTo.fileExists()) {
            String fileName = new File(connectorTo.getAbsoluteOutputFileName()).getName();
            MessageDialog messageDialog = new MessageDialog(
                    "Choose operation", "Destination file already exists:<br><b>" + fileName + "</b>",
                    Arrays.asList(UPDATE, OVERWRITE, CANCEL),
                    new MessageDialog.Callback() {
                        public void onDialogResult(String answer) {
                            processFileAction(answer);
                        }
                    }
            );
            messageDialog.setWidth("465px");

            navigator.addWindow(messageDialog);
        } else {
            processFileAction(CREATE);
        }
    }

    private void processFileAction(String action) {
        if (action.equals(UPDATE)) {
            startUpdateFile();
        } else if (action.equals(OVERWRITE) || action.equals(CREATE)) {
            startRegularExport();
        }
    }
    
    private void startUpdateFile() {
        UpdateFilePage page = new UpdateFilePage(syncConfig);
        navigator.show(page);
    }

    private void startRegularExport() {
        ExportPage page = new ExportPage(syncConfig);
        navigator.show(page);
    }
}

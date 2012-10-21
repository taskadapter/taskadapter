package com.taskadapter.webui.export;


import com.taskadapter.config.StorageException;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.FileBasedConnector;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPOutputFileNameNotSetException;
import com.taskadapter.web.MessageDialog;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.Navigator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

/**
 * Data exporter. Always exports from connector1 to connector2.
 */
public class Exporter {

    private final Logger logger = LoggerFactory.getLogger(Exporter.class);

    private static final String UPDATE = "Only update tasks present in the file";
    private static final String OVERWRITE = "Overwrite";
    private static final String CREATE = "Create";
    private static final String CANCEL = "Cancel";

    private final Services services;
    private final Navigator navigator;
    private final UISyncConfig syncConfig;

    public Exporter(Services services, Navigator navigator, UISyncConfig syncConfig) {
        this.services = services;
        this.navigator = navigator;
        this.syncConfig = syncConfig;
    }

    public void export() {
        String errorMessage = null;
        boolean valid = true;

        try {
            syncConfig.getConnector1().validateForLoad();
        } catch (ValidationException e) {
            errorMessage = e.getMessage();
            valid = false;
        }

        // TODO refactor these if (valid), if (valid) checks
        if (valid) {
            UIConnectorConfig connector2 = syncConfig.getConnector2();
            try {
                connector2.validateForSave();

            } catch (MSPOutputFileNameNotSetException e) {
                // TODO !!! added for Maxim K:
                // catch (MSPOutputFileNameNotSetException e) { ... } - autofixConfigForSave

                // auto generate output file name (for MSP local mode)
                final String userName = services.getAuthenticator().getUserName();
                String absoluteFileName = services.getFileManager().createDefaultMSPFileName(userName);

                ((MSPConfig) connector2.getRawConfig()).setOutputAbsoluteFilePath(absoluteFileName);
                ((MSPConfig) connector2.getRawConfig()).setInputAbsoluteFilePath(absoluteFileName);
                try {
                    services.getUIConfigStore().saveConfig(userName, syncConfig);
                } catch (StorageException e1) {
                    String message = "There were some troubles saving the config:<BR>" + e.getMessage();
                    logger.error(message, e);
                    navigator.showError(message);
                }

            } catch (BadConfigException e) {
                errorMessage = connector2.decodeException(e);
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

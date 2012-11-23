package com.taskadapter.webui.export;


import com.taskadapter.config.StorageException;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.FileBasedConnector;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPOutputFileNameNotSetException;
import com.taskadapter.web.MessageDialog;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.Navigator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

import static com.vaadin.terminal.Sizeable.UNITS_PIXELS;

/**
 * Data exporter. Always exports from connector1 to connector2.
 */
public class Exporter {

    private final Logger logger = LoggerFactory.getLogger(Exporter.class);


    private Messages messages;
    private final Services services;
    private final Navigator navigator;
    private final UISyncConfig syncConfig;

    public Exporter(Messages messages, Services services, Navigator navigator, UISyncConfig syncConfig) {
        this.messages = messages;
        this.services = services;
        this.navigator = navigator;
        this.syncConfig = syncConfig;
    }

    public void export() {
        String errorMessage = null;
        boolean valid = true;

        UIConnectorConfig connector1 = syncConfig.getConnector1();
        try {
            connector1.validateForLoad();
        } catch (BadConfigException e) {
            errorMessage = connector1.decodeException(e);
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
                final String userName = services.getCurrentUserInfo().getUserName();
                String absoluteFileName = services.getFileManager().createDefaultMSPFileName(userName);

                ((MSPConfig) connector2.getRawConfig()).setOutputAbsoluteFilePath(absoluteFileName);
                ((MSPConfig) connector2.getRawConfig()).setInputAbsoluteFilePath(absoluteFileName);
                try {
                    services.getUIConfigStore().saveConfig(userName, syncConfig);
                } catch (StorageException e1) {
                    String message = messages.format("export.troublesSavingConfig", e1.getMessage());
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
                    messages.get("export.chooseOperation"),
                    messages.format("export.fileAlreadyExists", fileName),
                    Arrays.asList(messages.get("export.update"), messages.get("export.overwrite"), messages.get("button.cancel")),
                    new MessageDialog.Callback() {
                        public void onDialogResult(String answer) {
                            processFileAction(answer);
                        }
                    }
            );
            messageDialog.setWidth(465, UNITS_PIXELS);

            navigator.addWindow(messageDialog);
        } else {
            processFileAction(messages.get("export.create"));
        }
    }

    private void processFileAction(String action) {
        if (action.equals(messages.get("export.update"))) {
            startUpdateFile();
        } else if (action.equals(messages.get("export.overwrite")) || action.equals(messages.get("export.create"))) {
            startRegularExport();
        }
    }
    
    private void startUpdateFile() {
        UpdateFilePage page = new UpdateFilePage(syncConfig);
        page.setNavigator(navigator);
        page.buildInitialPage();
        navigator.show(page);
    }

    private void startRegularExport() {
        ExportPage page = new ExportPage(syncConfig);
        page.setNavigator(navigator);
        page.buildInitialPage();
        navigator.show(page);
    }
}

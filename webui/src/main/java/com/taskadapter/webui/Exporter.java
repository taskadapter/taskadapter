package com.taskadapter.webui;


import com.taskadapter.PluginManager;
import com.taskadapter.config.ConnectorDataHolder;
import com.taskadapter.config.TAFile;
import com.taskadapter.connector.definition.*;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.web.FileManager;
import com.taskadapter.web.MessageDialog;

import java.io.File;
import java.util.Arrays;

public class Exporter {

    private static final String TEXT_UPDATE = "Only update tasks present in the file";
    private static final String OVERWRITE = "Overwrite";
    private static final String CANCEL = "Cancel";

    private Navigator navigator;
    private PluginManager pluginManager;
    private ConnectorDataHolder sourceDataHolder;
    private ConnectorDataHolder destinationDataHolder;
    private TAFile taFile;

    public Exporter(Navigator navigator, PluginManager pluginManager, final ConnectorDataHolder sourceDataHolder, final ConnectorDataHolder destinationDataHolder, TAFile taFile) {
        this.navigator = navigator;
        this.pluginManager = pluginManager;
        this.sourceDataHolder = sourceDataHolder;
        this.destinationDataHolder = destinationDataHolder;
        this.taFile = taFile;
    }

    public void export() {
        String dataHolderLabel = null;
        String errorMessage = null;
        boolean valid = true;

        try {
            sourceDataHolder.getData().validateForLoad();
        } catch (ValidationException e) {
            dataHolderLabel = sourceDataHolder.getData().getLabel();
            errorMessage = e.getMessage();
            valid = false;
        }

        if (valid) {
            try {

                if (destinationDataHolder.getData() instanceof MSPConfig) {
                    // implementation of http://www.hostedredmine.com/issues/68820
                    // quite ugly
                    // TODO check for XSS attack with wrong paths...
                    MSPConfig config = (MSPConfig) destinationDataHolder.getData();
                    if (config.getOutputAbsoluteFilePath().isEmpty()) {
                        String userName = navigator.getServices().getAuthenticator().getUserName();
                        FileManager fm = new FileManager();
                        File f = fm.getUserFolder(userName);
                        String newPath = String.format("%s/%s-%s.xml", f.getAbsolutePath(), sourceDataHolder.getType(), destinationDataHolder.getType());
                        config.setOutputAbsoluteFilePath(newPath);
                        config.setInputAbsoluteFilePath(newPath);
                    }
                } else {
                    destinationDataHolder.getData().validateForSave();
                }

            } catch (ValidationException e) {
                dataHolderLabel = destinationDataHolder.getData().getLabel();
                errorMessage = e.getMessage();
                valid = false;
            }
        }

        if (valid) {
            processBasedOnDestinationConnectorType();
        } else {
            navigator.showConfigureTaskPage(taFile, dataHolderLabel, errorMessage);
        }
    }

    private void processBasedOnDestinationConnectorType() {
        Connector<ConnectorConfig> destinationConnector = getConnector(destinationDataHolder);
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
                    Arrays.asList(TEXT_UPDATE, OVERWRITE, CANCEL),
                    new MessageDialog.Callback() {
                        public void onDialogResult(String answer) {
                            processFileAction(answer);
                        }
                    }
            );
            messageDialog.setWidth("465px");

            navigator.addWindow(messageDialog);
        } else {
            processFileAction(OVERWRITE);
        }
    }

    private void processFileAction(String action) {
        if (action.equals(TEXT_UPDATE)) {
            startUpdateFile();
        } else if (action.equals(OVERWRITE)) {
            startRegularExport();
        } else {
            System.out.println("canceled");
        }
    }

    private void startUpdateFile() {
        UpdateFilePage page = new UpdateFilePage(getConnector(sourceDataHolder), getConnector(destinationDataHolder), taFile);
        navigator.show(page);
    }

    private void startRegularExport() {
        ExportPage page = new ExportPage(getConnector(sourceDataHolder), getConnector(destinationDataHolder), taFile);
        navigator.show(page);
    }

    private Connector getConnector(ConnectorDataHolder connectorDataHolder) {
        final PluginFactory factory = pluginManager.getPluginFactory(connectorDataHolder.getType());
        final ConnectorConfig config = connectorDataHolder.getData();
        return factory.createConnector(config);
    }

}

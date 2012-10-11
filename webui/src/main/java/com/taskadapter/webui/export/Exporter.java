package com.taskadapter.webui.export;


import com.taskadapter.PluginManager;
import com.taskadapter.config.TAFile;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.FileBasedConnector;
import com.taskadapter.connector.definition.MappingSide;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPOutputFileNameNotSetException;
import com.taskadapter.web.MessageDialog;
import com.taskadapter.web.service.Services;
import com.taskadapter.webui.Navigator;

import java.io.File;
import java.util.Arrays;

public class Exporter {

    private static final String UPDATE = "Only update tasks present in the file";
    private static final String OVERWRITE = "Overwrite";
    private static final String CREATE = "Create";
    private static final String CANCEL = "Cancel";

    private final Navigator navigator;
    private final PluginManager pluginManager;
    private final TAFile file;
    private final MappingSide exportDirection;
    private final ExportConfig<?, ?> exportConfig;

    public Exporter(Navigator navigator, PluginManager pluginManager,
                    TAFile taFile, MappingSide exportDirection) {
        this.navigator = navigator;
        this.pluginManager = pluginManager;
        this.file = taFile;
        this.exportDirection = exportDirection;
        this.exportConfig = ExportConfig.createExportOrder(taFile, exportDirection);
    }

    public void export() {
//        String dataHolderLabel = null;
        String errorMessage = null;
        boolean valid = true;

        try {
            exportConfig.getSourceConfig().getData().validateForLoad();
        } catch (ValidationException e) {
//            dataHolderLabel = resolver.getSourceConfig().getLabel();
            errorMessage = e.getMessage();
            valid = false;
        }

        // TODO refactor these if (valid), if (valid) checks
        if (valid) {
            try {
                exportConfig.getTargetConfig().getData().validateForSave();

            } catch (MSPOutputFileNameNotSetException e) {
                // TODO !!! added for Maxim K:
                // catch (MSPOutputFileNameNotSetException e) { ... } - autofixConfigForSave

                // auto generate output file name (for MSP local mode)
                Services services = navigator.getServices();
                String userName = services.getAuthenticator().getUserName();
                String absoluteFileName = services.getFileManager().createDefaultMSPFileName(userName);

                ((MSPConfig) exportConfig.getTargetConfig().getData()).setOutputAbsoluteFilePath(absoluteFileName);
                ((MSPConfig) exportConfig.getTargetConfig().getData()).setInputAbsoluteFilePath(absoluteFileName);
                services.getConfigStorage().saveConfig(userName, file);

            } catch (ValidationException e) {
//                dataHolderLabel = destinationDataHolder.getData().getLabel();
                errorMessage = e.getMessage();
                valid = false;
            }
        }

        if (valid) {
            processBasedOnDestinationConnectorType();
        } else {
            navigator.showConfigureTaskPage(file, errorMessage);
        }
    }

    private void processBasedOnDestinationConnectorType() {
        Connector<ConnectorConfig> destinationConnector = new ConnectorFactory(pluginManager).getConnector(exportConfig.getTargetConfig());
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
        UpdateFilePage page = new UpdateFilePage(pluginManager, file, exportDirection);
        navigator.show(page);
    }

    private void startRegularExport() {
        ExportPage page = new ExportPage(pluginManager, file, exportDirection);
        navigator.show(page);
    }
}

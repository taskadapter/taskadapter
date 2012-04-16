package com.taskadapter.webui;


import com.taskadapter.PluginManager;
import com.taskadapter.config.ConnectorDataHolder;
import com.taskadapter.connector.definition.*;
import com.taskadapter.web.configeditor.EditorUtil;

import java.util.Arrays;

public class Exporter {

    private static final String TEXT_UPDATE = "Only update tasks present in the file";
    private static final String OVERWRITE = "Overwrite";
    private static final String CANCEL = "Cancel";

    private PageManager pageManager;
    private PluginManager pluginManager;
    private ConnectorDataHolder sourceDataHolder;
    private ConnectorDataHolder destinationDataHolder;
    private String taFileName;

    public Exporter(PageManager pageManager, PluginManager pluginManager, final ConnectorDataHolder sourceDataHolder, final ConnectorDataHolder destinationDataHolder, String taFileName) {
        this.pageManager = pageManager;
        this.pluginManager = pluginManager;
        this.sourceDataHolder = sourceDataHolder;
        this.destinationDataHolder = destinationDataHolder;
        this.taFileName = taFileName;
    }

    public void export() {
        try {
            sourceDataHolder.getData().validateForLoad();
            destinationDataHolder.getData().validateForSave();
            processBasedOnDestinationConnectorType();
        } catch (ValidationException e) {
            EditorUtil.showError(pageManager.getMainWindow(), "Failed validation!", e.getMessage());
            pageManager.show(PageManager.CONFIGURE_TASK_PAGE_ID_PREFFIX + taFileName);
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
            MessageDialog messageDialog = new MessageDialog(
                    "Choose operation", "Destination file already exists:\n" + connectorTo.getAbsoluteOutputFileName(),
                    Arrays.asList(TEXT_UPDATE, OVERWRITE, CANCEL),
                    new MessageDialog.Callback() {
                        public void onDialogResult(String answer) {
                            processFileAction(answer);
                        }
                    }
            );
            messageDialog.setWidth("425px");

            pageManager.getMainWindow().addWindow(messageDialog);
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
        UpdateFilePage page = new UpdateFilePage(getConnector(sourceDataHolder), getConnector(destinationDataHolder));
        pageManager.show(page);
    }

    private void startRegularExport() {
        ExportPage page = new ExportPage(getConnector(sourceDataHolder), getConnector(destinationDataHolder));
        pageManager.show(page);
    }


    private Connector getConnector(ConnectorDataHolder connectorDataHolder) {
        final PluginFactory factory = pluginManager.getPluginFactory(connectorDataHolder.getType());
        final ConnectorConfig config = connectorDataHolder.getData();
        return factory.createConnector(config);
    }

}

package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.configeditor.file.FilePanel;
import com.taskadapter.web.configeditor.file.LocalModeFilePanel;
import com.taskadapter.web.configeditor.file.ServerModeFilePanel;
import com.taskadapter.web.service.Services;

/**
 * @author Alexey Skorokhodov
 */
public class MSPEditor extends ConfigEditor {

    public static final String INTERNAL_PANEL_WIDTH = "450px";
    private FilePanel filePanel;
    private MSPInfoPanel infoPanel;

    public MSPEditor(ConnectorConfig config, Services services) {
        super(config, services);
        buildUI();
        addFieldsMappingPanel(MSPDescriptor.instance.getAvailableFieldsProvider());
        setData(config);
        setMSPDataToForm();
    }

    private void buildUI() {
        createFilePanel();
        createInfoReadOnlyPanel();
    }

    private void createInfoReadOnlyPanel() {
        infoPanel = new MSPInfoPanel();
        infoPanel.setWidth(INTERNAL_PANEL_WIDTH);
        addComponent(infoPanel);
    }

    private void createFilePanel() {
        if (isLocalMode()) {
            filePanel = new LocalModeFilePanel();
        } else {
            filePanel = createRemoteModeFilePanel();
        }
        filePanel.setWidth(INTERNAL_PANEL_WIDTH);
        addComponent(filePanel);
    }

    private ServerModeFilePanel createRemoteModeFilePanel() {
        // TODO fix or delete
        return new ServerModeFilePanel(services.getAuthenticator());
        /*new UploadListener() {
            @Override
            public void fileUploaded(String file) {
                inputFileNameField.setValue(new FileManager().getFullFileNameOnServer(file));
            }
        }*/
    }

    private void setMSPDataToForm() {
        infoPanel.setDurationValue(MSXMLFileWriter.FIELD_DURATION_UNDEFINED.toString());
        infoPanel.setWorkValue(MSXMLFileWriter.FIELD_WORK_UNDEFINED.toString());
        MSPConfig mspConfig = (MSPConfig) config;
        filePanel.refreshConfig(mspConfig);
    }

    @Override
    public ConnectorConfig getPartialConfig() {
        MSPConfig mspConfig = new MSPConfig();
        mspConfig.setInputFileName(filePanel.getInputFileName());
        mspConfig.setOutputFileName(filePanel.getOutputFileName());
        return mspConfig;
    }

    private boolean isLocalMode() {
        return services.getSettingsManager().isLocal();
    }
}

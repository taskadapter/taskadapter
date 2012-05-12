package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.configeditor.DefaultPanel;
import com.taskadapter.web.configeditor.file.FilePanel;
import com.taskadapter.web.configeditor.file.LocalModeFilePanel;
import com.taskadapter.web.configeditor.file.ServerModeFilePanel;
import com.taskadapter.web.configeditor.file.ServerModelFilePanelPresenter;
import com.taskadapter.web.service.Services;

/**
 * @author Alexey Skorokhodov
 */
public class MSPEditor extends ConfigEditor {

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
        infoPanel.setWidth(DefaultPanel.WIDE_PANEL_WIDTH);
        addComponent(infoPanel);
    }

    private void createFilePanel() {
        if (isLocalMode()) {
            filePanel = new LocalModeFilePanel();
        } else {
            filePanel = createRemoteModeFilePanel();
        }
        filePanel.setWidth(DefaultPanel.WIDE_PANEL_WIDTH);
        addComponent(filePanel);
    }

    private ServerModeFilePanel createRemoteModeFilePanel() {
        ServerModelFilePanelPresenter presenter =
                new ServerModelFilePanelPresenter(services.getAuthenticator().getUserName());
        return new ServerModeFilePanel(presenter);
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

package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.configeditor.FieldsMappingPanel;
import com.taskadapter.web.configeditor.TwoColumnsConfigEditor;
import com.taskadapter.web.configeditor.file.FilePanel;
import com.taskadapter.web.configeditor.file.LocalModeFilePanel;
import com.taskadapter.web.configeditor.file.ServerModeFilePanel;
import com.taskadapter.web.configeditor.file.ServerModelFilePanelPresenter;
import com.taskadapter.web.service.Services;

/**
 * @author Alexey Skorokhodov
 */
public class MSPEditor extends TwoColumnsConfigEditor {

    private FilePanel filePanel;
    private MSPInfoPanel infoPanel;

    public MSPEditor(ConnectorConfig config, Services services) {
        super(config, services);
        buildUI();
        setData(config);
        setMSPDataToForm();
    }

    private void buildUI() {
        // left
        addToLeftColumn(createFilePanel());
        addToLeftColumn(createEmptyLabel("24px"));
        addToLeftColumn(createInfoReadOnlyPanel());

        // right
        addToRightColumn(new FieldsMappingPanel(MSPDescriptor.instance.getAvailableFields(), config.getFieldMappings()));
    }

    private MSPInfoPanel createInfoReadOnlyPanel() {
        infoPanel = new MSPInfoPanel();
        infoPanel.setHeight("152px");
        return infoPanel;
    }

    private FilePanel createFilePanel() {
        if (isLocalMode()) {
            filePanel = new LocalModeFilePanel();
        } else {
            filePanel = createRemoteModeFilePanel();
        }
        return filePanel;
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
        mspConfig.setInputAbsoluteFilePath(filePanel.getInputFileName());
        mspConfig.setOutputAbsoluteFilePath(filePanel.getOutputFileName());
        return mspConfig;
    }

    private boolean isLocalMode() {
        return services.getSettingsManager().isTAWorkingOnLocalMachine();
    }
}

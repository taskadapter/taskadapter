package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.configeditor.file.FilePanel;
import com.taskadapter.web.configeditor.file.LocalModeFilePanel;
import com.taskadapter.web.configeditor.file.ServerModeFilePanel;
import com.taskadapter.web.service.Services;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;

/**
 * @author Alexey Skorokhodov
 */
public class MSPEditor extends ConfigEditor {

    private TextField durationText;
    private TextField workText;
    private FilePanel filePanel;

    public MSPEditor(ConnectorConfig config, Services services) {
        super(config, services);
        buildUI();
        addFieldsMappingPanel(MSPDescriptor.instance.getAvailableFieldsProvider());
        setData(config);
        setMSPDataToForm();
    }

    private void buildUI() {
        GridLayout internalStuffGroup = new GridLayout();
        internalStuffGroup.setCaption("MSP Text Fields to use for some internal stuff");
        internalStuffGroup.addStyleName("bordered-panel");
        internalStuffGroup.setMargin(true);
        internalStuffGroup.setSpacing(true);

        durationText = new TextField("Store 'Duration undefined' flag as:");
        durationText.addStyleName("msp-editor-textfield");
        durationText.setEnabled(false);
        internalStuffGroup.addComponent(durationText);

        workText = new TextField("Store 'Work undefined' flag as:");
        workText.addStyleName("msp-editor-textfield");
        workText.setEnabled(false);
        internalStuffGroup.addComponent(workText);

        addComponent(internalStuffGroup);
        createFilePanel();
    }

    private void createFilePanel() {
        if (isLocalMode()) {
            filePanel = new LocalModeFilePanel();
        } else {
            filePanel = createRemoteModeFilePanel();
        }
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
        durationText.setValue(MSXMLFileWriter.FIELD_DURATION_UNDEFINED.toString());
        workText.setValue(MSXMLFileWriter.FIELD_WORK_UNDEFINED.toString());
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

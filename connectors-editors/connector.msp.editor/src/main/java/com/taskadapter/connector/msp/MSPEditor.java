package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.web.FileManager;
import com.taskadapter.web.LocalRemoteModeListener;
import com.taskadapter.web.LocalRemoteOptionsPanel;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class MSPEditor extends ConfigEditor {

    private static final String XML_SUFFIX_LOWERCASE = ".xml";

    private static final String TOOLTIP_FILE_NAME = "Microsoft Project XML file name (full or relative path)";

    private static final String LABEL_FILE_NAME = "Input File name:";

//    private static final String MAIN_GROUP_LABEL = "Configure MSProject settings";
//    private static final String INTERNAL_GROUP_LABEL = "MSP Text Fields to use for some internal stuff";

    private TextField fileNameField;
    private TextField durationText;
    private TextField workText;
    private SettingsManager settingsManager;
    private static final String TEXT_WIDTH = "500px";

    public MSPEditor(ConnectorConfig config, SettingsManager settingsManager) {
        super(config);
        this.settingsManager = settingsManager;
        buildUI();
        addFieldsMappingPanel(MSPDescriptor.instance.getAvailableFieldsProvider(), config.getFieldsMapping());
        setDataToForm();
    }

    private void buildUI() {
        VerticalLayout internalStuffGroup = new VerticalLayout();
        internalStuffGroup.setCaption("MSP Text Fields to use for some internal stuff");

        this.durationText = new TextField("Store 'Duration undefined' flag as:");
        internalStuffGroup.addComponent(durationText);

        durationText.setEnabled(false);
        this.workText = new TextField("Store 'Work undefined' flag as:");
        workText.setEnabled(false);

        internalStuffGroup.addComponent(workText);

        addComponent(internalStuffGroup);

        final VerticalLayout filePanel = new VerticalLayout();

        settingsManager.setLocalRemoteListener(new LocalRemoteModeListener() {
            @Override
            public void modeChanged(boolean isLocal) {
                filePanel.removeAllComponents();
                if (isLocal) {
                    filePanel.addComponent(createLocalModeFilePanel());
                } else {
                    filePanel.addComponent(createRemoteModeFilePanel());
                }
            }
        });

        LocalRemoteOptionsPanel localRemoteOptionsPanel = new LocalRemoteOptionsPanel(settingsManager);

        addComponent(localRemoteOptionsPanel);
        addComponent(filePanel);

        fileNameField = createLabeledText(this, LABEL_FILE_NAME, TOOLTIP_FILE_NAME);
        fileNameField.setWidth(TEXT_WIDTH);
        fileNameField.addListener(new FieldEvents.BlurListener() {
            public void blur(FieldEvents.BlurEvent event) {
                addXMLExtensionIfNeeded();
            }
        });
    }

    /**
   	 * Add ".xml" extension to the MSP file name if it's not there yet.
   	 */
    private void addXMLExtensionIfNeeded() {
        String value = (String) fileNameField.getValue();
        if (!value.toLowerCase().endsWith(XML_SUFFIX_LOWERCASE)) {
            fileNameField.setValue(value + XML_SUFFIX_LOWERCASE);
        }
    }

//    private void browseForMSPFile() {
//        FilDialog dialog = new FieDialog(shell, SWT.NULL);
//        dialog.setText("Select a Microsoft Project XML file");
//        String[] filterExt = {"*.xml", "*.*"};
//        dialog.setFilterExtensions(filterExt);
//
//        String oldFileName = fileNameField.getText();
//        if (!oldFileName.trim().isEmpty()) {
//            File oldFile = new File(fileNameField.getText());
//            dialog.setFilterPath(oldFile.getParent());
//            dialog.setFileName(oldFile.getName());
//        }
//
//        String path = dialog.open();
//        if (path != null) {
//            fileNameField.setText(path);
//        }
//    }

    private AbstractComponent createLocalModeFilePanel() {
        return new Label("Some LOCAL text here");
    }

    private AbstractComponent createRemoteModeFilePanel() {
        RemoteModePanel remoteModePanel = new RemoteModePanel(new UploadListener() {
            @Override
            public void fileUploaded(String file) {
                fileNameField.setValue(new FileManager().getFullFileNameOnServer(file));
            }
        });

        return remoteModePanel;
    }

    private void setDataToForm() {
        durationText.setValue(MSXMLFileWriter.FIELD_DURATION_UNDEFINED.toString());
        workText.setValue(MSXMLFileWriter.FIELD_WORK_UNDEFINED.toString());
//		saveRemoteId.setSelection(mspConfig.isSaveRemoteId());
        MSPConfig mspConfig = (MSPConfig) config;
        if (mspConfig.getInputFileName() != null) {
            fileNameField.setValue(mspConfig.getInputFileName());
        }
    }


    @Override
    public ConnectorConfig getPartialConfig() {
        String fileNameToSave = getFileName();
        MSPConfig c = new MSPConfig(fileNameToSave);
//		c.setSaveRemoteId(saveRemoteId.getSelection());
//        saveCommonFields(c);
        return c;
    }

    private String getFileName() {
        String enteredFileName = (String) fileNameField.getValue();
        if (settingsManager.isLocal()) {
            return enteredFileName;
        } else {
            return new FileManager().getFullFileNameOnServer(enteredFileName);
        }
    }

    public void validate() throws ValidationException {
        String fileNameString = (String) fileNameField.getValue();
        if (fileNameString.isEmpty()) {
            throw new ValidationException("File name is required");
        }
    }
}

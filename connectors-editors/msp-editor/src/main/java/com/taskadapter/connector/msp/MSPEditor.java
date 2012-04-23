package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.FileManager;
import com.taskadapter.web.LocalRemoteModeListener;
import com.taskadapter.web.LocalRemoteOptionsPanel;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.configeditor.EditorUtil;
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
    private static final String MPP_SUFFIX_LOWERCASE = ".mpp";

    private static final String LABEL_FILE_NAME = "Input File name:";
    private static final String TOOLTIP_FILE_NAME = "Microsoft Project file name to load the data from (MPP or XML file)";

    private static final String LABEL_OUTPUT_FILE_NAME = "Output file name:";
    private static final String TOOLTIP_OUTPUT_FILE_NAME = "Microsoft Project file name to save the data to (only XML format is supported)";

//    private static final String MAIN_GROUP_LABEL = "Configure MSProject settings";
//    private static final String INTERNAL_GROUP_LABEL = "MSP Text Fields to use for some internal stuff";

    private TextField inputFileNameField;
    private TextField outputFileNameField;
    private TextField durationText;
    private TextField workText;
    private SettingsManager settingsManager;

    public MSPEditor(ConnectorConfig config, SettingsManager settingsManager) {
        super(config);
        this.settingsManager = settingsManager;
        buildUI();
        addFieldsMappingPanel(MSPDescriptor.instance.getAvailableFieldsProvider(), config.getFieldsMapping());
        setData(config);
        setMSPDataToForm();
    }

    private void buildUI() {
        VerticalLayout internalStuffGroup = new VerticalLayout();
        internalStuffGroup.setCaption("MSP Text Fields to use for some internal stuff");

        durationText = new TextField("Store 'Duration undefined' flag as:");
        durationText.addStyleName("msp-editor-textfield");
        durationText.setEnabled(false);
        internalStuffGroup.addComponent(durationText);

        workText = new TextField("Store 'Work undefined' flag as:");
        workText.addStyleName("msp-editor-textfield");
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

        inputFileNameField = createFileName(LABEL_FILE_NAME, TOOLTIP_FILE_NAME);
        inputFileNameField.addListener(new FieldEvents.BlurListener() {
            public void blur(FieldEvents.BlurEvent event) {
                showXMLFieldIfNeeded();
            }
        });

        outputFileNameField = createFileName(LABEL_OUTPUT_FILE_NAME, TOOLTIP_OUTPUT_FILE_NAME);
    }

    private TextField createFileName(String label, String tooltip) {
        final TextField field = EditorUtil.addLabeledText(this, label, tooltip);
        field.addStyleName("msp-file-name-textfield");
        field.addListener(new FieldEvents.BlurListener() {
            public void blur(FieldEvents.BlurEvent event) {
                addXMLExtensionIfNeeded(field);
            }
        });
        return field;
    }

    /**
     * Add ".xml" extension to the MSP file name if it's not there yet.
     */
    private void addXMLExtensionIfNeeded(TextField field) {
        String value = (String) field.getValue();
        if (!value.toLowerCase().endsWith(MSPFileReader.XML_SUFFIX_LOWERCASE)
                && (!value.toLowerCase().endsWith(MSPFileReader.MPP_SUFFIX_LOWERCASE))
                && (!value.isEmpty())) {
            field.setValue(value + XML_SUFFIX_LOWERCASE);
        }
    }

    private void showXMLFieldIfNeeded() {
        String outputFileNameString = (String) inputFileNameField.getValue();
        String fileNameLowercase = ((String)inputFileNameField.getValue()).toLowerCase();
        if (fileNameLowercase.endsWith(MSPFileReader.MPP_SUFFIX_LOWERCASE)) {
            outputFileNameString = (String) createXMLFileNameForMPP((String) inputFileNameField.getValue());
        }
        outputFileNameField.setValue(outputFileNameString);
    }

    private String createXMLFileNameForMPP(String text) {
        String fileNameWithoutMPPExtension = text.substring(0, text.length() - MSPFileReader.MPP_SUFFIX_LOWERCASE.length());
        return fileNameWithoutMPPExtension + MSPFileReader.XML_SUFFIX_LOWERCASE;
    }

//    private void browseForMSPFile() {
//        FilDialog dialog = new FieDialog(shell, SWT.NULL);
//        dialog.setText("Select a Microsoft Project file (MPP or XML)");
//        String[] filterExt = { "*.xml;*.mpp", "*.*" };
//        dialog.setFilterExtensions(filterExt);
//
//        String oldFileName = inputFileNameField.getText();
//        if (!oldFileName.trim().isEmpty()) {
//            File oldFile = new File(inputFileNameField.getText());
//            dialog.setFilterPath(oldFile.getParent());
//            dialog.setFileName(oldFile.getName());
//        }
//
//        String path = dialog.open();
//        if (path != null) {
//            inputFileNameField.setText(path);
//        }
//    }

    private AbstractComponent createLocalModeFilePanel() {
        return new Label("Some LOCAL text here");
    }

    private AbstractComponent createRemoteModeFilePanel() {
        RemoteModePanel remoteModePanel = new RemoteModePanel(new UploadListener() {
            @Override
            public void fileUploaded(String file) {
                inputFileNameField.setValue(new FileManager().getFullFileNameOnServer(file));
            }
        });

        return remoteModePanel;
    }

    private void setMSPDataToForm() {
        durationText.setValue(MSXMLFileWriter.FIELD_DURATION_UNDEFINED.toString());
        workText.setValue(MSXMLFileWriter.FIELD_WORK_UNDEFINED.toString());
        MSPConfig mspConfig = (MSPConfig) config;
        EditorUtil.setNullSafe(inputFileNameField, mspConfig.getInputFileName());
        EditorUtil.setNullSafe(outputFileNameField, mspConfig.getOutputFileName());
    }


    @Override
    public ConnectorConfig getPartialConfig() {
        MSPConfig mspConfig = new MSPConfig();
        mspConfig.setInputFileName(getFileName(inputFileNameField));
        mspConfig.setOutputFileName(getFileName(outputFileNameField));
        return mspConfig;
    }

    private String getFileName(TextField fileNameField) {
        String enteredFileName = (String) fileNameField.getValue();
        if (settingsManager.isLocal()) {
            return enteredFileName;
        } else {
            return new FileManager().getFullFileNameOnServer(enteredFileName);
        }
    }
}

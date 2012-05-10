package com.taskadapter.web.configeditor.file;

import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.web.configeditor.EditorUtil;
import com.vaadin.ui.TextField;

public class LocalModeFilePanel extends FilePanel {
    private static final String LABEL_FILE_NAME = "Input File name:";
    private static final String TOOLTIP_FILE_NAME = "Microsoft Project file name to load the data from (MPP or XML file)";

    private static final String LABEL_OUTPUT_FILE_NAME = "Output file name:";
    private static final String TOOLTIP_OUTPUT_FILE_NAME = "Microsoft Project file name to save the data to (only XML format is supported)";

    public LocalModeFilePanel() {
        buildUI();
    }

    private TextField inputFileNameField;
    private TextField outputFileNameField;

    private void buildUI() {
        addStyleName("bordered-panel");
        setMargin(true);
        setSpacing(true);

        inputFileNameField = createFileName(LABEL_FILE_NAME, TOOLTIP_FILE_NAME);
        outputFileNameField = createFileName(LABEL_OUTPUT_FILE_NAME, TOOLTIP_OUTPUT_FILE_NAME);
        addComponent(inputFileNameField);
        addComponent(outputFileNameField);
    }


    private TextField createFileName(String label, String tooltip) {
        final TextField field = new TextField(label);
        field.setDescription(tooltip);
        field.addStyleName("msp-file-name-textfield");
        return field;
    }

    @Override
    public void refreshConfig(MSPConfig config) {
        EditorUtil.setNullSafe(inputFileNameField, config.getInputFileName());
        EditorUtil.setNullSafe(outputFileNameField, config.getOutputFileName());
    }

    @Override
    public String getInputFileName() {
        return (String) inputFileNameField.getValue();
    }

    @Override
    public String getOutputFileName() {
        return (String) outputFileNameField.getValue();
    }
}

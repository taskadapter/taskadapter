package com.taskadapter.web.configeditor.file;

import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.web.configeditor.EditorUtil;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class LocalModeFilePanel extends FilePanel {
    private static final String LABEL_FILE_NAME = "Input file:";
    private static final String TOOLTIP_FILE_NAME = "Microsoft Project file name to load the data from (MPP or XML file)";

    private static final String LABEL_OUTPUT_FILE_NAME = "Output file:";
    private static final String TOOLTIP_OUTPUT_FILE_NAME = "Microsoft Project file name to save the data to (only XML format is supported)";

    public LocalModeFilePanel() {
        super("Microsoft project files");
        buildUI();
    }

    private TextField inputFileNameField;
    private TextField outputFileNameField;

    private void buildUI() {
        GridLayout layout = new GridLayout();
        addComponent(layout);
        layout.setColumns(2);
        layout.setSpacing(true);

        layout.addComponent(new Label(LABEL_FILE_NAME));
        inputFileNameField = createFileName(TOOLTIP_FILE_NAME);
        layout.addComponent(inputFileNameField);

        layout.addComponent(new Label(LABEL_OUTPUT_FILE_NAME));
        outputFileNameField = createFileName(TOOLTIP_OUTPUT_FILE_NAME);
        layout.addComponent(outputFileNameField);
    }


    private TextField createFileName(String tooltip) {
        final TextField field = new TextField();
        field.setDescription(tooltip);
        field.addStyleName("msp-file-name-textfield");
        return field;
    }

    @Override
    public void refreshConfig(MSPConfig config) {
        EditorUtil.setNullSafe(inputFileNameField, config.getInputAbsoluteFilePath());
        EditorUtil.setNullSafe(outputFileNameField, config.getOutputAbsoluteFilePath());
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

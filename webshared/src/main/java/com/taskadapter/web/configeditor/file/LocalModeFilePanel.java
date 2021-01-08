package com.taskadapter.web.configeditor.file;

import com.taskadapter.connector.definition.FileSetup;
import com.taskadapter.vaadin14shim.Binder;
import com.taskadapter.vaadin14shim.TextField;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class LocalModeFilePanel extends VerticalLayout {
    private static final String LABEL_FILE_NAME = "Input file:";
    private static final String TOOLTIP_FILE_NAME = "Microsoft Project file name to load the data from (MPP or XML file)";

    private static final String LABEL_OUTPUT_FILE_NAME = "Output file:";
    private static final String TOOLTIP_OUTPUT_FILE_NAME = "Microsoft Project file name to save the data to (only XML format is supported)";

    private final FileSetup fileSetup;

    public LocalModeFilePanel(FileSetup fileSetup) {
//    	super("Microsoft project files");
        this.fileSetup = fileSetup;
//        buildUI();
    }

    private TextField outputFileNameField;

   /* private void buildUI() {
        GridLayout layout = new GridLayout();
        setContent(layout);
        layout.setColumns(2);
        layout.setSpacing(true);

        layout.add(new Label(LABEL_FILE_NAME));
        TextField inputFileNameField = createFileName(TOOLTIP_FILE_NAME);
        Binder.bindField(inputFileNameField, fileSetup, "sourceFile");
        layout.add(inputFileNameField);

        layout.add(new Label(LABEL_OUTPUT_FILE_NAME));
        outputFileNameField = createFileName(TOOLTIP_OUTPUT_FILE_NAME);
        Binder.bindField(outputFileNameField, fileSetup, "targetFile");
        outputFileNameField.addBlurListener((FieldEvents.BlurListener) event -> {
            String val = outputFileNameField.getValue();
            if(!(val.endsWith(".xml") || val.endsWith(".XML"))) {
                outputFileNameField.setValue(val + ".xml");
            }
        });
        layout.add(outputFileNameField);
    }

*/
    private TextField createFileName(String tooltip) {
        final TextField field = new TextField();
//        field.setDescription(tooltip);
        field.addClassName("msp-file-name-textfield");
        return field;
    }
}

package com.taskadapter.web.configeditor.file;

import com.taskadapter.connector.definition.FileSetup;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.uiapi.SavableComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalModeFilePanel extends VerticalLayout implements SavableComponent {
    private static final String LABEL_FILE_NAME = "Input file:";
    private static final String TOOLTIP_FILE_NAME = "Microsoft Project file name to load the data from (MPP or XML file)";

    private static final String LABEL_OUTPUT_FILE_NAME = "Output file:";
    private static final String TOOLTIP_OUTPUT_FILE_NAME = "Microsoft Project file name to save the data to (only XML format is supported)";

    private static final Logger logger = LoggerFactory.getLogger(LocalModeFilePanel.class);

    private final FileSetup fileSetup;
    private final Binder<FileSetup> binder = new Binder<>(FileSetup.class);

    public LocalModeFilePanel(FileSetup fileSetup) {
        this.fileSetup = fileSetup;
        buildUI();
    }

    private void buildUI() {
        var caption = EditorUtil.createCaption("Microsoft project files");

        var inputFileNameField = EditorUtil.textInput(binder, "sourceFile");
        EditorUtil.setTooltip(inputFileNameField, TOOLTIP_FILE_NAME);

        var outputFileNameField = EditorUtil.textInput(binder, "targetFile");
        EditorUtil.setTooltip(outputFileNameField, TOOLTIP_OUTPUT_FILE_NAME);

        outputFileNameField.addBlurListener(event -> {
            String val = outputFileNameField.getValue();
            if (!(val.endsWith(".xml") || val.endsWith(".XML"))) {
                outputFileNameField.setValue(val + ".xml");
            }
        });
        var layout = new FormLayout();
        layout.add(new Label(LABEL_FILE_NAME),
                inputFileNameField);
        layout.add(new Label(LABEL_OUTPUT_FILE_NAME),
                outputFileNameField);

        add(caption, layout);
        binder.readBean(fileSetup);
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public boolean save() {
        try {
            binder.writeBean(fileSetup);
        } catch (ValidationException e) {
            logger.error("validation error while trying to save file" + e.toString());
            return false;
        }
        return true;
    }
}

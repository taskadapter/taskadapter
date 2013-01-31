package com.taskadapter.connector.msp.editor;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPFileReader;
import com.taskadapter.connector.msp.MSPOutputFileNameNotSetException;
import com.taskadapter.connector.msp.MSPUtils;
import com.taskadapter.connector.msp.UnsupportedRelationType;
import com.taskadapter.connector.msp.editor.error.InputFileNameNotSetException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.configeditor.file.FileProcessingResult;
import com.taskadapter.web.configeditor.file.LocalModeFilePanel;
import com.taskadapter.web.configeditor.file.ServerModeFilePanel;
import com.taskadapter.web.configeditor.file.UploadProcessor;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Sandbox;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import java.io.File;

import static com.taskadapter.web.configeditor.EditorUtil.propertyInput;

public class MSPEditorFactory implements PluginEditorFactory<MSPConfig> {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.msp.messages";
    private static final String LABEL_DESCRIPTION_TEXT = "Description:";
    private static final String LABEL_TOOLTIP = "Text to show for this connector on 'Export' button. Enter any text.";
    private static final String UPLOAD_SUCCESS = "Upload success";
    private static final String UPLOAD_MPP_SUCCESS = "File uploaded and successfully converted to XML";
    private static final String SAVE_FILE_FAILED = "Save file error"; // error of saving after upload

    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public String formatError(Throwable e) {
        if (e instanceof UnsupportedRelationType) {
            return MESSAGES.format(
                    "errors.unsupportedRelation",
                    MESSAGES.get("relations."
                            + ((UnsupportedRelationType) e).getRelationType()));
        } else if (e instanceof InputFileNameNotSetException) {
            return MESSAGES.get("error.inputFileNameNotSet");
        }
        return null;
    }

    @Override
    public ComponentContainer getMiniPanelContents(WindowProvider windowProvider, Sandbox sandbox, MSPConfig config) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth(450, Sizeable.UNITS_PIXELS);
        layout.addComponent(createDescriptionElement(config));
        layout.addComponent(createFilePanel(sandbox, config));
        layout.addComponent(createInfoReadOnlyPanel());
        return layout;
    }

    private HorizontalLayout createDescriptionElement(ConnectorConfig config) {
        HorizontalLayout descriptionLayout = new HorizontalLayout();
        descriptionLayout.setSpacing(true);
        descriptionLayout.addComponent(new Label(LABEL_DESCRIPTION_TEXT));
        TextField labelText = propertyInput(config, "label");
        labelText.setDescription(LABEL_TOOLTIP);
        labelText.addStyleName("label-textfield");
        descriptionLayout.addComponent(labelText);
        return descriptionLayout;
    }

    private MSPInfoPanel createInfoReadOnlyPanel() {
        MSPInfoPanel infoPanel = new MSPInfoPanel();
        infoPanel.setHeight("152px");
        return infoPanel;
    }

    private Panel createFilePanel(Sandbox sandbox, MSPConfig config) {
        final Property inputFilePath = new MethodProperty<String>(
                config, "inputAbsoluteFilePath");
        final Property outputFilePath = new MethodProperty<String>(
                config, "outputAbsoluteFilePath");
        if (sandbox.allowLocalFSAccess()) {
            return new LocalModeFilePanel(inputFilePath, outputFilePath);
        } else {                    
            return createServerModePanel(sandbox, inputFilePath, outputFilePath);
        }
    }

    private ServerModeFilePanel createServerModePanel(final Sandbox sandbox,
            final Property inputFilePath, final Property outputFilePath) {
        final UploadProcessor proc = new UploadProcessor() {
            @Override
            public FileProcessingResult processFile(File uploadedFile) {
                return MSPEditorFactory.this.processFile(sandbox, uploadedFile);
            }
        };
        return new ServerModeFilePanel(sandbox.getUserContentDirectory(),
                inputFilePath, outputFilePath, proc);
    }

    @Override
    public void validateForSave(MSPConfig config) throws BadConfigException {
        if (config.getOutputAbsoluteFilePath().isEmpty()) {
            throw new MSPOutputFileNameNotSetException();
        }
    }

    @Override
    public void validateForLoad(MSPConfig config) throws BadConfigException {
        if (config.getInputAbsoluteFilePath().isEmpty()) {
            throw new InputFileNameNotSetException();
        }
    }

    @Override
    public String describeSourceLocation(MSPConfig config) {
        return new File(config.getInputAbsoluteFilePath()).getName();
    }

    @Override
    public String describeDestinationLocation(MSPConfig config) {
        return new File(config.getOutputAbsoluteFilePath()).getName();
    }

    FileProcessingResult processFile(Sandbox sandbox, File uploadedFile) {
        String fileName = uploadedFile.getName();

        // check if MPP file
        boolean isMpp = fileName.toLowerCase().endsWith(MSPFileReader.MPP_SUFFIX_LOWERCASE);
        if (!isMpp) {
            return new FileProcessingResult(uploadedFile, UPLOAD_SUCCESS);
        }
        
        File f = new File(sandbox.getUserContentDirectory(), fileName);
        String newFilePath = MSPUtils.convertMppProjectFileToXml(f
                .getAbsolutePath());
        if (newFilePath == null) {
            return new FileProcessingResult(null, SAVE_FILE_FAILED);
        }
        
        return new FileProcessingResult(new File(newFilePath), UPLOAD_MPP_SUCCESS);
    }

}

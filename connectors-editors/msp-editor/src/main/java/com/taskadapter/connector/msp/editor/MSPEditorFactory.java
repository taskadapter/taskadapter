package com.taskadapter.connector.msp.editor;

import com.taskadapter.common.ui.FieldMapping;
import com.taskadapter.connector.common.FileNameGenerator;
import com.taskadapter.connector.definition.ConnectorSetup;
import com.taskadapter.connector.definition.FileSetup;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPConnector;
import com.taskadapter.connector.msp.MSPFileReader;
import com.taskadapter.connector.msp.MSPUtils;
import com.taskadapter.connector.msp.UnsupportedRelationType;
import com.taskadapter.connector.msp.editor.error.InputFileNameNotSetException;
import com.taskadapter.web.ConnectorSetupPanel;
import com.taskadapter.web.DroppingNotSupportedException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.configeditor.file.FileProcessingResult;
import com.taskadapter.web.configeditor.file.LocalModeFilePanel;
import com.taskadapter.web.configeditor.file.ServerModeFilePanel;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.web.uiapi.DefaultSavableComponent;
import com.taskadapter.web.uiapi.SavableComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MSPEditorFactory implements PluginEditorFactory<MSPConfig, FileSetup> {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.msp.messages";
    private static final String LABEL_DESCRIPTION_TEXT = "Description:";
    private static final String LABEL_TOOLTIP = "Text to show for this connector on 'Export' button. Enter any text.";
    private static final String UPLOAD_SUCCESS = "Upload success";
    private static final String UPLOAD_MPP_SUCCESS = "File uploaded and successfully converted to XML";
    private static final String SAVE_FILE_FAILED = "Save file error"; // error of saving after upload

    private static final Messages messages = new Messages(BUNDLE_NAME);

    @Override
    public String formatError(Throwable e) {
        if (e instanceof UnsupportedRelationType) {
            return messages.format("errors.unsupportedRelation",
                    messages.get("relations." + ((UnsupportedRelationType) e).getRelationType()));
        }
        if (e instanceof InputFileNameNotSetException) {
            return messages.get("error.inputFileNameNotSet");
        }
        return e.getMessage();
    }

    @Override
    public SavableComponent getMiniPanelContents(Sandbox sandbox, MSPConfig config, FileSetup setup) {
        var layout = new VerticalLayout();
        layout.setMargin(true);

        var infoReadOnlyPanel = new MSPInfoPanel();
        infoReadOnlyPanel.setHeight("152px");

        layout.add(infoReadOnlyPanel);
        // TODO 14 update the save function
        return new DefaultSavableComponent(layout, () -> {
            return true;
        });
    }

    @Override
    public boolean isWebConnector() {
        return false;
    }

    @Override
    public ConnectorSetupPanel getEditSetupPanel(Sandbox sandbox, FileSetup setup) {
        SavableComponent savableComponent = sandbox.allowLocalFSAccess() ?
                new LocalModeFilePanel(setup) : createServerModePanel(sandbox, setup);

        return new ConnectorSetupPanel() {
            @Override
            public Component getComponent() {
                return savableComponent.getComponent();
            }

            @Override
            public Optional<String> validate() {
                return Optional.empty();
            }

            @Override
            public void showError(String string) {

            }

            @Override
            public ConnectorSetup getResult() {
                savableComponent.save();
                return setup;
            }
        };
    }

    @Override
    public FileSetup createDefaultSetup(Sandbox sandbox) {
        var newPath = FileNameGenerator.findSafeAvailableFileName(sandbox.getUserContentDirectory(), "MSP_%d.xml").getAbsolutePath();
        var label = getShortLabel(newPath);
        return FileSetup.apply(MSPConnector.ID, label, newPath, newPath);
    }

    @Override
    public List<BadConfigException> validateForSave(MSPConfig config, FileSetup setup, List<FieldMapping<?>> fieldMappings) {
        // empty target file name is valid because it will be generated in [[updateForSave]]
        // right before the export
        return Collections.emptyList();
    }

    @Override
    public List<BadConfigException> validateForLoad(MSPConfig config, FileSetup setup) {
        var seq = new ArrayList<BadConfigException>();
        if (setup.getSourceFile().isEmpty()) {
            seq.add(new InputFileNameNotSetException());
        }
        return seq;
    }

    @Override
    public void validateForDropInLoad(MSPConfig config) throws BadConfigException, DroppingNotSupportedException {
        // Always valid!
    }

    @Override
    public String describeSourceLocation(MSPConfig config, FileSetup setup) {
        return new File(setup.getSourceFile()).getName();
    }

    @Override
    public String describeDestinationLocation(MSPConfig config, FileSetup setup) {
        return new File(setup.getTargetFile()).getName();
    }

    @Override
    public Messages fieldNames() {
        return messages;
    }

    private static String getShortLabel(String fileName) {
        return Paths.get(fileName).getFileName().toString();
    }

    private SavableComponent createServerModePanel(Sandbox sandbox, FileSetup fileSetup) {
        return new ServerModeFilePanel(sandbox.getUserContentDirectory(), fileSetup,
                uploadedFile -> processFile(sandbox, uploadedFile)
        );
    }


    private FileProcessingResult processFile(Sandbox sandbox, File uploadedFile) {
        var fileName = uploadedFile.getName();
        // check if MPP file
        var isMpp = fileName.toLowerCase().endsWith(MSPFileReader.MPP_SUFFIX_LOWERCASE);
        if (!isMpp) {
            return new FileProcessingResult(uploadedFile, UPLOAD_SUCCESS);
        }
        var f = new File(sandbox.getUserContentDirectory(), fileName);
        var newFilePath = MSPUtils.convertMppProjectFileToXml(f.getAbsolutePath());
        if (newFilePath == null) return new FileProcessingResult(null, SAVE_FILE_FAILED);
        return new FileProcessingResult(new File(newFilePath), UPLOAD_MPP_SUCCESS);
    }
}

package com.taskadapter.connector.msp.editor;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPOutputFileNameNotSetException;
import com.taskadapter.connector.msp.UnsupportedRelationType;
import com.taskadapter.connector.msp.editor.error.InputFileNameNotSetException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.configeditor.file.LocalModeFilePanel;
import com.taskadapter.web.configeditor.file.ServerModeFilePanel;
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
            return new ServerModeFilePanel(sandbox.getUserContentDirectory(), inputFilePath, outputFilePath);
        }
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

}

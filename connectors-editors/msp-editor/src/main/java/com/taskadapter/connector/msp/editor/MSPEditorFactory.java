package com.taskadapter.connector.msp.editor;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.connector.msp.MSPConnector;
import com.taskadapter.connector.msp.UnsupportedRelationType;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.configeditor.file.LocalModeFilePanel;
import com.taskadapter.web.configeditor.file.ServerModeFilePanel;
import com.taskadapter.web.configeditor.file.ServerModelFilePanelPresenter;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.*;

public class MSPEditorFactory implements PluginEditorFactory {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.msp.messages";
    private static final String LABEL_DESCRIPTION_TEXT = "Description:";
    private static final String LABEL_TOOLTIP = "Text to show for this connector on 'Export' button. Enter any text.";

    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public String getId() {
        return MSPConnector.ID;
    }

    @Override
    public String formatError(Throwable e) {
        if (e instanceof UnsupportedRelationType) {
            return MESSAGES.format(
                    "errors.unsupportedRelation",
                    MESSAGES.get("relations."
                            + ((UnsupportedRelationType) e).getRelationType()));
        }
        return null;
    }

    @Override
    public AvailableFields getAvailableFields() {
        return MSPSupportedFields.SUPPORTED_FIELDS;
    }

    @Override
    public ComponentContainer getMiniPanelContents(WindowProvider windowProvider, Services services, ConnectorConfig config) {
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(createDescriptionElement(config));
        layout.addComponent(createInfoReadOnlyPanel());
        layout.addComponent(createFilePanel(services, config));
        return layout;
    }

    private HorizontalLayout createDescriptionElement(ConnectorConfig config) {
        HorizontalLayout descriptionLayout = new HorizontalLayout();
        descriptionLayout.setSpacing(true);
        descriptionLayout.addComponent(new Label(LABEL_DESCRIPTION_TEXT));
        TextField labelText = new TextField();
        labelText.setDescription(LABEL_TOOLTIP);
        labelText.addStyleName("label-textfield");
        labelText.setPropertyDataSource(new MethodProperty<String>(config, "label"));
        descriptionLayout.addComponent(labelText);
        return descriptionLayout;
    }

    private MSPInfoPanel createInfoReadOnlyPanel() {
        MSPInfoPanel infoPanel = new MSPInfoPanel();
        infoPanel.setHeight("152px");
        return infoPanel;
    }

    private Panel createFilePanel(Services services, ConnectorConfig config) {
        if (isLocalMode(services)) {
            return new LocalModeFilePanel((MSPConfig) config);
        } else {
            return createRemoteModeFilePanel(services, config);
        }
    }

    private ServerModeFilePanel createRemoteModeFilePanel(Services services, ConnectorConfig config) {
        ServerModelFilePanelPresenter presenter =
                new ServerModelFilePanelPresenter(services.getFileManager(), services.getAuthenticator().getUserName());
        return new ServerModeFilePanel(presenter, (MSPConfig) config);
    }

    private boolean isLocalMode(Services services) {
        return services.getSettingsManager().isTAWorkingOnLocalMachine();
    }

}
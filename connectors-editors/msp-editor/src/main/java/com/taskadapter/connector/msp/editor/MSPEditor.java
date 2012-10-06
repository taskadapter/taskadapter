package com.taskadapter.connector.msp.editor;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.msp.MSPConfig;
import com.taskadapter.web.configeditor.FieldsMappingPanel;
import com.taskadapter.web.configeditor.TwoColumnsConfigEditor;
import com.taskadapter.web.configeditor.file.LocalModeFilePanel;
import com.taskadapter.web.configeditor.file.ServerModeFilePanel;
import com.taskadapter.web.configeditor.file.ServerModelFilePanelPresenter;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

public class MSPEditor extends TwoColumnsConfigEditor {
    private static final String LABEL_DESCRIPTION_TEXT = "Description:";
    private static final String LABEL_TOOLTIP = "Text to show for this connector on 'Export' button. Enter any text.";

    public MSPEditor(ConnectorConfig config, Services services) {
        super(config, services);
        buildUI();
    }

    private void buildUI() {
        addToLeftColumn(createDescriptionElement());

        // left
        addToLeftColumn(createFilePanel());
        addToLeftColumn(createEmptyLabel("24px"));
        addToLeftColumn(createInfoReadOnlyPanel());
    }

    private HorizontalLayout createDescriptionElement() {
        HorizontalLayout descriptionLayout = new HorizontalLayout();
        descriptionLayout.setSpacing(true);
        descriptionLayout.addComponent(new Label(LABEL_DESCRIPTION_TEXT));
        TextField labelText = new TextField();
        labelText.setDescription(LABEL_TOOLTIP);
        labelText.addStyleName("label-textfield");
        labelText.setPropertyDataSource(new MethodProperty<String>(config,
                "label"));
        descriptionLayout.addComponent(labelText);
        return descriptionLayout;
    }

    private MSPInfoPanel createInfoReadOnlyPanel() {
        MSPInfoPanel infoPanel = new MSPInfoPanel((MSPConfig) config);
        infoPanel.setHeight("152px");
        return infoPanel;
    }

    private Panel createFilePanel() {
        if (isLocalMode()) {
            return new LocalModeFilePanel((MSPConfig) config);
        } else {
            return createRemoteModeFilePanel();
        }
    }

    private ServerModeFilePanel createRemoteModeFilePanel() {
        ServerModelFilePanelPresenter presenter =
                new ServerModelFilePanelPresenter(services.getFileManager(), services.getAuthenticator().getUserName());
        return new ServerModeFilePanel(presenter, (MSPConfig) config);
    }

    private boolean isLocalMode() {
        return services.getSettingsManager().isTAWorkingOnLocalMachine();
    }
}

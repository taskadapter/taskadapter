package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.configeditor.FieldsMappingPanel;
import com.taskadapter.web.configeditor.TwoColumnsConfigEditor;
import com.taskadapter.web.configeditor.file.LocalModeFilePanel;
import com.taskadapter.web.configeditor.file.ServerModeFilePanel;
import com.taskadapter.web.configeditor.file.ServerModelFilePanelPresenter;
import com.taskadapter.web.service.Services;
import com.vaadin.ui.Panel;

/**
 * @author Alexey Skorokhodov
 */
public class MSPEditor extends TwoColumnsConfigEditor {

    private MSPInfoPanel infoPanel;

    public MSPEditor(ConnectorConfig config, Services services) {
        super(config, services);
        buildUI();
    }

    private void buildUI() {
        // left
        addToLeftColumn(createFilePanel());
        addToLeftColumn(createEmptyLabel("24px"));
        addToLeftColumn(createInfoReadOnlyPanel());

        // right
        addToRightColumn(new FieldsMappingPanel(MSPDescriptor.instance.getAvailableFields(), config.getFieldMappings()));
    }

    private MSPInfoPanel createInfoReadOnlyPanel() {
        infoPanel = new MSPInfoPanel((MSPConfig) config);
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
                new ServerModelFilePanelPresenter(services.getAuthenticator().getUserName());
        return new ServerModeFilePanel(presenter, (MSPConfig) config);
    }

    private boolean isLocalMode() {
        return services.getSettingsManager().isTAWorkingOnLocalMachine();
    }
}

package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.configeditor.DefaultPanel;
import com.taskadapter.web.configeditor.FieldsMappingPanel;
import com.taskadapter.web.configeditor.file.FilePanel;
import com.taskadapter.web.configeditor.file.LocalModeFilePanel;
import com.taskadapter.web.configeditor.file.ServerModeFilePanel;
import com.taskadapter.web.configeditor.file.ServerModelFilePanelPresenter;
import com.taskadapter.web.service.Services;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class MSPEditor extends ConfigEditor {

    private FilePanel filePanel;
    private MSPInfoPanel infoPanel;

    public MSPEditor(ConnectorConfig config, Services services) {
        super(config, services);
        buildUI();
        setData(config);
        setMSPDataToForm();
    }

    private void buildUI() {

        /**********/
        // TODO refactor this copy paste from Redmine Editor
        HorizontalLayout root = new HorizontalLayout();
        root.setSpacing(true);

        VerticalLayout leftVerticalLayout = new VerticalLayout();
        leftVerticalLayout.setWidth(DefaultPanel.WIDE_PANEL_WIDTH);
        leftVerticalLayout.setSpacing(true);

        VerticalLayout rightVerticalLayout = new VerticalLayout();
        rightVerticalLayout.setWidth(DefaultPanel.NARROW_PANEL_WIDTH);
        rightVerticalLayout.setSpacing(true);

        root.addComponent(leftVerticalLayout);
        root.addComponent(rightVerticalLayout);
        /**********/

        createFilePanel(leftVerticalLayout);
        leftVerticalLayout.addComponent(new Label("&nbsp", Label.CONTENT_XHTML));
        createInfoReadOnlyPanel(leftVerticalLayout);

        //addFieldsMappingPanel(MSPDescriptor.instance.getAvailableFieldsProvider());

        FieldsMappingPanel fieldsMappingPanel = new FieldsMappingPanel(MSPDescriptor.instance.getAvailableFieldsProvider(), config);
        addPanelToCustomComponent(rightVerticalLayout, fieldsMappingPanel);
        fieldsMappingPanel.setWidth(DefaultPanel.NARROW_PANEL_WIDTH);

        addComponent(root);
    }

    private void createInfoReadOnlyPanel(VerticalLayout verticalLayout) {
        infoPanel = new MSPInfoPanel();
        infoPanel.setWidth(DefaultPanel.WIDE_PANEL_WIDTH);
        infoPanel.setHeight("152px");
        verticalLayout.addComponent(infoPanel);
    }

    private void createFilePanel(VerticalLayout verticalLayout) {
        if (isLocalMode()) {
            filePanel = new LocalModeFilePanel();
        } else {
            filePanel = createRemoteModeFilePanel();
        }
        filePanel.setWidth(DefaultPanel.WIDE_PANEL_WIDTH);
        verticalLayout.addComponent(filePanel);
    }

    private ServerModeFilePanel createRemoteModeFilePanel() {
        ServerModelFilePanelPresenter presenter =
                new ServerModelFilePanelPresenter(services.getAuthenticator().getUserName());
        return new ServerModeFilePanel(presenter);
    }

    private void setMSPDataToForm() {
        infoPanel.setDurationValue(MSXMLFileWriter.FIELD_DURATION_UNDEFINED.toString());
        infoPanel.setWorkValue(MSXMLFileWriter.FIELD_WORK_UNDEFINED.toString());
        MSPConfig mspConfig = (MSPConfig) config;
        filePanel.refreshConfig(mspConfig);
    }

    @Override
    public ConnectorConfig getPartialConfig() {
        MSPConfig mspConfig = new MSPConfig();
        mspConfig.setInputFileName(filePanel.getInputFileName());
        mspConfig.setOutputFileName(filePanel.getOutputFileName());
        return mspConfig;
    }

    private boolean isLocalMode() {
        return services.getSettingsManager().isLocal();
    }
}

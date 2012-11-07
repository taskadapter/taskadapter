package com.taskadapter.webui.config;

import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.MiniPanel;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

public class OnePageEditor extends VerticalLayout implements WindowProvider {

    private Messages messages;
    // TODO for MaximK: why is this deprecated? what's a replacement for it?
    @Deprecated
    private Services services;

    private UISyncConfig config;
    private TaskFieldsMappingFragment taskFieldsMappingFragment;
    private ExportButtonsFragment exportButtonsFragment;

    public OnePageEditor(Messages messages, Services services, UISyncConfig config) {
        this.messages = messages;
        this.services = services;
        this.config = config;
        buildUI();
    }

    private void buildUI() {
        setWidth(760, UNITS_PIXELS);
        setMargin(true);
        addConnectorsPanel();
        addMappingPanel();
    }

    private void addConnectorsPanel() {
        HorizontalLayout layout = new HorizontalLayout();

        addLeft(layout);
        addExportButtonsToCenter(layout);
        addRight(layout);

        addComponent(layout);
    }

    private void addExportButtonsToCenter(HorizontalLayout layout) {
        exportButtonsFragment = new ExportButtonsFragment();
        layout.addComponent(exportButtonsFragment);
        layout.setComponentAlignment(exportButtonsFragment, Alignment.MIDDLE_CENTER);
    }

    private void addLeft(HorizontalLayout layout) {
        MiniPanel miniPanel1 = createMiniPanel(config.getConnector1());
        Button editButton1 = createEditButton(miniPanel1);
        layout.addComponent(editButton1);
        layout.setComponentAlignment(editButton1, Alignment.MIDDLE_LEFT);
        layout.addComponent(miniPanel1);
        layout.setComponentAlignment(miniPanel1, Alignment.MIDDLE_RIGHT);
    }

    private void addRight(HorizontalLayout layout) {
        MiniPanel miniPanel2 = createMiniPanel(config.getConnector2());
        Button editButton2 = createEditButton(miniPanel2);
        layout.addComponent(miniPanel2);
        layout.addComponent(editButton2);
        layout.setComponentAlignment(editButton2, Alignment.MIDDLE_RIGHT);
        layout.setComponentAlignment(miniPanel2, Alignment.MIDDLE_LEFT);
    }

    private void addMappingPanel() {
        taskFieldsMappingFragment = createOnePageMappingPanel();
        addComponent(taskFieldsMappingFragment);
    }

    private MiniPanel createMiniPanel(UIConnectorConfig connectorConfig) {
        MiniPanel miniPanel = new MiniPanel(this, connectorConfig);
        // "services" instance is only used by MSP Editor Factory
        miniPanel.setPanelContents(connectorConfig.createMiniPanel(this, services));
        return miniPanel;
    }

    private Button createEditButton(final MiniPanel miniPanel) {
        Button editButton = EditorUtil.createButton("", "Edit the connector settings",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        miniPanel.showEditConnectorDialog();
                    }
                }
        );
        editButton.setIcon(new ThemeResource("img/edit.png"));
        editButton.setStyleName(Runo.BUTTON_SMALL);
        editButton.addStyleName("editConfigButton");
        return editButton;
    }

    private TaskFieldsMappingFragment createOnePageMappingPanel() {
        return new TaskFieldsMappingFragment(messages, config.getConnector1(), config.getConnector2(), config.getNewMappings());
    }

    public void validate() throws BadConfigException {
        // TODO !!! validate left/right editors too. this was lost during the last refactoring.
        taskFieldsMappingFragment.validate();
    }

    Button getButtonRight() {
        return exportButtonsFragment.getButtonRight();
    }

    Button getButtonLeft() {
        return exportButtonsFragment.getButtonLeft();
    }

}

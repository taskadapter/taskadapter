package com.taskadapter.webui;

import com.taskadapter.connector.definition.MappingSide;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.config.DirectionResolver;
import com.taskadapter.webui.config.ExportButtonsFragment;
import com.taskadapter.webui.export.Exporter;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

/**
 * Buttons panel with left/right arrows.
 */
public class ConfigActionsPanel extends VerticalLayout {
    private Navigator navigator;
    private UISyncConfig syncConfig;
    private HorizontalLayout horizontalLayout;
    private static final String NO_DESCRIPTION_TEXT = "<i>No description</i>"; //&nbsp;
    private final Messages messages;
    private final Services services;
    private ExportButtonsFragment exportButtonsFragment;
    private Button editButton;
    private HorizontalLayout descriptionLayout;

    public ConfigActionsPanel(Messages messages, Services services, Navigator navigator, UISyncConfig uiSyncConfig) {
        this.messages = messages;
        this.services = services;
        this.navigator = navigator;
        this.syncConfig = uiSyncConfig;
        buildUI();
    }

    private void buildUI() {
        addStyleName("configPanelInConfigsList");

        addDescriptionPart();
        addExportPart();
    }

    private void addDescriptionPart() {
        descriptionLayout = new HorizontalLayout();
        descriptionLayout.setWidth(100, UNITS_PERCENTAGE);
        descriptionLayout.addStyleName("configDescriptionPanel");
        addComponent(descriptionLayout);
        addDescription();
        addEditButton();
    }

    private void addDescription() {
        final String labelText = syncConfig.getLabel();
        Label description = new Label(labelText.isEmpty() ? NO_DESCRIPTION_TEXT : labelText, Label.CONTENT_XHTML);
        description.addStyleName("configDescriptionLabel");
        descriptionLayout.addComponent(description);
        descriptionLayout.setComponentAlignment(description, Alignment.MIDDLE_LEFT);
    }

    private void addEditButton() {
        editButton = new Button();
        editButton.setIcon(new ThemeResource("img/edit.png"));
        editButton.setStyleName(Runo.BUTTON_SMALL);
        editButton.addStyleName("editConfigButton");
        editButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                navigator.showConfigureTaskPage(syncConfig);
            }
        });
        descriptionLayout.addComponent(editButton);
        descriptionLayout.setComponentAlignment(editButton, Alignment.MIDDLE_RIGHT);
    }

    private void addExportPart() {
        horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        addComponent(horizontalLayout);

        createBox(syncConfig.getConnector1().getLabel());
        createActionButtons();
        createBox(syncConfig.getConnector2().getLabel());
    }

    private void createActionButtons() {
        exportButtonsFragment = new ExportButtonsFragment();
        addExportButtonListeners();
        horizontalLayout.addComponent(exportButtonsFragment);
    }

    private void addExportButtonListeners() {
        addExportButtonListener(exportButtonsFragment.getButtonLeft(), MappingSide.LEFT);
        addExportButtonListener(exportButtonsFragment.getButtonRight(), MappingSide.RIGHT);
    }

    private void addExportButtonListener(Button button, MappingSide exportDirection) {
        UISyncConfig configForExport = DirectionResolver.getDirectionalConfig(syncConfig, exportDirection);
        final Exporter exporter = new Exporter(messages, services, navigator, configForExport);
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                exporter.export();
            }
        });
    }

    private void createBox(final String label) {
        Label configBoxButton = new Label(label);
        configBoxButton.addStyleName("connectorLabelInBlueBox");
        horizontalLayout.addComponent(configBoxButton);
        horizontalLayout.setComponentAlignment(configBoxButton, Alignment.MIDDLE_CENTER);
    }

    UISyncConfig getConfig() {
        return syncConfig;
    }
}

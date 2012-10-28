package com.taskadapter.webui;

import com.taskadapter.connector.definition.MappingSide;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.export.Exporter;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
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
    private final Services services;

    public ConfigActionsPanel(Services services, Navigator navigator, UISyncConfig uiSyncConfig) {
        this.services = services;
        this.navigator = navigator;
        this.syncConfig = uiSyncConfig;
        buildUI();
    }

    private void buildUI() {
        addDescription();

        horizontalLayout = new HorizontalLayout();
        horizontalLayout.addStyleName("configs-single-panel-inner");
        horizontalLayout.setSpacing(true);
        addComponent(horizontalLayout);

        createBox(syncConfig.getConnector1().getLabel());
        createActionButtons();
        createBox(syncConfig.getConnector2().getLabel());
    }

    private void addDescription() {
        final String labelText = syncConfig.getLabel();
        Label description = new Label(labelText.isEmpty() ? NO_DESCRIPTION_TEXT : labelText, Label.CONTENT_XHTML);
        description.setStyleName("configs-description-label");
        addComponent(description);
        setComponentAlignment(description, Alignment.MIDDLE_CENTER);
    }

    private void createBox(final String label) {
        NativeButton configBoxButton = new NativeButton(label);
        configBoxButton.addStyleName("boxButton");
        configBoxButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                navigator.showConfigureTaskPage(syncConfig);
            }
        });
        horizontalLayout.addComponent(configBoxButton);
    }

    private void createActionButtons() {
        VerticalLayout buttonsLayout = new VerticalLayout();
        buttonsLayout.setSpacing(true);
        buttonsLayout.addComponent(createButton(MappingSide.RIGHT));
        buttonsLayout.addComponent(createButton(MappingSide.LEFT));
        horizontalLayout.addComponent(buttonsLayout);
    }

    private Button createButton(MappingSide exportDirection) {
        String imageFile;
        UISyncConfig config;
        
        switch (exportDirection) {
            case RIGHT:
                imageFile = "img/arrow_right.png";
                config = syncConfig;
                break;
            case LEFT:
                imageFile = "img/arrow_left.png";
                config = syncConfig.reverse();
                break;
            default:
                throw new IllegalArgumentException("Unsupported mapping direction " + exportDirection);
        }
        Button button = new Button();
        button.setIcon(new ThemeResource(imageFile));
        button.setStyleName(Runo.BUTTON_SMALL);
        button.addStyleName("configsTableArrowButton");

        final Exporter exporter = new Exporter(services, navigator, config);
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                exporter.export();
            }
        });
        return button;
    }
}

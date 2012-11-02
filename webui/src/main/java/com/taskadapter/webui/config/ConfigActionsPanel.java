package com.taskadapter.webui.config;

import com.taskadapter.web.service.Services;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.Navigator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;

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

    private void createActionButtons() {
        horizontalLayout.addComponent(new ExportButtonsFragment(services, navigator, syncConfig));
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
}

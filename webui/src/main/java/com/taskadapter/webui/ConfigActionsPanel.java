package com.taskadapter.webui;

import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

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
        addModernExportPart();
    }

    private void addDescriptionPart() {
        descriptionLayout = new HorizontalLayout();
        descriptionLayout.setWidth(100, UNITS_PERCENTAGE);
        descriptionLayout.addStyleName("configDescriptionPanel");
        descriptionLayout.addListener(new LayoutEvents.LayoutClickListener() {
            @Override
            public void layoutClick(LayoutClickEvent event) {
                navigator.showConfigureTaskPage(syncConfig);
            }
        });
        addComponent(descriptionLayout);
        addDescription();
        addEditIcon();
    }

    private void addDescription() {
        final String labelText = syncConfig.getLabel();
        Label description = new Label(labelText.isEmpty() ? NO_DESCRIPTION_TEXT : labelText, Label.CONTENT_XHTML);
        description.addStyleName("configDescriptionLabel");
        descriptionLayout.addComponent(description);
        descriptionLayout.setComponentAlignment(description, Alignment.MIDDLE_LEFT);
    }

    private void addEditIcon() {
        final Embedded editButton = new Embedded(null, new ThemeResource("img/edit.png"));
        descriptionLayout.addComponent(editButton);
        descriptionLayout.setComponentAlignment(editButton, Alignment.MIDDLE_RIGHT);
    }

    private void addModernExportPart() {
        horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        addComponent(horizontalLayout);

        horizontalLayout
                .addComponent(new UniConfigExport(messages, services,
                        navigator, syncConfig).getUI());
        horizontalLayout.addComponent(new UniConfigExport(messages, services,
                navigator, syncConfig.reverse()).getUI());
    }
}

package com.taskadapter.webui;

import com.taskadapter.web.data.Messages;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.config.ConfigAccessor;
import com.taskadapter.webui.service.Services;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
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
    private static final String NO_DESCRIPTION_TEXT = "<i>No description</i>"; //&nbsp;
    private final Messages messages;
    private final Services services;
    private final ConfigAccessor accessor;
    private HorizontalLayout descriptionLayout;

    public ConfigActionsPanel(Messages messages, Services services,
            Navigator navigator, UISyncConfig uiSyncConfig,
            ConfigAccessor accessor) {
        this.messages = messages;
        this.services = services;
        this.navigator = navigator;
        this.syncConfig = uiSyncConfig;
        this.accessor = accessor;
        buildUI();
    }

    private void buildUI() {
        addStyleName("configPanelInConfigsList");

        addDescriptionPart();
        addModernExportPart();
    }

    private void addDescriptionPart() {
        descriptionLayout = new HorizontalLayout();
        descriptionLayout.setWidth(100, Unit.PERCENTAGE);
        descriptionLayout.addStyleName("configDescriptionPanel");
        descriptionLayout.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
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
        final String labelText = accessor.nameOf(syncConfig);
        Label description = new Label(labelText.isEmpty() ? NO_DESCRIPTION_TEXT : labelText, ContentMode.HTML);
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
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        addComponent(horizontalLayout);

        horizontalLayout
                .addComponent(new UniConfigExport(messages, services,
                        navigator, syncConfig).getUI());
        horizontalLayout.addComponent(new UniConfigExport(messages, services,
                navigator, syncConfig.reverse()).getUI());
    }
}

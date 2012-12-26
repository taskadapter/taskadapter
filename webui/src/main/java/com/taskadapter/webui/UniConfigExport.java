package com.taskadapter.webui;

import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.export.Exporter;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * Controller for a single export.
 * 
 */
final class UniConfigExport {
    private final Messages messages;
    private final Services services;
    private final Navigator navigator;
    private final UISyncConfig syncConfig;

    private final HorizontalLayout ui;

    public UniConfigExport(Messages messages, Services services,
            Navigator navigator, UISyncConfig uiSyncConfig) {
        this.messages = messages;
        this.services = services;
        this.navigator = navigator;
        this.syncConfig = uiSyncConfig;

        ui = createUI();
    }

    private HorizontalLayout createUI() {
        final HorizontalLayout res = new HorizontalLayout();
        res.setWidth(274, Sizeable.UNITS_PIXELS);

        final String validationFailure = getValidationError();
        final boolean isValid = validationFailure == null;

        res.addStyleName("uniExportPanel");
        res.addStyleName(isValid ? "valid" : "invalid");

        final UIConnectorConfig config1;
        final UIConnectorConfig config2;
        final String assetName;

        if (syncConfig.isReversed()) {
            config1 = syncConfig.getConnector2();
            config2 = syncConfig.getConnector1();
            assetName = "img/arrow_left.png";
        } else {
            config1 = syncConfig.getConnector1();
            config2 = syncConfig.getConnector2();
            assetName = "img/arrow_right.png";
        }

        final Label leftLabel = createLabel(config1);
        final Label rightLabel = createLabel(config2);
        final Embedded actionLabel = new Embedded(null, new ThemeResource(
                assetName));

        leftLabel.addStyleName("left-label");
        rightLabel.addStyleName("right-label");

        res.addComponent(leftLabel);
        res.addComponent(actionLabel);
        res.addComponent(rightLabel);

        res.setExpandRatio(leftLabel, 1.0f);
        res.setExpandRatio(rightLabel, 1.0f);
        res.setSpacing(true);

        res.setComponentAlignment(leftLabel, Alignment.MIDDLE_RIGHT);
        res.setComponentAlignment(actionLabel, Alignment.MIDDLE_CENTER);
        res.setComponentAlignment(rightLabel, Alignment.MIDDLE_LEFT);

        if (isValid) {
            res.addListener(new LayoutEvents.LayoutClickListener() {
                @Override
                public void layoutClick(LayoutClickEvent event) {
                    export();
                }
            });
        } else {
            leftLabel.setDescription(validationFailure);
            rightLabel.setDescription(validationFailure);
            actionLabel.setDescription(validationFailure);
            res.setDescription(validationFailure);
        }

        return res;
    }

    private String getValidationError() {
        final StringBuilder rb = new StringBuilder();
        try {
            syncConfig.getConnector1().validateForLoad();
        } catch (BadConfigException e) {
            rb.append(messages.format("configsPage.errorSource", syncConfig
                    .getConnector1().decodeException(e)));
        }
        try {
            syncConfig.getConnector2().validateForSave();
        } catch (BadConfigException e) {
            rb.append(messages.format("configsPage.errorDestination",
                    syncConfig.getConnector2().decodeException(e)));
        }
        if (rb.length() == 0) {
            return null;
        }
        return messages.format("configsPage.validationTemplate", rb.toString());
    }

    private Label createLabel(UIConnectorConfig connector) {
        final Label res = new Label(connector.getLabel());
        res.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        return res;
    }

    void export() {
        new Exporter(messages, services, navigator, syncConfig).export();
    }

    AbstractComponent getUI() {
        return ui;
    }

}

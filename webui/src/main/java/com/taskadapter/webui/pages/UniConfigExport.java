package com.taskadapter.webui.pages;

import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.Page;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.server.Sizeable.Unit.PIXELS;

/**
 * Controller for a single export.
 * 
 */
final class UniConfigExport {
    
    private static String getValidationError(UISyncConfig syncConfig) {
        final StringBuilder rb = new StringBuilder();
        try {
            syncConfig.getConnector1().validateForLoad();
        } catch (BadConfigException e) {
            rb.append(Page.MESSAGES.format("configsPage.errorSource",
                    syncConfig.getConnector1().decodeException(e)));
        }
        try {
            syncConfig.getConnector2().validateForSave();
        } catch (BadConfigException e) {
            rb.append(Page.MESSAGES.format("configsPage.errorDestination",
                    syncConfig.getConnector2().decodeException(e)));
        }
        if (rb.length() == 0) {
            return null;
        }
        return Page.MESSAGES.format("configsPage.validationTemplate",
                rb.toString());
    }

    private static Label createLabel(UIConnectorConfig connector) {
        final Label res = new Label(connector.getLabel());
        res.setWidth(100, PERCENTAGE);
        return res;
    }

    void export() {
     //FIXME:!!!
        //      new Exporter(messages, services, navigator, syncConfig).export();
    }

    /**
     * Renders a config.
     * 
     * @param config
     *            config to render.
     * @param callback
     *            action to invoke when user clicks on the config.
     */
    public static Component render(UISyncConfig config, final Runnable callback) {
        final HorizontalLayout res = new HorizontalLayout();
        res.setWidth(274, PIXELS);

        final String validationFailure = getValidationError(config);
        final boolean isValid = validationFailure == null;

        res.addStyleName("uniExportPanel");
        res.addStyleName(isValid ? "valid" : "invalid");

        final UIConnectorConfig config1;
        final UIConnectorConfig config2;
        final String assetName;

        if (config.isReversed()) {
            config1 = config.getConnector2();
            config2 = config.getConnector1();
            assetName = "img/arrow_left.png";
        } else {
            config1 = config.getConnector1();
            config2 = config.getConnector2();
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
            res.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
                @Override
                public void layoutClick(LayoutClickEvent event) {
                    callback.run();
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

}

package com.taskadapter.webui.pages;

import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.web.DroppingNotSupportedException;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.ImageLoader;
import com.taskadapter.webui.Page;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.Embedded;
import com.taskadapter.vaadin14shim.HorizontalLayout;
import com.taskadapter.vaadin14shim.Label;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;
import scala.collection.Seq;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.server.Sizeable.Unit.PIXELS;

/**
 * Controller for a single export.
 *
 */
final class UniConfigExport {

    static int width = 300;

    /**
     * Unified config export callback.
     */
    public interface Callback {
        /**
         * Performs a regular export.
         */
        void doExport();

        /**
         * Performs a drop-in export.
         *
         * @param file
         *            file to export.
         */
        void dropInExport(Html5File file);
    }

    private static String getDropInValidationError(UISyncConfig syncConfig)
            throws DroppingNotSupportedException {
        final StringBuilder rb = new StringBuilder();
        try {
            syncConfig.getConnector1().validateForDropIn();
        } catch (BadConfigException e) {
            rb.append(Page.message("configsPage.errorSource",
                    syncConfig.getConnector1().decodeException(e)));
        }
        Seq<BadConfigException> saveErrors = syncConfig.getConnector2().validateForSave(syncConfig.fieldMappings());
        saveErrors.foreach(e -> rb.append(Page.message("configsPage.errorDestination",
                syncConfig.getConnector2().decodeException(e))));

        if (rb.length() == 0) {
            return null;
        }
        return Page.message("configsPage.validationTemplate",
                rb.toString());
    }

    private static String getValidationError(UISyncConfig syncConfig) {
        StringBuilder rb = new StringBuilder();

        Seq<BadConfigException> loadErrors = syncConfig.getConnector1().validateForLoad();
        loadErrors.foreach(e -> rb.append(Page.message("configsPage.errorSource",
                syncConfig.getConnector1().decodeException(e))));

        Seq<BadConfigException> saveErrors = syncConfig.getConnector2().validateForSave(syncConfig.fieldMappings());
        saveErrors.foreach(e -> rb.append(Page.message("configsPage.errorDestination",
                    syncConfig.getConnector2().decodeException(e))));

        if (rb.length() == 0) {
            return null;
        }
        return Page.message("configsPage.validationTemplate",
                rb.toString());
    }

    private static Label createLabel(UIConnectorConfig connector) {
        final Label res = new Label(connector.getConnectorSetup().label());
        res.setWidth(100, PERCENTAGE);
        res.addClassName(ValoTheme.LABEL_H3);
        return res;
    }

    private static Component renderSimple(UISyncConfig config,
            final Callback callback) {
        final HorizontalLayout res = new HorizontalLayout();
        res.setWidth(width, PIXELS);
        res.setHeight(65, PIXELS);

        final String validationFailure = getValidationError(config);
        final boolean isValid = validationFailure == null;

        res.addClassName("uniExportPanel");
        res.addClassName(isValid ? "valid" : "invalid");

        final UIConnectorConfig config1;
        final UIConnectorConfig config2;
        final String assetName;

        if (config.isReversed()) {
            config1 = config.getConnector2();
            config2 = config.getConnector1();
            assetName = "arrow_left.png";
        } else {
            config1 = config.getConnector1();
            config2 = config.getConnector2();
            assetName = "arrow_right.png";
        }

        final Label leftLabel = createLabel(config1);
        final Label rightLabel = createLabel(config2);
        final Embedded actionLabel = new Embedded(null, ImageLoader.getImage(assetName));

        leftLabel.addClassName("left-label");
        rightLabel.addClassName("right-label");

        res.add(leftLabel);
        res.add(actionLabel);
        res.add(rightLabel);

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
                    callback.doExport();
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
    
    private static Component wrapDropArea(final Callback callback,
            final Component dropLabel) {
        final DragAndDropWrapper dadw = new DragAndDropWrapper(dropLabel);

        dadw.setDropHandler(new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
                final Transferable t = event.getTransferable();
                if (!(t instanceof WrapperTransferable))
                    return;
                final WrapperTransferable wt = (WrapperTransferable) t;
                if (wt.getFiles().length == 0)
                    return;
                if (wt.getFiles().length > 1) {
                    Notification.show(Page.message("configsPage.configElement.dndIcon.multiDropError"));
                    return;
                }

                callback.dropInExport(wt.getFiles()[0]);
            }
        });
        return dadw;
    }

    /**
     * Renders the config box with connectors' names, arrow and possibly "drop" icon.
     *
     * @param config   config to render.
     * @param callback action to invoke when user clicks on the config.
     */
    public static Component render(UISyncConfig config, final Callback callback) {
        final HorizontalLayout layout = new HorizontalLayout();
        final Component regularExportBox = renderSimple(config, callback);
        layout.setWidth(width, PIXELS);
        try {
            final String validationFailure = getDropInValidationError(config);
            final boolean isValid = validationFailure == null;

            final Embedded dropLabel = new Embedded(null, ImageLoader.getImage("file_drop.gif"));
            dropLabel.setDescription(Page.message("configsPage.configElement.dndIcon.tooltip"));

            if (!isValid) {
                dropLabel.setDescription(validationFailure);
            }
            
            dropLabel.setWidth(32, PIXELS);
            regularExportBox.setWidth(width - 32, PIXELS);
            
            if (config.isReversed()) {
                layout.add(regularExportBox);
                layout.add(dropLabel);
                layout.setComponentAlignment(dropLabel, Alignment.MIDDLE_RIGHT);
            } else {
                layout.add(dropLabel);
                layout.add(regularExportBox);
                layout.setComponentAlignment(dropLabel, Alignment.MIDDLE_LEFT);
            }
            layout.setExpandRatio(regularExportBox, 1f);
            layout.setExpandRatio(dropLabel, 0.0f);

            if (isValid) {
                return wrapDropArea(callback, layout);
            }
        } catch (DroppingNotSupportedException e) {
            regularExportBox.setWidth(width, PIXELS);
            layout.add(regularExportBox);
        }
        return layout;
    }
}

package com.taskadapter.webui.pages;

import static com.vaadin.server.Sizeable.Unit.PIXELS;

import com.taskadapter.connector.definition.TaskError;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.taskadapter.webui.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.taskadapter.vaadin14shim.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.UI;
import com.taskadapter.vaadin14shim.VerticalLayout;

/**
 * Component factory for different sync actions.
 */
public final class SyncActionComponents {

    /**
     * Renders a "saving data" indicator.
     * 
     * @param destination
     *            save destination.
     * @return progress indicator.
     */
    public static ProgressIndicator renderSaveIndicator(
            UIConnectorConfig destination) {
        final ProgressIndicator saveProgress = new ProgressIndicator();
        saveProgress.setIndeterminate(false);
        saveProgress.setEnabled(true);
        saveProgress.setCaption(Page.message("action.saving",
                destination.getDestinationLocation()));
        return saveProgress;

    }

    /**
     * Creates a load indicator.
     * 
     * @param source
     *            source config/name.
     * @return load indicator.
     */
    public static Component renderLoadIndicator(UIConnectorConfig source) {
        return renderLoadIndicator(source.getSourceLocation() + " ("
                + source.getLabel() + ")");

    }

    public static Component renderLoadIndicator(final String sourceDescription) {
        final VerticalLayout res = new VerticalLayout();
        final String labelText = Page.message("action.loadingData",
                sourceDescription);

        final Label label = new Label(labelText);
        label.setWidth(800, PIXELS);
        res.add(label);

        final ProgressBar loadProgress = new ProgressBar();
        loadProgress.setIndeterminate(true);
        UI.getCurrent().setPollInterval(200);

        res.add(loadProgress);
        return res;
    }
}

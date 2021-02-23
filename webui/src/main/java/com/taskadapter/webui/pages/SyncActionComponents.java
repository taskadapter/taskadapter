package com.taskadapter.webui.pages;

import com.taskadapter.connector.definition.TaskError;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.taskadapter.webui.Page;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;

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

    public static ProgressBar renderSaveIndicator(
            UIConnectorConfig destination) {
        final ProgressBar saveProgress = new ProgressBar();
        saveProgress.setIndeterminate(false);
        // TODO 14
//        saveProgress.setEnabled(true);
//        saveProgress.setCaption(Page.message("action.saving",
//                destination.getDestinationLocation()));
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
        var layout = new VerticalLayout();
        var labelText = Page.message("action.loadingData",
                sourceDescription);

        var label = new Label(labelText);
        label.setWidth("800px");
        layout.add(label);

        var loadProgress = new ProgressBar();
        loadProgress.setIndeterminate(true);
        // TODO 14 check this
//        UI.getCurrent().setPollInterval(200);

        layout.add(loadProgress);
        return layout;
    }
}

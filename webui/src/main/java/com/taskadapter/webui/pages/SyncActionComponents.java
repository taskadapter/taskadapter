package com.taskadapter.webui.pages;

import static com.vaadin.server.Sizeable.Unit.PIXELS;

import com.taskadapter.connector.definition.TaskError;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.taskadapter.webui.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Component factory for different sync actions.
 */
public final class SyncActionComponents {

    private static String quot(String str) {
        return str.replace("&", "&amp;").replace("\"", "&quot;")
                .replace("<", "&lt;").replace(">", "&gt;");
    }

    /**
     * Adds connector errors into the output.
     * 
     * @param container
     *            error container.
     * @param connector
     *            connector, which created the errors.
     * @param generalErrors
     *            list of general errors.
     * @param taskErrors
     *            task errors.
     */
    public static void addErrors(ComponentContainer container,
            UIConnectorConfig connector, scala.collection.immutable.List<Throwable> generalErrors,
            scala.collection.immutable.List<TaskError<Throwable>> taskErrors) {

        if (generalErrors.isEmpty() && taskErrors.isEmpty())
            return;

        container.addComponent(new Label(
                "There were some problems during export:"));
        String errorText = "";
        scala.collection.Iterator<Throwable> generalErrorsIter = generalErrors.iterator();
        while (generalErrorsIter.hasNext()) {
            Throwable t = generalErrorsIter.next();
            errorText += quot(connector.decodeException(t)) + "<br/>\n";
        }
        scala.collection.Iterator<TaskError<Throwable>> taskErrorsIter = taskErrors.iterator();
        while (taskErrorsIter.hasNext()) {
            TaskError<Throwable> error = taskErrorsIter.next();
            errorText += "Task " + error.getTask().getId() + " (\""
                    + error.getTask() + "\"): "
                    + connector.decodeException(error.getErrors());
        }
        final Label errorTextLabel = new Label(errorText);
        errorTextLabel.addStyleName("errorMessage");
        errorTextLabel.setContentMode(ContentMode.HTML);
        container.addComponent(errorTextLabel);
    }

    public static HorizontalLayout createdExportResultLabel(String labelName,
            String labelValue) {
        final Label lName = new Label("<strong>" + labelName + "</strong>");
        lName.setContentMode(ContentMode.HTML);
        lName.setWidth("98px");

        final Label lValue = new Label("<em>" + labelValue + "</em>");
        lValue.setContentMode(ContentMode.HTML);

        final HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(lName);
        hl.addComponent(lValue);
        hl.addStyleName("export-result");

        return hl;
    }

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
        res.addComponent(label);

        final ProgressBar loadProgress = new ProgressBar();
        loadProgress.setIndeterminate(true);
        UI.getCurrent().setPollInterval(200);

        res.addComponent(loadProgress);
        return res;
    }
}

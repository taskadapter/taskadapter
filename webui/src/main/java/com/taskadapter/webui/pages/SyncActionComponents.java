package com.taskadapter.webui.pages;

import static com.vaadin.server.Sizeable.Unit.PIXELS;

import java.util.List;

import com.taskadapter.connector.definition.TaskError;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.taskadapter.webui.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
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
            UIConnectorConfig connector, List<Throwable> generalErrors,
            List<TaskError<Throwable>> taskErrors) {

        if (generalErrors.isEmpty() && taskErrors.isEmpty())
            return;

        container.addComponent(new Label(
                "There were some problems during export:"));
        String errorText = "";
        for (Throwable e : generalErrors) {
            errorText += quot(connector.decodeException(e)) + "<br/>";
        }
        for (TaskError<Throwable> error : taskErrors) {
            errorText += "Task " + error.getTask().getId() + " (\""
                    + error.getTask().getSummary() + "\"): "
                    + connector.decodeException(error.getErrors());
        }
        final Label errorTextLabel = new Label(errorText);
        errorTextLabel.addStyleName("errorMessage");
        errorTextLabel.setContentMode(ContentMode.HTML);
        container.addComponent(errorTextLabel);
    }

    public static HorizontalLayout createdExportResultLabel(String labelName,
            String labelValue) {
        final Label lName = new Label("<strong>" + labelName + ":</strong>");
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
        saveProgress.setCaption(Page.MESSAGES.format("action.saving",
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
        final String labelText = Page.MESSAGES.format("action.loadingData",
                sourceDescription);

        final Label label = new Label(labelText);
        label.setWidth(800, PIXELS);
        res.addComponent(label);

        final ProgressIndicator loadProgress = new ProgressIndicator();
        loadProgress.setIndeterminate(true);
        loadProgress.setPollingInterval(200);

        res.addComponent(loadProgress);
        return res;
    }

    /**
     * Renders a welcoming message before reading/downloading message.
     * 
     * @param htmlMessage
     *            html-formatted string with the message.
     * @param onAccepted
     *            action to perform when user accepts an operation.
     * @param onCancel
     *            action to perform when user cancels the operation.
     * @return download welcome message.
     */
    public static Component renderDownloadWelcome(String htmlMessage,
            final Runnable onAccepted, final Runnable onCancel) {
        final VerticalLayout res = new VerticalLayout();

        final Label label = new Label(htmlMessage);
        label.setWidth(800, PIXELS);
        label.setContentMode(ContentMode.HTML);
        res.addComponent(label);

        final HorizontalLayout buttonsLayout = new HorizontalLayout();
        final Button goButton = new Button(Page.MESSAGES.get("button.go"));
        goButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                onAccepted.run();
            }
        });
        buttonsLayout.addComponent(goButton);

        final Button backButton = new Button(Page.MESSAGES.get("button.cancel"));
        backButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                onCancel.run();
            }
        });
        buttonsLayout.addComponent(backButton);

        res.addComponent(buttonsLayout);

        return res;
    }

}

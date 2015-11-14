package com.taskadapter.webui.config;

import com.taskadapter.config.StorageException;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.CloneDeletePanel;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.data.ExceptionFormatter;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;

public class EditConfigPage {

    public interface Callback {
        /**
         * User requested synchronization in "forward" directions (from left to
         * right).
         * 
         * @param config
         *            config for the operation.
         */
        void forwardSync(UISyncConfig config);

        /**
         * User requested synchronization in "reverse" direction (from right to
         * left).
         * 
         * @param config
         *            config for the operation.
         */
        void backwardSync(UISyncConfig config);

        /**
         * User attempts to leave this page.
         */
        void back();

    }

    private static final Logger LOGGER = LoggerFactory
            .getLogger(EditConfigPage.class);

    private final UISyncConfig config;
    private final ConfigOperations configOps;
    private final Callback callback;

    private final VerticalLayout layout;
    private final Label errorMessageLabel;

    private final OnePageEditor editor;

    private EditConfigPage(final UISyncConfig config,
            ConfigOperations operations, boolean allowFullFSAccess,
            String error, final Callback callback) {
        this.config = config;
        this.configOps = operations;
        this.callback = callback;

        layout = new VerticalLayout();
        layout.setSpacing(true);

        final HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setWidth(100, PERCENTAGE);
        final Component cloneDeletePanel = CloneDeletePanel.render(config,
                operations, callback::back);
        buttonsLayout.addComponent(cloneDeletePanel);
        buttonsLayout.setComponentAlignment(cloneDeletePanel,
                Alignment.MIDDLE_RIGHT);
        layout.addComponent(buttonsLayout);

        layout.addComponent(createEditDescriptionElement(config));

        editor = new OnePageEditor(Page.MESSAGES, new Sandbox(
                allowFullFSAccess, operations.syncSandbox), config,
                mkExportAction(new Runnable() {
                    @Override
                    public void run() {
                        callback.backwardSync(config);
                    }
                }), mkExportAction(new Runnable() {
                    @Override
                    public void run() {
                        callback.forwardSync(config);
                    }
                }));
        layout.addComponent(editor.getUI());

        errorMessageLabel = new Label(error);
        errorMessageLabel.addStyleName("error-message-label");
        errorMessageLabel.setWidth(100, PERCENTAGE);
        errorMessageLabel.setContentMode(ContentMode.HTML);
        layout.addComponent(errorMessageLabel);
        layout.addComponent(createBottomButtons());
    }

    /**
     * Creates "edit description" element for the config.
     * 
     * @param config
     *            config.
     * @return description editor.
     */
    private static Component createEditDescriptionElement(UISyncConfig config) {
        HorizontalLayout descriptionLayout = new HorizontalLayout();
        descriptionLayout.setSpacing(true);
        descriptionLayout.addComponent(new Label(Page.MESSAGES
                .get("editConfig.description")));
        TextField descriptionField = new TextField();
        descriptionField.addStyleName("configEditorDescriptionLabel");
        MethodProperty<String> label = new MethodProperty<>(config,
                "label");
        descriptionField.setPropertyDataSource(label);
        descriptionLayout.addComponent(descriptionField);

        return descriptionLayout;
    }

    private Component createBottomButtons() {
        final HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setWidth(100, PERCENTAGE);
        final HorizontalLayout rightLayout = new HorizontalLayout();
        buttonsLayout.addComponent(rightLayout);
        buttonsLayout
                .setComponentAlignment(rightLayout, Alignment.BOTTOM_RIGHT);

        final Button saveButton = new Button(Page.MESSAGES.get("button.save"));
        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                saveClicked();
            }
        });
        rightLayout.addComponent(saveButton);

        final Button backButton = new Button(Page.MESSAGES.get("button.close"));
        backButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                callback.back();
            }
        });        
        rightLayout.addComponent(backButton);
        
        return buttonsLayout;
    }

    private void saveClicked() {
        if (validate()) {
            save();
            Notification.show("", Page.MESSAGES.get("editConfig.messageSaved"),
                    Notification.Type.HUMANIZED_MESSAGE);
        }
    }

    /** Wraps an export action. */
    private Runnable mkExportAction(final Runnable exporter) {
        return new Runnable() {
            @Override
            public void run() {
                if (validate()) {
                    save();
                    exporter.run();
                }
            }
        };
    }

    private boolean validate() {
        errorMessageLabel.setValue("");
        try {
            editor.validate();
        } catch (FieldAlreadyMappedException e) {
            String s = Page.MESSAGES.format(
                    "editConfig.error.fieldAlreadyMapped", e.getValue());
            errorMessageLabel.setValue(s);
            return false;
        } catch (FieldNotMappedException e) {
            String fieldDisplayName = GTaskDescriptor.getDisplayValue(e
                    .getField());
            String s = Page.MESSAGES.format(
                    "error.fieldSelectedForExportNotMapped", fieldDisplayName);
            errorMessageLabel.setValue(s);
            return false;
        } catch (BadConfigException e) {
            String s = ExceptionFormatter.format(e);
            errorMessageLabel.setValue(s);
            return false;
        }
        return true;
    }

    private void save() {
        try {
            configOps.saveConfig(config);
        } catch (StorageException e) {
            String message = Page.MESSAGES.format("editConfig.error.cantSave",
                    e.getMessage());
            errorMessageLabel.setValue(message);
            LOGGER.error(message, e);
        }
    }

    public void setErrorMessage(String errorMessage) {
        errorMessageLabel.setValue(errorMessage);
    }

    /**
     * Renders a new config editor.
     * 
     * @param config
     *            config to edit.
     * @param operations
     *            config operations.
     * @param error
     *            optional welcome error. May be null.
     * @param callback
     *            callback to invoke when user attempts to leave this page.
     * @return edit page UI.
     */
    public static Component render(UISyncConfig config,
            ConfigOperations operations, boolean allowFullFSAccess,
            String error, Callback callback) {
        return new EditConfigPage(config, operations, allowFullFSAccess, error,
                callback).layout;
    }
}

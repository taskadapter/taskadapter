package com.taskadapter.webui.pages.config;

import com.google.common.base.Strings;
import com.taskadapter.common.ui.FieldMapping;
import com.taskadapter.config.StorageException;
import com.taskadapter.connector.definition.exception.FieldNotMappedException;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.ui.HtmlLabel;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.Sizes;
import com.taskadapter.webui.config.FieldAlreadyMappedException;
import com.taskadapter.webui.config.TaskFieldsMappingFragment;
import com.taskadapter.webui.data.ExceptionFormatter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Fields mapping plus "description" element at the top.
 */
public class EditConfigPage extends VerticalLayout {
    private final ConfigOperations configOps;
    private final Messages messages;
    private final String error;
    private final UISyncConfig config;

    private static final Logger logger = LoggerFactory.getLogger(EditConfigPage.class);

    private final Binder<UISyncConfig> binder = new Binder<>(UISyncConfig.class);

    private final TextField descriptionField = EditorUtil.textInput(binder, "label");

    private final Label errorMessageLabel = new HtmlLabel("");
    private final TaskFieldsMappingFragment taskFieldsMappingFragment;

    public EditConfigPage(ConfigOperations configOps, Messages messages, String error, UISyncConfig config) {
        this.configOps = configOps;
        this.messages = messages;
        this.error = error;
        this.config = config;

        descriptionField.setWidth(Sizes.editConfigDescriptionFieldWidth);
        errorMessageLabel.addClassName("error-message-label");
        errorMessageLabel.setWidth("100%");
        errorMessageLabel.setVisible(false);

        HorizontalLayout buttons = createConfigOperationsButtons();
        buttons.setWidth("20%");
        HorizontalLayout topRowLayout = new HorizontalLayout(descriptionField, buttons);

        binder.readBean(config);

        taskFieldsMappingFragment = new TaskFieldsMappingFragment(messages,
                config.getConnector1().getAllFields(), config.getConnector1().fieldNames(), config.getConnector1().getLabel(),
                config.getConnector2().getAllFields(), config.getConnector2().fieldNames(), config.getConnector2().getLabel(),
                config.getNewMappings());

        add(topRowLayout,
                taskFieldsMappingFragment.getComponent(),
                errorMessageLabel);

        if (!Strings.isNullOrEmpty(error)) {
            showError(error);
        }
    }

    private HorizontalLayout createConfigOperationsButtons() {
        var buttonsLayout = new HorizontalLayout();
        var rightLayout = new HorizontalLayout();
        rightLayout.setSpacing(true);
        buttonsLayout.add(rightLayout);
        var saveButton = new Button(Page.message("button.save"),
                event -> saveClicked());
        rightLayout.add(saveButton);
        return buttonsLayout;
    }

    private void saveClicked() {
        if (validate()) {
            save();
            Notification.show(Page.message("editConfig.messageSaved"));
        }
    }

    private boolean validate() {
        clearErrorMessage();
        try {
            // need to save the UI elements into the model  first
            saveUiElementsIntoModel();

            // TODO validate left/right editors too. this was lost during the last refactoring.
            taskFieldsMappingFragment.validate();
        } catch (FieldAlreadyMappedException e) {
            var s = Page.message("editConfig.error.fieldAlreadyMapped", e.getValue());
            showError(s);
            return false;
        } catch (FieldNotMappedException e) {
            var s = Page.message("error.fieldSelectedForExportNotMapped", e.getFieldName());
            showError(s);
            return false;
        } catch (BadConfigException e) {
            var s = ExceptionFormatter.format(e);
            showError(s);
            return false;
        }
        return true;
    }


    private void save() {
        try {
            saveUiElementsIntoModel();
            var newFieldMappings = getElements();
            var newConfig = new UISyncConfig(
                    config.getTaskKeeperLocationStorage(),
                    config.getConfigId(),
                    descriptionField.getValue(),
                    config.getConnector1(),
                    config.getConnector2(),
                    newFieldMappings,
                    config.isReversed()
            );
            configOps.saveConfig(newConfig);
        } catch (StorageException e) {
            var message = Page.message("editConfig.error.cantSave", e.getMessage());
            showError(message);
            logger.error(message, e);
        }
    }

    public void showError(String errorMessage) {
        errorMessageLabel.setVisible(true);
        errorMessageLabel.setText(errorMessage);
    }

    private void clearErrorMessage() {
        errorMessageLabel.setVisible(false);
        errorMessageLabel.setText("");
    }

    private List<FieldMapping<?>> getElements() {
        return taskFieldsMappingFragment.getElements();
    }

    private void saveUiElementsIntoModel() {
        taskFieldsMappingFragment.save();
    }

}

package com.taskadapter.webui.config;

import com.taskadapter.common.ui.FieldMapping;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.model.Field;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.Validatable;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.uiapi.SavableComponent;
import com.taskadapter.webui.Page;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TaskFieldsMappingFragment implements SavableComponent, Validatable {
    private final Logger logger = LoggerFactory.getLogger(TaskFieldsMappingFragment.class);

    // TODO maybe merge this help file with all the other localized strings? but it has some rules about namings...
    private static final String BUNDLE_NAME = "help";
    private static final Messages HELP_MESSAGES = new Messages(BUNDLE_NAME);
//  private static final int  HELP_ICON_RESOURCE = new ThemeResource("../runo/icons/16/help.png");

    private final Messages messages;
    private final List<Field<?>> connector1SupportedFields;
    private final Messages connector1Messages;
    private final String connector1Label;
    private final List<Field<?>> connector2SupportedFields;
    private final Messages connector2Messages;
    private final String connector2Label;
    private final List<FieldMapping<?>> mappings;

    private final VerticalLayout layout = new VerticalLayout();
    private final FormLayout gridLayout = new FormLayout();

    private EditablePojoMappings editablePojoMappings;

    public TaskFieldsMappingFragment(Messages messages,
                                     List<Field<?>> connector1SupportedFields,
                                     Messages connector1Messages,
                                     String connector1Label,
                                     List<Field<?>> connector2SupportedFields,
                                     Messages connector2Messages,
                                     String connector2Label,
                                     List<FieldMapping<?>> mappings) {
        this.messages = messages;
        this.connector1SupportedFields = connector1SupportedFields;
        this.connector1Messages = connector1Messages;
        this.connector1Label = connector1Label;
        this.connector2SupportedFields = connector2SupportedFields;
        this.connector2Messages = connector2Messages;
        this.connector2Label = connector2Label;
        this.mappings = mappings;
        buildUi();
    }

    private void buildUi() {
        gridLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("2em", 1),
                new FormLayout.ResponsiveStep("2em", 2),
                new FormLayout.ResponsiveStep("10em", 3),
                new FormLayout.ResponsiveStep("10em", 4),
                new FormLayout.ResponsiveStep("10em", 5),
                new FormLayout.ResponsiveStep("5em", 6));

        editablePojoMappings = new EditablePojoMappings(mappings,
                new ConnectorFieldLoader(connector1SupportedFields),
                new ConnectorFieldLoader(connector2SupportedFields));

        layout.add(
                new Label(messages.get("editConfig.mappings.caption")),
                gridLayout);

        rebuildMappingUI();
        addNewRowButton();

    }

    private void rebuildMappingUI() {
        gridLayout.removeAll();
        addTableHeaders();
        addFieldsToUI();
    }

    private void addTableHeaders() {
        var label2 = new Label(messages.get("editConfig.mappings.exportFieldHeader"));
        label2.addClassName("fieldsTitle");
        label2.setWidth("40px");
        gridLayout.add(label2);
        var label = new Label(" ");
        label.setWidth("20px");
        gridLayout.add(label);

        var label1 = new Label(connector1Label);
        label1.addClassName("fieldsTitle");
        label1.setWidth("230px");
        gridLayout.add(label1);

        var label3 = new Label(connector2Label);
        label3.addClassName("fieldsTitle");
        label3.setWidth("230px");
        gridLayout.add(label3);

        var label4 = new Label(messages.get("editConfig.mappings.defaultValueColumn"));
        label4.addClassName("fieldsTitle");
        label4.setWidth("180px");
        gridLayout.add(label4);
        var column5label = new Label();
        column5label.setWidth("30px");
        gridLayout.add(column5label);
    }

    /**
     * Add all rows to mappings table
     */
    private void addFieldsToUI() {
        editablePojoMappings.getEditablePojoMappings().forEach(e -> addRowToVaadinForm(e));
    }

    /**
     * Add a row to mapping table:
     * selected, tooltip, connector 1 field name, connector 2 field name, default value.
     */
    private void addRowToVaadinForm(EditableFieldMapping field) {

        addCheckbox(field.getBinder());
/////////////////     TODO TA3 help is per connector field, not for the whole row now.
        String helpForField = null; //getHelpForField(field);
        if (helpForField != null) addHelp(helpForField);
        else addEmptyCell();
        addConnectorField(field.getBinder(), connector1SupportedFields, connector1Messages, field.getFieldInConnector1(), "fieldInConnector1");
        addConnectorField(field.getBinder(), connector2SupportedFields, connector2Messages, field.getFieldInConnector2(), "fieldInConnector2");
        addTextFieldForDefaultValue(field.getBinder());
        addRemoveRowButton(field);
        field.getBinder().readBean(field);
    }

    private void addRemoveRowButton(EditableFieldMapping field) {
        var button = new Button(Page.message("editConfig.mappings.buttonRemove"),
                e -> removeRow(field));
        gridLayout.add(button);
    }

    private void removeRow(EditableFieldMapping field) {
        // save the current fields info into the data model first
        save();
        editablePojoMappings.removeFieldFromList(field);
        rebuildMappingUI();
    }

    private void addCheckbox(Binder<EditableFieldMapping> binder) {
        var checkbox = EditorUtil.checkbox("", "Include this field when exporting data", binder, "selected");
        gridLayout.add(checkbox);
    }

    private void addTextFieldForDefaultValue(Binder<EditableFieldMapping> binder) {
        var field = EditorUtil.textInput(binder, "defaultValue");
        gridLayout.add(field);
    }

    private void addHelp(String helpForField) {
//    var helpIcon = new Embedded(null, TaskFieldsMappingFragment.HELP_ICON_RESOURCE);
//    helpIcon.setDescription(helpForField);
//    gridLayout.add(helpIcon);
//    gridLayout.setComponentAlignment(helpIcon, Alignment.MIDDLE_CENTER);
    }

    private void addEmptyCell() {
        var emptyLabel = new Label(" ");
        gridLayout.add(emptyLabel);
    }

    private void addConnectorField(Binder<EditableFieldMapping> binder,
                                   List<Field<?>> connectorFields,
                                   Messages connectorMessages,
                                   String selectedValue,
                                   String propertyName) {
        var combobox = new ComboBox<String>();
        combobox.setItemLabelGenerator(fieldName ->
                Optional.ofNullable(connectorMessages.getNoDefault(fieldName)).orElse(fieldName));
        combobox.setItems(connectorFields.stream().map(Field::getFieldName));
        combobox.setAllowCustomValue(true);
        gridLayout.add(combobox);
        combobox.setValue(selectedValue);
        binder.bind(combobox, propertyName);
    }

    private void addNewRowButton() {
        var button = new Button(Page.message("editConfig.mappings.buttonAdd"),
                e -> {
                    var m = new EditableFieldMapping(
                            new Binder<>(EditableFieldMapping.class),
                            UUID.randomUUID().toString(), "", "", false, "");
                    editablePojoMappings.add(m);
                    addRowToVaadinForm(m);
                });
        layout.add(button);
    }

    @Override
    public void validate() throws BadConfigException {
        editablePojoMappings.validate();
    }

    public List<FieldMapping<?>> getElements() {
        return editablePojoMappings.getElements();
    }

    @Override
    public Component getComponent() {
        return layout;
    }

    @Override
    public boolean save() {
        for (EditableFieldMapping editablePojoMapping : editablePojoMappings.getEditablePojoMappings()) {
            try {
                editablePojoMapping.getBinder().writeBean(editablePojoMapping);
            } catch (ValidationException e) {
                logger.error("validation exception when saving UI elements into the model: " + e.toString(), e);
                return false;
            }
        }
        return true;
    }
}



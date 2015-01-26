package com.taskadapter.webui.config;

import com.google.common.collect.ImmutableSet;
import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.NewMappings;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.web.configeditor.Validatable;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import java.util.Arrays;

import static com.vaadin.server.Sizeable.Unit.PIXELS;

public class TaskFieldsMappingFragment implements Validatable {
    private static final int COLUMN_DESCRIPTION = 0;
    private static final int COLUMN_HELP = 1;
    private static final int COLUMN_LEFT_CONNECTOR = 2;
    private static final int COLUMN_RIGHT_CONNECTOR = 3;
    private static final int COLUMN_DEFAULT_VALUE = 4;
    private static final int COLUMNS_NUMBER = 5;

    // TODO maybe merge this help file with all the other localized strings? but it has some rules about namings...
    private static final String BUNDLE_NAME = "help";
    private static final Messages HELP_MESSAGES = new Messages(BUNDLE_NAME);
    private static final Resource HELP_ICON_RESOURCE = new ThemeResource("../runo/icons/16/help.png");

    private GridLayout gridLayout;

    
    private final Panel ui;
    private Messages messages;
    private UIConnectorConfig connector1;
    private UIConnectorConfig connector2;
    private NewMappings originalMappings;
    private NewMappings mappings;

    public TaskFieldsMappingFragment(Messages messages, UIConnectorConfig connector1,
                                     UIConnectorConfig connector2, NewMappings mappings) {
        this.messages = messages;
        this.connector1 = connector1;
        this.connector2 = connector2;
        this.mappings = mappings;
        this.originalMappings = new NewMappings(mappings.getMappings());

        ui = new Panel(messages.get("editConfig.mappings.caption"));
        
        addFields();
    }

    private void addFields() {
        createGridLayout();
        addTableHeaders();
        addSupportedFields();
    }

    private void createGridLayout() {
        gridLayout = new GridLayout();
        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);
        gridLayout.setRows(GTaskDescriptor.FIELD.values().length + 2);
        gridLayout.setColumns(COLUMNS_NUMBER);
        ui.setContent(gridLayout);
    }

    private void addTableHeaders() {
        Label label2 = new Label(messages.get("editConfig.mappings.exportFieldHeader"));
        label2.addStyleName("fieldsTitle");
        label2.setWidth(50, PIXELS);
        gridLayout.addComponent(label2, COLUMN_DESCRIPTION, 0);
        gridLayout.setComponentAlignment(label2, Alignment.MIDDLE_LEFT);

        Label label = new Label(" ");
        label.setWidth(20, PIXELS);
        gridLayout.addComponent(label, COLUMN_HELP, 0);

        Label label1 = new Label(connector1.getLabel());
        label1.addStyleName("fieldsTitle");
        label1.setWidth(180, PIXELS);
        gridLayout.addComponent(label1, COLUMN_LEFT_CONNECTOR, 0);
        gridLayout.setComponentAlignment(label1, Alignment.MIDDLE_LEFT);


        Label label3 = new Label(connector2.getLabel());
        label3.addStyleName("fieldsTitle");
        label3.setWidth(180, PIXELS);
        gridLayout.addComponent(label3, COLUMN_RIGHT_CONNECTOR, 0);
        gridLayout.setComponentAlignment(label3, Alignment.MIDDLE_LEFT);

        Label label4 = new Label(messages.get("editConfig.mappings.defaultValueColumn"));
        label4.addStyleName("fieldsTitle");
        label4.setWidth(180, PIXELS);
        gridLayout.addComponent(label4, COLUMN_DEFAULT_VALUE, 0);
        gridLayout.setComponentAlignment(label4, Alignment.MIDDLE_LEFT);

        gridLayout.addComponent(new Label("<hr>", ContentMode.HTML), 0, 1, COLUMNS_NUMBER - 1, 1);
    }

    private void addSupportedFields() {
        // TODO sort?
        for (FieldMapping mapping : mappings.getMappings()) {
            addField(mapping);
        }
    }

    private void addField(FieldMapping field) {
        addCheckbox(field);
        String helpForField = getHelpForField(field);
        if (helpForField != null) {
            addHelp(helpForField);
        } else {
            addEmptyCell();
        }

        addConnectorElement(field, connector1, "connector1");
        addConnectorElement(field, connector2, "connector2");
        addTextFieldForDefaultValue(field);
    }

    private void addConnectorElement(FieldMapping field, UIConnectorConfig config, String leftRightField) {
        if (field.getField() == FIELD.REMOTE_ID && remoteIdFieldNotSupported(config)) {
            String idFieldDisplayValue = GTaskDescriptor.getDisplayValue(FIELD.ID);
            createMappingForSingleValue(idFieldDisplayValue);
        } else {
            addConnectorField(config.getAvailableFields(), field, leftRightField);
        }
    }

    private boolean remoteIdFieldNotSupported(UIConnectorConfig config) {
        return !config.getAvailableFields().isFieldSupported(FIELD.REMOTE_ID);
    }

    private void addCheckbox(FieldMapping field) {
        CheckBox checkbox = new CheckBox();
        final MethodProperty<Boolean> selected = new MethodProperty<Boolean>(field, "selected");
        checkbox.setPropertyDataSource(selected);
        gridLayout.addComponent(checkbox);
        gridLayout.setComponentAlignment(checkbox, Alignment.MIDDLE_CENTER);
    }

    private void addTextFieldForDefaultValue(FieldMapping mapping) {
        TextField field = new TextField();
        final MethodProperty<String> methodProperty = new MethodProperty<String>(mapping, "defaultValue");
        field.setPropertyDataSource(methodProperty);
        gridLayout.addComponent(field);
        gridLayout.setComponentAlignment(field, Alignment.MIDDLE_CENTER);
    }

    private void addHelp(String helpForField) {
        Embedded helpIcon = new Embedded(null, HELP_ICON_RESOURCE);
        helpIcon.setDescription(helpForField);
        gridLayout.addComponent(helpIcon);
        gridLayout.setComponentAlignment(helpIcon, Alignment.MIDDLE_CENTER);
    }

    private void addEmptyCell() {
        Label emptyLabel = new Label(" ");
        gridLayout.addComponent(emptyLabel);
        gridLayout.setComponentAlignment(emptyLabel, Alignment.MIDDLE_LEFT);
    }

    private void addConnectorField(AvailableFields connectorFields, FieldMapping fieldMapping, String leftRightField) {
        String[] allowedValues = connectorFields.getAllowedValues(fieldMapping.getField());
        BeanItemContainer<String> container = new BeanItemContainer<String>(String.class);
        final MethodProperty<String> mappedTo = new MethodProperty<String>(fieldMapping, leftRightField);

        if (connectorFields.isFieldSupported(fieldMapping.getField())) {
            if (allowedValues.length > 1) {
                container.addAll(Arrays.asList(allowedValues));
                ComboBox combo = new ComboBox(null, container);
                combo.setPropertyDataSource(mappedTo);
                combo.setWidth(160, PIXELS);
                gridLayout.addComponent(combo);
                gridLayout.setComponentAlignment(combo, Alignment.MIDDLE_LEFT);
                Object currentValue = mappedTo.getValue();
                combo.select(currentValue);
            } else if (allowedValues.length == 1) {
                createMappingForSingleValue(allowedValues[0]);
            } else {
                final String displayValue = GTaskDescriptor
                        .getDisplayValue(fieldMapping.getField());
                createMappingForSingleValue(displayValue);
            }
        } else {
            addEmptyCell();
        }
    }

    private void createMappingForSingleValue(String displayValue) {
        Label label = new Label(displayValue);
        gridLayout.addComponent(label);
        gridLayout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
    }

    String getHelpForField(FieldMapping field) {
        String elementId = field.getField().toString();
        return HELP_MESSAGES.getNoDefault(elementId);
    }

    @Override
    public void validate() throws BadConfigException {
        MappingsValidator.validate(mappings);
    }
    
    public Component getUI() {
        return ui;
    }
}
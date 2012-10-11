package com.taskadapter.webui;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.NewMappings;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.web.Messages;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.configeditor.Validatable;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

import java.util.Arrays;

public class OnePageMappingPanel extends Panel implements Validatable {
    public static final int COLUMN_DESCRIPTION = 0;
    public static final int COLUMN_LEFT_CONNECTOR = 1;
    public static final int COLUMN_RIGHT_CONNECTOR = 2;
    private Resource helpIconResource = new ThemeResource("../runo/icons/16/help.png");

    private static final String PANEL_TITLE = "Task fields";

    private static final int COLUMNS_NUMBER = 3;

    private GridLayout gridLayout;

    private String connector1Label;
    private AvailableFields connector1Fields;
    private String connector2Label;
    private AvailableFields connector2Fields;
    private NewMappings mappings;

    public OnePageMappingPanel(String connector1Label, AvailableFields connector1Fields,
                               String connector2Label, AvailableFields connector2Fields,
                               NewMappings mappings) {
        super("Task fields mapping");
        this.connector1Label = connector1Label;
        this.connector1Fields = connector1Fields;
        this.connector2Label = connector2Label;
        this.connector2Fields = connector2Fields;
        this.mappings = mappings;

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
        addComponent(gridLayout);
    }

    private void addTableHeaders() {
        Label label2 = new Label("Field");
        label2.addStyleName("fieldsTitle");
        label2.setWidth("30px");
        gridLayout.addComponent(label2, COLUMN_DESCRIPTION, 0);

        Label label1 = new Label(connector1Label);
        label1.addStyleName("fieldsTitle");
        label1.setWidth("135px");
        gridLayout.addComponent(label1, COLUMN_LEFT_CONNECTOR, 0);


        Label label3 = new Label(connector2Label);
        label3.addStyleName("fieldsTitle");
        gridLayout.addComponent(label3, COLUMN_RIGHT_CONNECTOR, 0);

        gridLayout.addComponent(new Label("<hr>", Label.CONTENT_XHTML), 0, 1, COLUMNS_NUMBER - 1, 1);
    }

    private void addSupportedFields() {
        for (GTaskDescriptor.FIELD field : GTaskDescriptor.FIELD.values()) {
            addField(field);
        }
    }

    private void addField(GTaskDescriptor.FIELD field) {
        addCheckbox(field);
//        addEmptyCell();
        addConnectorField(field, connector1Fields, mappings.getMapping(field), "connector1");
        addConnectorField(field, connector2Fields, mappings.getMapping(field), "connector2");
    }

    private CheckBox addCheckbox(GTaskDescriptor.FIELD field) {
        CheckBox checkbox = new CheckBox(GTaskDescriptor.getDisplayValue(field));
        FieldMapping fieldMapping = mappings.getMapping(field);
        if (fieldMapping == null) {
            fieldMapping = new FieldMapping(field, "", "", true);
            mappings.put(fieldMapping);
        }
        final MethodProperty<Boolean> selected = new MethodProperty<Boolean>(fieldMapping, "selected");
        checkbox.setPropertyDataSource(selected);

        String helpForField = getHelpForField(field);
        if (helpForField != null) {
            HorizontalLayout layout = addHelpTipToCheckbox(checkbox,
                    helpForField);
            gridLayout.addComponent(layout);
            gridLayout.setComponentAlignment(layout, Alignment.MIDDLE_LEFT);
        } else {
            gridLayout.addComponent(checkbox);
            gridLayout.setComponentAlignment(checkbox, Alignment.MIDDLE_LEFT);
        }

        return checkbox;
    }

    private void addEmptyCell() {
        Label emptyLabel = new Label(" ");
        gridLayout.addComponent(emptyLabel);
        gridLayout.setComponentAlignment(emptyLabel, Alignment.MIDDLE_LEFT);
    }

    private void addConnectorField(GTaskDescriptor.FIELD field, AvailableFields connectorFields, FieldMapping fieldMapping, String leftRightField) {
        String[] allowedValues = connectorFields.getAllowedValues(field);
        BeanItemContainer<String> container = new BeanItemContainer<String>(String.class);
        final MethodProperty<String> mappedTo = new MethodProperty<String>(fieldMapping, leftRightField);

        if (connectorFields.isFieldSupported(field)) {
            if (allowedValues.length > 1) {
                container.addAll(Arrays.asList(allowedValues));
                ComboBox combo = new ComboBox(null, container);
                combo.setPropertyDataSource(mappedTo);
                combo.setWidth("160px");
                gridLayout.addComponent(combo);
                gridLayout.setComponentAlignment(combo, Alignment.MIDDLE_LEFT);
                Object currentValue = mappedTo.getValue();
                combo.select(currentValue);
            } else if (allowedValues.length == 1) {
                createMappingForSingleValue(allowedValues[0], fieldMapping, leftRightField);
            } else {
                String displayValue = GTaskDescriptor.getDisplayValue(field);
                createMappingForSingleValue(displayValue, fieldMapping, leftRightField);
            }
        } else {
            addEmptyCell();
        }
    }

    private void createMappingForSingleValue(String displayValue, FieldMapping mapping, String leftRight) {
/*
        // TODO !!! bug? why did we set this?
        if (leftRight.equals("left")) {
            mapping.setLeft(displayValue);
        } else if (leftRight.equals("right")) {
            mapping.setRight(displayValue);
        } else {
            throw new IllegalArgumentException();
        }
*/
        Label label = new Label(displayValue);
        gridLayout.addComponent(label);
        gridLayout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
    }

    private String getHelpForField(GTaskDescriptor.FIELD field) {
        return Messages.getMessageDefaultLocale(field.toString());
    }

    private HorizontalLayout addHelpTipToCheckbox(CheckBox checkbox,
                                                  String helpForField) {
        Embedded helpIcon = new Embedded(null, helpIconResource);
        helpIcon.setDescription(helpForField);
        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(checkbox);
        layout.addComponent(helpIcon);
        return layout;
    }

    @Override
    public void validate() throws ValidationException {
/*
        for (GTaskDescriptor.FIELD f : availableFields.getSupportedFields()) {
            if (mappings.isFieldSelected(f) && mappings.getMappedTo(f) == null) {
                throw new ValidationException(getRequiredFieldErrorMessage(f));
            }
        }
*/
    }

    private String getRequiredFieldErrorMessage(GTaskDescriptor.FIELD f) {
        return "Field \"" + GTaskDescriptor.getDisplayValue(f)
                + "\" is selected for export."
                + "\nPlease set the *destination* field or constraint in "
                + PANEL_TITLE + " section.";
    }

    /**
     * Checks, if there are any changes to perform.
     *
     * @return <code>true</code> iff there are changes since panel creation.
     */
 /*   public boolean hasChanges() {
        return originalMappings.equals(mappings);
    }*/
}
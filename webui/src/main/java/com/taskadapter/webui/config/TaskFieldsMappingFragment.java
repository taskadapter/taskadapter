package com.taskadapter.webui.config;

import com.taskadapter.connector.Field;
import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.model.GTaskDescriptor;
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
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.util.List;
import java.util.stream.Collectors;

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
    private List<EditableFieldMapping> editablePojoMappings;

    public TaskFieldsMappingFragment(Messages messages, UIConnectorConfig connector1,
                                     UIConnectorConfig connector2, scala.collection.Seq<FieldMapping> mappings) {
        this.messages = messages;
        this.connector1 = connector1;
        this.connector2 = connector2;

        ui = new Panel(messages.get("editConfig.mappings.caption"));

        addFields(mappings);
    }

    private void addFields(Seq<FieldMapping> mappings) {
        createGridLayout();
        addTableHeaders();
        addSupportedFields(mappings);
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

    /**
     * Add all rows to mappings table
     */
    private void addSupportedFields(Seq<FieldMapping> mappings) {
        editablePojoMappings = JavaConverters.asJavaCollection(mappings)
                .stream()
                .map(ro ->
                        new EditableFieldMapping(ro.fieldInConnector1().name(), ro.fieldInConnector1().typeName(),
                                ro.fieldInConnector2().name(), ro.fieldInConnector2().typeName(),
                                ro.selected(), ro.defaultValue()))
                .collect(Collectors.toList());
        editablePojoMappings.forEach(e -> addField(e));
    }

    /**
     * Add a row to mapping table:
     * selected, tooltip, connector 1 field name, connector 2 field name, default value.
     */
    private void addField(EditableFieldMapping field) {
        addCheckbox(field);
        // TODO TA3 help is per connector field, not for the whole row now.
        String helpForField = null; //getHelpForField(field);
        if (helpForField != null) {
            addHelp(helpForField);
        } else {
            addEmptyCell();
        }

        addConnectorElement(field, connector1, "fieldInConnector1");
        addConnectorElement(field, connector2, "fieldInConnector2");
        addTextFieldForDefaultValue(field);
    }

    private void addConnectorElement(EditableFieldMapping field, UIConnectorConfig config, String leftRightField) {
        addConnectorField(config.getAvailableFields(), field, leftRightField);
    }

    private void addCheckbox(EditableFieldMapping field) {
        CheckBox checkbox = new CheckBox();
        final MethodProperty<Boolean> selected = new MethodProperty<>(field, "selected");
        checkbox.setPropertyDataSource(selected);
        gridLayout.addComponent(checkbox);
        gridLayout.setComponentAlignment(checkbox, Alignment.MIDDLE_CENTER);
    }

    private void addTextFieldForDefaultValue(EditableFieldMapping mapping) {
        TextField field = new TextField();
        final MethodProperty<String> methodProperty = new MethodProperty<>(mapping, "defaultValue");
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

    private void addConnectorField(List<Field> connectorFields, EditableFieldMapping fieldMapping, String classFieldName) {
        BeanItemContainer<String> container = new BeanItemContainer<>(String.class);
        final MethodProperty<String> mappedTo = new MethodProperty<>(fieldMapping, classFieldName);

        List<String> fieldNames = connectorFields.stream()
                .map(field -> field.name()).collect(Collectors.toList());
        container.addAll(fieldNames);
        ComboBox combo = new ComboBox(null, container);
        combo.setPropertyDataSource(mappedTo);
        combo.setWidth(160, PIXELS);
        gridLayout.addComponent(combo);
        gridLayout.setComponentAlignment(combo, Alignment.MIDDLE_LEFT);
        String currentFieldName = classFieldName.equals("fieldInConnector1") ?
                fieldMapping.fieldInConnector1() :
                fieldMapping.fieldInConnector2();
        combo.select(currentFieldName);
    }

    @Override
    public void validate() throws BadConfigException {
        MappingsValidator.validate(editablePojoMappings);
    }

    public Component getUI() {
        return ui;
    }

    public Iterable<FieldMapping> getElements() {
        return editablePojoMappings.stream()
                .map(e -> new FieldMapping(new Field(e.fieldTypeInConnector1(), e.fieldInConnector1()),
                        new Field(e.fieldTypeInConnector2(), e.fieldInConnector2()),
                        e.selected(), e.defaultValue()))
                .collect(Collectors.toList());
    }
}
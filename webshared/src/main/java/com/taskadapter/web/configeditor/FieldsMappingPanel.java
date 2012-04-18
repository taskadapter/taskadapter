package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.AvailableFieldsProvider;
import com.taskadapter.connector.definition.Mapping;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.model.GTaskDescriptor;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Alexey Skorokhodov
 */
public class FieldsMappingPanel extends GridLayout implements Validatable {
    private static final String PANEL_TITLE = "Task fields";

    private static final String COLUMN1_HEADER = "Task Adapter field";

    private static final String COLUMN2_HEADER = "Connector field or constraint";

    private Map<GTaskDescriptor.FIELD, Mapping> fieldsMapping;

    private Map<GTaskDescriptor.FIELD, CheckBox> fieldToButtonMap = new HashMap<GTaskDescriptor.FIELD, CheckBox>();
    private Map<GTaskDescriptor.FIELD, ComboBox> fieldToValueMap = new HashMap<GTaskDescriptor.FIELD, ComboBox>();

    private final AvailableFieldsProvider availableFieldsProvider;
    private static final int COLUMNS_NUMBER = 2;

    /**
     * Config Editors should NOT create this object directly, use ConfigEditor.addFieldsMappingPanel() method instead.
     *
     * @see ConfigEditor#addFieldsMappingPanel(com.taskadapter.connector.definition.AvailableFieldsProvider, java.util.Map)
     */
    public FieldsMappingPanel(AvailableFieldsProvider availableFieldsProvider,
                              Map<GTaskDescriptor.FIELD, Mapping> fieldsMapping) {
        addStyleName("fields_mapping_panel");
        this.availableFieldsProvider = availableFieldsProvider;
        this.fieldsMapping = fieldsMapping;
        setDescription("Select fields to export when SAVING data to this connector");
        setColumns(COLUMNS_NUMBER);
        addFields();
    }

    private void addFields() {
        addComponent(new Label(COLUMN1_HEADER));
        addComponent(new Label(COLUMN2_HEADER));

        for (GTaskDescriptor.FIELD f : availableFieldsProvider.getSupportedFields()) {
            CheckBox checkbox = new CheckBox(GTaskDescriptor.getDisplayValue(f));
            addComponent(checkbox);
            fieldToButtonMap.put(f, checkbox);

            Mapping mapping = fieldsMapping.get(f);
            if (mapping == null) {
                // means this config does not have a mapping for this field, which
                // availableFieldsProvider reported as "supported": probably OLD config
                continue;
            }

            checkbox.setValue(mapping.isSelected());
            String[] allowedValues = availableFieldsProvider.getAllowedValues(f);
            BeanItemContainer<String> container = new BeanItemContainer<String>(String.class);
            if (allowedValues.length > 1) {
                container.addAll(Arrays.asList(allowedValues));
                ComboBox combo = new ComboBox("", container);
                fieldToValueMap.put(f, combo);
//                combo.setItems(allowedValues);
                addComponent(combo);
//                selectItem(combo, mapping.getCurrentValue());
                combo.select(mapping.getCurrentValue());
            } else if (allowedValues.length == 1) {
                addComponent(new Label(allowedValues[0]));
            } else {
                // field not supported by this connector
                checkbox.setEnabled(false);
                checkbox.setValue(false);
            }
        }
    }

    // TODO use this!
    public Map<GTaskDescriptor.FIELD, Mapping> getResult() {
        Map<GTaskDescriptor.FIELD, Mapping> map = new TreeMap<GTaskDescriptor.FIELD, Mapping>();
        for (GTaskDescriptor.FIELD f : availableFieldsProvider.getSupportedFields()) {
            boolean selected = fieldToButtonMap.get(f).booleanValue();
            String value = null;
            ComboBox combo = fieldToValueMap.get(f);
            if (combo != null) {
                value = (String) combo.getValue();
            }
            Mapping mapping = new Mapping(selected, value);
            map.put(f, mapping);
        }
        return map;
    }

    @Override
    public void validate() throws ValidationException {
        // TODO copied from getResult just to compile the code. REFACTOR THIS!!!
        for (GTaskDescriptor.FIELD f : availableFieldsProvider.getSupportedFields()) {
            boolean selected = fieldToButtonMap.get(f).booleanValue();
            ComboBox combo = fieldToValueMap.get(f);
            if (combo != null) {
                // the field can be mapped to one of SEVERAL options, need to find the combobox
                String selectedOption = (String) combo.getValue();
                if (selected && (selectedOption == null)) {
                    throw new ValidationException(getRequiredFieldErrorMessage(f));
                }
            }
        }
    }

    private String getRequiredFieldErrorMessage(GTaskDescriptor.FIELD f) {
        return "Field \"" +
                GTaskDescriptor.getDisplayValue(f) +
                "\" is selected for export." +
                "\nPlease set the *destination* field or constraint in " + PANEL_TITLE + " section.";
    }

}

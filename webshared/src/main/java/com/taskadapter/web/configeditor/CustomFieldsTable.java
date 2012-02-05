package com.taskadapter.web.configeditor;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Igor Laishen
 */
public class CustomFieldsTable extends CustomComponent {
    private static final String LABEL = "Custom fields:";
    private static final String ADD_NEW_BUTTON = "+";
    private static final String ADD_NEW_BUTTON_DESCRIPTION = "Add new custom field";
    private static final String REMOVE_BUTTON = "-";
    private static final String REMOVE_BUTTON_DESCRIPTION = "Remove selected custom field";

    private static final String TABLE_HEADER_ID = "Field ID";
    private static final String TABLE_HEADER_VALUE = "Value";

    private static final String CELL_DEFAULT_VALUE = "...";

    private Panel panel = new Panel();
    private GridLayout mainLayout = new GridLayout(2, 1);
    private final Table table = new Table();


    public CustomFieldsTable() {
        this(new ArrayList<CustomField>());
    }

    public CustomFieldsTable(List<CustomField> customFields) {
        buildUI();
        setCustomFields(customFields);
        setCompositionRoot(panel);
    }

    public List<CustomField> getCustomFields() {
        Collection itemIds = table.getItemIds();
        List<CustomField> newCustomFields = new ArrayList<CustomField>(itemIds.size());

        if (!itemIds.isEmpty()) {
            for (Object itemId : itemIds) {
                Item item = table.getItem(itemId);

                String id = item.getItemProperty(TABLE_HEADER_ID).getValue().toString();
                String value = item.getItemProperty(TABLE_HEADER_VALUE).getValue().toString();

                if (!"".equals(id) && !CELL_DEFAULT_VALUE.equals(id)) {
                    newCustomFields.add(new CustomField(id, value));
                }
            }
        }

        return newCustomFields;
    }

    public void setCustomFields(List<CustomField> customFields) {
        table.removeAllItems();
        for (CustomField customField : customFields) {
            table.addItem(new Object[]{
                    customField.getId(),
                    customField.getValue()
            }, null);
        }
    }

    private void buildUI() {
        panel.setCaption(LABEL);

        table.setSelectable(true);
        table.setMultiSelect(false);
        table.setNullSelectionAllowed(false);
        table.setImmediate(true);
        table.setSortDisabled(true);

        table.setPageLength(5);
        table.setWidth("320px");
        table.setHeight("110px");

        table.addContainerProperty(TABLE_HEADER_ID, String.class, CELL_DEFAULT_VALUE);
        table.setColumnWidth(TABLE_HEADER_ID, 90);
        table.addContainerProperty(TABLE_HEADER_VALUE, String.class, CELL_DEFAULT_VALUE);

        table.addListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.getButton() == ItemClickEvent.BUTTON_LEFT && event.isDoubleClick()) {
                    final Object cellItemId = event.getItemId();
                    final Object cellPropertyId = event.getPropertyId();
                    final String cellValue = event.getItem().getItemProperty(event.getPropertyId()).getValue().toString();

                    table.setTableFieldFactory(new TableFieldFactory() {
                        @Override
                        public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
                            if (propertyId.toString().equalsIgnoreCase(cellPropertyId.toString())
                                    && itemId.toString().equalsIgnoreCase(cellItemId.toString())) {

                                TextField textField = new TextField(null, cellValue);
                                textField.setImmediate(true);
                                textField.setWidth("80%");
                                textField.setSelectionRange(0, cellValue.length());
                                textField.setTabIndex(0);
                                textField.addListener(new FieldEvents.BlurListener() {
                                    @Override
                                    public void blur(FieldEvents.BlurEvent event) {
                                        table.setEditable(false);
                                    }
                                });

                                return textField;
                            }
                            return null; //cell will not be editable
                        }
                    });
                    table.setEditable(true);
                }
                table.select(event.getItemId());  //still cannot remove bug with not correct focusing of selected row
            }
        });


        Button addNewBtn = new Button(ADD_NEW_BUTTON);
        addNewBtn.setDescription(ADD_NEW_BUTTON_DESCRIPTION);
        addNewBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                table.addItem(new Object[]{CELL_DEFAULT_VALUE, CELL_DEFAULT_VALUE}, null);
                table.refreshRowCache();
            }
        });

        Button removeBtn = new Button(REMOVE_BUTTON);
        removeBtn.setDescription(REMOVE_BUTTON_DESCRIPTION);
        removeBtn.setWidth("36px");
        removeBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Object itemId = table.getValue();
                if (itemId != null) {
                    table.removeItem(itemId);
                }
            }
        });

        VerticalLayout buttonsVL = new VerticalLayout();
        buttonsVL.addComponent(addNewBtn);
        buttonsVL.addComponent(removeBtn);

        mainLayout.addComponent(table);
        mainLayout.setComponentAlignment(table, Alignment.TOP_CENTER);

        mainLayout.addComponent(buttonsVL);
        mainLayout.setComponentAlignment(buttonsVL, Alignment.TOP_LEFT);

        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        mainLayout.setStyleName("bordered_panel");

        panel.setContent(mainLayout);
    }
}

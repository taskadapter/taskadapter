package com.taskadapter.web.configeditor;

import com.taskadapter.web.configeditor.map.MapEditorModel;
import com.taskadapter.vaadin14shim.HorizontalLayout;
import com.taskadapter.vaadin14shim.VerticalLayout;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Runo;

import java.util.Map;

// TODO TA3 custom fields: not needed anymore?
public class CustomFieldsTablePanel extends Panel {
    private static final String LABEL = "Custom fields:";
    private static final String ADD_NEW_BUTTON = "Add";
    private static final String ADD_NEW_BUTTON_DESCRIPTION = "Add new custom field";
    private static final String REMOVE_BUTTON = "Remove";
    private static final String REMOVE_BUTTON_DESCRIPTION = "Remove selected custom field";

    private static final String TABLE_HEADER_ID = "Field ID";
    private static final String TABLE_HEADER_VALUE = "Value";

    private static final String CELL_DEFAULT_VALUE = "...";

    private VerticalLayout  mainLayout = new VerticalLayout();
    private final Table table = new Table();
    private static final String TABLE_HEIGHT = "113px";

    /**
     * Used model.
     */
    private final MapEditorModel model;

    public CustomFieldsTablePanel(Map<String, String> map) {
    	this.model = new MapEditorModel(map);
        buildUI();
    }

    private void buildUI() {
        setCaption(LABEL);
        table.setStyleName(Runo.TABLE_SMALL);
        table.setSelectable(true);
        table.setMultiSelect(false);
        table.setNullSelectionAllowed(false);
        table.setImmediate(true);
        table.setSortEnabled(false);
        table.setPageLength(5);
        table.setWidth("100%");
        table.setHeight(TABLE_HEIGHT);

        table.addContainerProperty(TABLE_HEADER_ID, String.class, CELL_DEFAULT_VALUE);
        table.setColumnWidth(TABLE_HEADER_ID, 90);
        table.addContainerProperty(TABLE_HEADER_VALUE, String.class, CELL_DEFAULT_VALUE);
        
        table.setContainerDataSource(model);

        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.getButton() == MouseEventDetails.MouseButton.LEFT && event.isDoubleClick()) {
                    final Object cellItemId = event.getItemId();
                    final Object cellPropertyId = event.getPropertyId();
                    Property property = event.getItem().getItemProperty(event.getPropertyId());
                    final String cellValue = (String) property.getValue();

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
                                textField.addBlurListener(new FieldEvents.BlurListener() {
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


        Button addNewBtn = new Button(ADD_NEW_BUTTON,
                event -> model.append(CELL_DEFAULT_VALUE, CELL_DEFAULT_VALUE));
        addNewBtn.setDescription(ADD_NEW_BUTTON_DESCRIPTION);

        Button removeBtn = new Button(REMOVE_BUTTON, event -> {
            Object itemId = table.getValue();
            if (itemId != null) {
                model.removeItem(itemId);
            }
        });
        removeBtn.setDescription(REMOVE_BUTTON_DESCRIPTION);

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.add(addNewBtn);
        buttonsLayout.add(removeBtn);

        mainLayout.add(table);
        mainLayout.setComponentAlignment(table, Alignment.TOP_CENTER);

        mainLayout.add(buttonsLayout);
        mainLayout.setComponentAlignment(buttonsLayout, Alignment.TOP_LEFT);

        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        setContent(mainLayout);
    }
}

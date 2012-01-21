package com.taskadapter.connector.jira;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Alexey Skorokhodov
 */
public class CustomFieldsPanel extends CustomComponent {

    private Collection<CustomField> customFields = new ArrayList<CustomField>();

    public CustomFieldsPanel() {
        buildUI();
    }

    private void buildUI() {
        Label label = new Label("Custom fields: NOT IMPLEMENTED YET");
        setCompositionRoot(label);
//        // see Jira bug https://jira.atlassian.com/browse/JRA-6857
//        label.setToolTipText("Set these custom fields for all NEW or UPDATED issues.\n" +
//                "The fields must already exist in your Jira installation.\n" +
//                "Unfortunately, Jira requires Custom Field *IDs* and not *names*.");
//
//        final GridData gridData = new GridData();
//        gridData.horizontalSpan = 1;
//
//        Comosite trackerTableContainer = new Comosite(container, SWT.NONE);
//        trackerTableContainer.setLayoutData(gridData);
//
//        customFieldsTable = new Table(trackerTableContainer, SWT.BORDER);
//        customFieldsTable.setHeaderVisible(true);
//        customFieldsTable.setBounds(0, 0, 300, 100);
//
//        TableColumn idColumn = new TableColumn(customFieldsTable, SWT.LEFT, 0);
//        idColumn.setText("Field ID");
//        idColumn.setWidth(80);
//        TableColumn nameColumn = new TableColumn(customFieldsTable, SWT.LEFT, 1);
//        nameColumn.setText("Value");
//        nameColumn.setWidth(120);
//        idEditor = new TableEditor(customFieldsTable);
//        nameEditor = new TableEditor(customFieldsTable);
//
//        customFieldsTable.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent e) {
//
//                Control oldEditor = idEditor.getEditor();
//                if (oldEditor != null)
//                    oldEditor.dispose();
//                oldEditor = nameEditor.getEditor();
//                if (oldEditor != null)
//                    oldEditor.dispose();
//
//                // Identify the selected row
//                TableItem tableItem = (TableItem) e.item;
//                if (tableItem == null)
//                    return;
//
//                // The editor must have the same size as the cell and must
//                // not be any smaller than 50 pixels.
//                idEditor.horizontalAlignment = SWT.LEFT;
//                idEditor.grabHorizontal = true;
//                idEditor.minimumWidth = 50;
//
//                Text idText = new Text(customFieldsTable, SWT.NONE);
//                idText.setText(tableItem.getText(0));
//                idEditor.setEditor(idText, tableItem, 0);
//
//                idText.addModifyListener(new ModifyListener() {
//                    public void modifyText(ModifyEvent me) {
//                        Text text = (Text) idEditor.getEditor();
//                        idEditor.getItem().setText(0, text.getText());
//                    }
//                });
//
//                // The editor must have the same size as the cell and must
//                // not be any smaller than 50 pixels.
//                nameEditor.horizontalAlignment = SWT.LEFT;
//                nameEditor.grabHorizontal = true;
//                nameEditor.minimumWidth = 50;
//
//                Text nameText = new Text(customFieldsTable, SWT.NONE);
//                nameText.setText(tableItem.getText(1));
//                nameEditor.setEditor(nameText, tableItem, 1);
//
//                nameText.addModifyListener(new ModifyListener() {
//                    public void modifyText(ModifyEvent me) {
//                        Text text = (Text) nameEditor.getEditor();
//                        nameEditor.getItem().setText(1, text.getText());
//                    }
//                });
//            }
//        });
//
//        Button addFieldButton = new Button(container, SWT.PUSH);
//        addFieldButton.setText("+");
//        addFieldButton
//                .setToolTipText("Add a new custom field to the list.");
//        addFieldButton.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(Selectionnt e) {
//                addTrackerOnTable("0", ".....");
//            }
//        });
//
//        Button removeFieldButton = new Button(container, SWT.PUSH);
//        removeFieldButton.setText("X");
//        removeFieldButton
//                .setToolTipText("Remove the selected custom fields from the list.");
//        removeFieldButton.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent e) {
//                removeTrackerFromTable();
//            }
//        });
    }

    public void addTrackerOnTable(String key, String s) {

    }

    public Collection<CustomField> getCustomFields() {
        return customFields;
    }
}

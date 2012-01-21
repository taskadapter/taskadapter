package com.taskadapter.web.configeditor;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.Descriptor.Feature;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.model.NamedKeyedObjectImpl;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import java.util.*;

/**
 * @author Alexey Skorokhodov
 */
public class PriorityPanel extends VerticalLayout implements Validatable {

    public static final String TEXT = "Priority Name";
    public static final String VALUE = "Task Adapter Priority Value";
    private final ConfigEditor configEditor;
    private final Descriptor descriptor;

    private BeanContainer data = new BeanContainer<String, Priority>(Priority.class);

    private Table prioritiesTable;

    /**
     * Config Editors should NOT create this object directly, use ConfigEditor.addPriorityPanel() method instead.
     *
     * @see ConfigEditor#addPriorityPanel(ConfigEditor, com.taskadapter.connector.definition.Descriptor, Priorities priorities)
     */
    PriorityPanel(ConfigEditor editor, Descriptor descriptor) {
        this.configEditor = editor;
        this.descriptor = descriptor;
        buildUI();
    }

    private void buildUI() {
        addStyleName("bordered_panel");

        Collection<Feature> features = descriptor.getSupportedFeatures();

		prioritiesTable = new Table("Priorities", data);
        data.setBeanIdProperty("text");
        prioritiesTable.addStyleName("priorities_table");
        prioritiesTable.setEditable(true);
        prioritiesTable.setColumnHeader("text", TEXT);
        prioritiesTable.setColumnHeader("value", VALUE);
        prioritiesTable.setVisibleColumns(new Object[]{"text", "value"});

//		prioritiesTable.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelecionEvent e) {
//
//				clearEditor();
//
//				// Identify the selected row
//				TableItem tableItem = (TableItem) e.item;
//				if (tableItem == null)
//					return;
//
//				// The editor must have the same size as the cell and must
//				// not be any smaller than 50 pixels.
//				prioritiesEditor.horizontalAlignment = SWT.LEFT;
//				prioritiesEditor.grabHorizontal = true;
//				prioritiesEditor.minimumWidth = 50;
//
//				ext mspText = new ext(prioritiesTable, SWT.NONE);
//				mspText.setText(tableItem.getText(1));
//				prioritiesEditor.setEditor(mspText, tableItem, 1);
//				mspText.setFocus();
//				mspText.selectAll();
//
//				mspText.addVerifyListener(new NumberVerifier());
//
//				mspText.addModifyListener(new ModifyListener() {
//					public void modifyText(ModifyEvent me) {
//						ext text = (Txt) prioritiesEditor.getEditor();
//						prioritiesEditor.getItem().setText(1, text.getText());
//					}
//				});
//			}
//		});
        addComponent(prioritiesTable);

        Button reloadButton = new Button("Reload");
        reloadButton.setDescription("Reload priority list.");
        reloadButton.setEnabled(features.contains(Feature.LOAD_PRIORITIES));
        reloadButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    reloadPriorityList();
                } catch (Exception e) {
                    // TODO handle the exception
                    e.printStackTrace();
                }
            }
        });
        addComponent(reloadButton);
    }

    public Priorities getPriorities() {
           Map<String, Integer> priorities = new HashMap<String, Integer>();
//           for (TableItem tableItem : prioritiesTable.getItems()) {
//               String trackerText = tableItem.getText(0);
//               Integer mspValue = Integer.parseInt(tableItem.getText(1));
//               priorities.put(trackerText, mspValue);
//           }

           return null;
       }
/*
       private void addPriorityOnTable(String id, String name) {
           TableItem tableItem = new TableItem(prioritiesTable, SWT.NONE);
           tableItem.setText(new String[] { id, name });
       }
*/
       private void reloadPriorityList() throws Exception {
           LookupOperation loadPrioritiesOperation = new LoadPrioritiesOperation(configEditor, descriptor.getPluginFactory());
           @SuppressWarnings("unchecked")
           List<NamedKeyedObjectImpl> list = (List<NamedKeyedObjectImpl>) loadPrioritiesOperation
                   .run();

           Priorities defaultPriorities = descriptor.createDefaultConfig().getPriorities();
           setPriorities(defaultPriorities);

           System.out.println("reloadPriorityList: not implemented");
       }

    public void setPriorities(Priorities items) {
        data.removeAllItems();
        for (final String priorityName : items.getAllNames()) {
            data.addBean(new Priority(priorityName, items.getPriorityByText(priorityName)));
        }
        prioritiesTable.setPageLength(prioritiesTable.size() + 1);
    }

    @Override
    public void validate() throws ValidationException {
        Integer mspValue;

        Set<Integer> set = new HashSet<Integer>();
        int i = 0;
//        for (TableItem tableItem : prioritiesTable.getItems()) {
//            i++;
//            mspValue = Integer.parseInt(tableItem.getText(1));
//            set.add(mspValue);
//        }

        if (i != set.size()) {
            throw new ValidationException("TaskAdapter priorities duplication found. Please make all priority values unique in the table.");
        }
    }

    /**
     * Make sure there are only digits in the text field.
     */
//	private final class NumberVerifier implements VerifyListener {
//		public void verifyText(VerifyEvent e) {
//		      e.doit = "0123456789".indexOf(e.text) >= 0;
//		  }
//	}

    public class Priority {
        String text;
        int value;

        private Priority(String text, int value) {
            this.text = text;
            this.value = value;
        }

        public String getText() {
            return text;
        }

        public int getValue() {
            return value;
        }
    }
}
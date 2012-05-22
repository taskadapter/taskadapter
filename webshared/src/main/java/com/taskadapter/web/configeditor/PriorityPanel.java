package com.taskadapter.web.configeditor;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.Descriptor.Feature;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.model.NamedKeyedObjectImpl;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

import java.util.*;

/**
 * @author Alexey Skorokhodov
 */
public class PriorityPanel extends Panel implements Validatable {

    private static final String NAME_HEADER = "Name";
    private static final String VALUE_HEADER = "Task Adapter Priority Value";

    private final ConfigEditor configEditor;
    private final Descriptor descriptor;

    private BeanContainer data = new BeanContainer<String, Priority>(Priority.class);

    private Table prioritiesTable;

    /**
     * Config Editors should NOT create this object directly, use ConfigEditor.addPriorityPanel() method instead.
     *
     * @param editor     ConfigEditor
     * @param descriptor Descriptor
     * @see ConfigEditor#addPriorityPanel(ConfigEditor, com.taskadapter.connector.definition.Descriptor, Priorities priorities)
     */
    public PriorityPanel(ConfigEditor editor, Descriptor descriptor) {
        super("Priorities");
        this.configEditor = editor;
        this.descriptor = descriptor;
        buildUI();
    }

    private void buildUI() {
        setWidth(DefaultPanel.NARROW_PANEL_WIDTH);

        Collection<Feature> features = descriptor.getSupportedFeatures();

        data.setBeanIdProperty("text");

        prioritiesTable = new Table("Priorities");
        prioritiesTable.setContainerDataSource(data);
        prioritiesTable.setStyleName(Runo.TABLE_SMALL);
        prioritiesTable.addStyleName("priorities-table");
        prioritiesTable.setEditable(true);

        prioritiesTable.setColumnHeader(Priority.TEXT, NAME_HEADER);
        prioritiesTable.setColumnHeader(Priority.VALUE, VALUE_HEADER);
        prioritiesTable.setVisibleColumns(new Object[]{Priority.TEXT, Priority.VALUE});

        //prioritiesTable.setColumnWidth(Priority.TEXT, );
        prioritiesTable.setWidth("100%");

/*        prioritiesTable.setTableFieldFactory(new DefaultFieldFactory() {
            @Override
            public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
                Field field = super.createField(container, itemId, propertyId, uiContext);
                if ("value".equals(propertyId)) {
                    field.addValidator(new IntegerValidator("error"));
                }
                return field;
            }
        });*/

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
                    getWindow().showNotification("Error!", e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });
        addComponent(reloadButton);
    }

    public Priorities getPriorities() {
        Map<String, Integer> priorities = new HashMap<String, Integer>();
        for (Object o : data.getItemIds()) {
            String sid = (String) o;
            Item item = data.getItem(sid);

            priorities.put((String) item.getItemProperty("text").getValue(), (Integer) item.getItemProperty("value").getValue());
        }

        return new Priorities(priorities);
    }

    private void reloadPriorityList() throws Exception {
        LookupOperation loadPrioritiesOperation = new LoadPrioritiesOperation(configEditor, descriptor.getPluginFactory());
        @SuppressWarnings("unchecked")
        List<NamedKeyedObjectImpl> list = (List<NamedKeyedObjectImpl>) loadPrioritiesOperation.run();

        Priorities defaultPriorities = descriptor.createDefaultConfig().getPriorities();

        Priorities newPriorities = new Priorities();
        for (NamedKeyedObject priority : list) {
            newPriorities.setPriority(priority.getKey(), defaultPriorities.getPriorityByText(priority.getKey()));
        }

        setPriorities(newPriorities);
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
        for (Object id : data.getItemIds()) {
            i++;
            mspValue = new Integer(((Priority) data.getItem(id).getBean()).getValue());
            set.add(mspValue);
        }

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
        public static final String TEXT = "text";
        public static final String VALUE = "value";

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

        public void setValue(final int value) {
            this.value = value;
        }
    }
}

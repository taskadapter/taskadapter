package com.taskadapter.web.configeditor;

import com.taskadapter.PluginManager;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.Descriptor.Feature;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.model.NamedKeyedObjectImpl;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
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
    
    /**
     * Priorities model.
     */
    private final PrioritiesModel data;

    private Table prioritiesTable;
    private final PluginManager pluginManager;
	public static final String VALUE = "value";
	public static final String TEXT = "text";

    /**
     * @param editor     ConfigEditor
     * @param descriptor Descriptor
     */
    public PriorityPanel(ConfigEditor editor, Descriptor descriptor, PluginManager pluginManager) {
        super("Priorities");
        this.configEditor = editor;
        this.descriptor = descriptor;
        this.pluginManager = pluginManager;
        this.data = new PrioritiesModel(editor.getOrigConfig().getPriorities());
        buildUI();
    }

    private void buildUI() {
        setWidth(DefaultPanel.NARROW_PANEL_WIDTH);

        Collection<Feature> features = descriptor.getSupportedFeatures();

        prioritiesTable = new Table("Priorities");
        prioritiesTable.setContainerDataSource(data);
        prioritiesTable.setStyleName(Runo.TABLE_SMALL);
        prioritiesTable.addStyleName("priorities-table");
        prioritiesTable.setEditable(true);

        prioritiesTable.setColumnHeader(PriorityPanel.TEXT, NAME_HEADER);
        prioritiesTable.setColumnHeader(PriorityPanel.VALUE, VALUE_HEADER);
        prioritiesTable.setVisibleColumns(new Object[]{PriorityPanel.TEXT, PriorityPanel.VALUE});

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
        
		prioritiesTable.setContainerDataSource(data);
		
		final ItemSetChangeListener listener = new ItemSetChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void containerItemSetChange(ItemSetChangeEvent event) {
		        prioritiesTable.setPageLength(prioritiesTable.size() + 1);
			}
		};
		
		data.addListener(listener);
		listener.containerItemSetChange(null);

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

    private void reloadPriorityList() throws Exception {
        LookupOperation loadPrioritiesOperation = new LoadPrioritiesOperation(configEditor,
        		pluginManager.getPluginFactory(descriptor.getID()));
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
    	data.updateContent(items);
    }

    @Override
    public void validate() throws ValidationException {
        Integer mspValue;

        Set<Integer> set = new HashSet<Integer>();
        int i = 0;
        for (Object id : data.getItemIds()) {
            i++;
			mspValue = configEditor.getOrigConfig().getPriorities()
					.getPriorityByText((String) id);
            set.add(mspValue);
        }

        if (i != set.size()) {
            throw new ValidationException("TaskAdapter priorities duplication found. Please make all priority values unique in the table.");
        }
    }
}

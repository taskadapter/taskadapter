package com.taskadapter.web.configeditor;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.web.callbacks.DataProvider;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class PriorityPanel extends Panel implements Validatable {

    private final Logger logger = LoggerFactory.getLogger(PriorityPanel.class);

    private static final String NAME_HEADER = "Name";
    private static final String VALUE_HEADER = "Task Adapter Priority Value";

    /**
     * Priorities model.
     */
    private final PrioritiesModel data;

    /**
     * Used priorities.
     */
    private final Priorities priorities;

    private Table prioritiesTable;
	public static final String VALUE = "value";
	public static final String TEXT = "text";
	
	private final DataProvider<Priorities> priorityLoader;

    /**
	 * @param priorities
	 *            priorities to edit.
	 * @param priorityLoader
	 *            "load priorities" data provider. Optional (may be
	 *            <code>null</code>).
	 */
	public PriorityPanel(Priorities priorities,	DataProvider<Priorities> priorityLoader) {
		super("Priorities");
		this.priorities = priorities;
        this.priorityLoader = priorityLoader;
        this.data = new PrioritiesModel(priorities);
        buildUI();
    }

    private void buildUI() {
        setWidth(DefaultPanel.NARROW_PANEL_WIDTH);

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
        reloadButton.setEnabled(priorityLoader != null);
        reloadButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    reloadPriorityList();
                } catch (Exception e) {
                    logger.error("Error loading priorities: " + e.getMessage(), e);
                    getWindow().showNotification("Error!", e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });
        addComponent(reloadButton);
    }

    private void reloadPriorityList() throws Exception {
        final Priorities newPriorities = priorityLoader.loadData();
        setPriorities(newPriorities);
    }

    private void setPriorities(Priorities items) {
    	data.updateContent(items);
    }

    @Override
    public void validate() throws ValidationException {
        Integer mspValue;

        Set<Integer> set = new HashSet<Integer>();
        int i = 0;
        for (Object id : data.getItemIds()) {
            i++;
			mspValue = priorities.getPriorityByText((String) id);
            set.add(mspValue);
        }

        if (i != set.size()) {
            throw new ValidationException("TaskAdapter priorities duplication found. Please make all priority values unique in the table.");
        }
    }
}

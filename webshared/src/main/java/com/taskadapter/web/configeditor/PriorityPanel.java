package com.taskadapter.web.configeditor;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.exceptions.BadConfigException;

import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.data.Messages;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PriorityPanel extends VerticalLayout implements Validatable {
    private static final String BUNDLE_NAME = "com.taskadapter.web.configeditor.priorities";
    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    private final Logger logger = LoggerFactory.getLogger(PriorityPanel.class);

    private static final String NAME_HEADER = "Priority name";
    private static final String VALUE_HEADER = "Task Adapter Priority Value";

    private final Priorities priorities;

	public static final String VALUE = "value";
	public static final String TEXT = "text";
	
	private final DataProvider<Priorities> priorityLoader;
    private ExceptionFormatter exceptionFormatter;
    FormLayout prioritiesTableLayout = new FormLayout();
    /**
	 * @param priorities
	 *            priorities to edit.
	 * @param priorityLoader
	 *            "load priorities" data provider. Optional (may be <code>null</code>).
	 */
	public PriorityPanel(Priorities priorities,	DataProvider<Priorities> priorityLoader, ExceptionFormatter exceptionFormatter) {
		this.priorities = priorities;
        this.priorityLoader = priorityLoader;
        this.exceptionFormatter = exceptionFormatter;
        buildUI();
        setPriorities(priorities);
    }

    private void buildUI() {

        prioritiesTableLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("50em", 1),
                new FormLayout.ResponsiveStep("50em", 2));

        Button reloadButton = new Button("Reload");
        reloadButton.setEnabled(priorityLoader != null);
        reloadButton.addClickListener(event -> {
            try {
                reloadPriorityList();
            } catch (BadConfigException e) {
                String localizedMessage = exceptionFormatter.formatError(e);
                Notification.show(localizedMessage);
            } catch (Exception e) {
                logger.error("Error loading priorities: " + e.getMessage(), e);
                new Notification(e.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        add(new Label("Priorities"),
                prioritiesTableLayout,
                reloadButton);
    }

    private void reloadPriorityList() throws Exception {
        Priorities newPriorities = priorityLoader.loadData();
        setPriorities(newPriorities);
    }

    private void setPriorities(Priorities items) {
        prioritiesTableLayout.removeAll();
        items.getAllNames().forEach(s -> {
            Label label = new Label(s);
            TextField textField = new TextField();
            textField.setValue(items.getPriorityByText(s) + "");
            textField.setReadOnly(true);

            prioritiesTableLayout.add(label, textField);
        });
    }

    @Override
    public void validate() throws BadConfigException {
	    // TODO 14 validate priorities

/*        Integer mspValue;
        
        final Map<String, String> badValues = data.getInvalidValues();
        
        final StringBuilder message = new StringBuilder();
        for (Map.Entry<String, String> badMessage : badValues.entrySet()) {
            message.append(MESSAGES.format("badMappingValue",
                    badMessage.getKey(), badMessage.getValue()));
            message.append(' ');
        }

        Set<Integer> set = new HashSet<>();
        int i = 0;
        for (Object id : data.getItemIds()) {
            i++;
			mspValue = priorities.getPriorityByText((String) id);
            set.add(mspValue);
        }

        if (i != set.size()) {
            message.append(MESSAGES.get("duplicateMappingValue"));
        }
        
        if (message.length() > 0) {
            throw new BadConfigException(message.toString());
        }
 */
    }
}

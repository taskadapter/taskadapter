package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public abstract class ConfigEditor extends VerticalLayout implements WindowProvider {
    private CheckBox findUserByName;
    private List<Validatable> toValidate = new ArrayList<Validatable>();
    
    // TODO the parent editor class must save / load data itself instead of letting the children do this

    private final ConfigPanelContainer panelContainer = new ConfigPanelContainer();

    protected ConnectorConfig config;
    protected Services services;
    private TextField labelText;

    private static final String LABEL_DESCRIPTION_TEXT = "Description:";
    private static final String LABEL_TOOLTIP = "Text to show for this connector on 'Export' button. Enter any text.";

    protected ConfigEditor(ConnectorConfig config, Services services) {
        this.config = config;
        this.services = services;
        setImmediate(false);
        setMargin(true);
        setSpacing(true);

        HorizontalLayout descriptionLayout = new HorizontalLayout();
        descriptionLayout.setSpacing(true);
        addComponent(descriptionLayout);
        descriptionLayout.addComponent(new Label(LABEL_DESCRIPTION_TEXT));
        labelText = new TextField();
        labelText.setDescription(LABEL_TOOLTIP);
        labelText.addStyleName("label-textfield");
		labelText.setPropertyDataSource(new MethodProperty<String>(config,
				"label"));
        descriptionLayout.addComponent(labelText);
        setWidth("800px");
    }

    protected void setIfNotNull(AbstractField field, Object value) {
        if (value != null) {
            field.setValue(value);
        }
    }

    protected void addPanelToLayout(Layout component, Panel panel) {
        //if layout supports Validatable interface add it to validation list
        if (panel instanceof Validatable) {
            toValidate.add((Validatable)panel);
        }
        
        component.addComponent(panel);
        panelContainer.add(panel);
    }

    public CheckBox createFindUsersElementIfNeeded() {
        if (findUserByName == null) {
            findUserByName = new CheckBox("Find users based on assignee's name");
            findUserByName.setDescription("This option can be useful when you need to export a new MSP project file to Redmine/Jira/Mantis/....\n" +
                    "Task Adapter can load the system's users by resource names specified in the MSP file\n" +
                    "and assign the new tasks to them.\n" +
                    "Note: this operation usually requires 'Admin' permission in the system.");
			findUserByName.setPropertyDataSource(new MethodProperty<Boolean>(
					config, "findUserByName"));
        }

        return findUserByName;
    }

    public void validateAll() throws ValidationException {
        for (Validatable v : toValidate) {
            v.validate();
        }
        validate();
    }

    /**
     * the default implementation does nothing.
     *
     * @throws ValidationException instance
     */
    public void validate() throws ValidationException {
    }
    
    public ConnectorConfig getConfig() {
        return config;
    }

    public <T> T getPanel(Class<T> clazz) {
    	return panelContainer.get(clazz);
    }
}

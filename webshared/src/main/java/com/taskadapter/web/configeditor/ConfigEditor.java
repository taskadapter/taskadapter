package com.taskadapter.web.configeditor;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.*;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.service.Services;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public abstract class ConfigEditor extends VerticalLayout implements WindowProvider {
    protected CheckBox findUserByName;
    private List<Validatable> toValidate = new ArrayList<Validatable>();

    // TODO the parent editor class must save / load data itself instead of letting the children do this

    // Panels
    protected ServerPanel serverPanel;
    protected ProjectPanel projectPanel;
    protected PriorityPanel priorityPanel;
    protected FieldsMappingPanel fieldsMappingPanel;

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
        descriptionLayout.addComponent(labelText);
        setWidth("800px");
    }

    public abstract ConnectorConfig getPartialConfig();

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
    }

    public CheckBox createFindUsersElementIfNeeded() {
        if (findUserByName == null) {
            findUserByName = new CheckBox("Find users based on assignee's name");
            findUserByName.setDescription("This option can be useful when you need to export a new MSP project file to Redmine/Jira/Mantis/....\n" +
                    "Task Adapter can load the system's users by resource names specified in the MSP file\n" +
                    "and assign the new tasks to them.\n" +
                    "Note: this operation usually requires 'Admin' permission in the system.");
        }

        return findUserByName;
    }

    protected void addPriorityPanel(ConfigEditor editor, Descriptor descriptor, Priorities priorities) {
		priorityPanel = new PriorityPanel(editor, descriptor,
				services.getPluginManager());
        toValidate.add(priorityPanel);
        addComponent(priorityPanel);
        priorityPanel.setPriorities(priorities);
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
        ConnectorConfig config = getPartialConfig();
        config.setLabel((String) labelText.getValue());
        // TODO this casting to WebConfig is not nice.
        if (serverPanel != null) {
            ((WebConfig) config).setServerInfo(serverPanel.getServerInfo());
        }
        if (fieldsMappingPanel != null) {
            config.setFieldsMapping(fieldsMappingPanel.getResult());
        }
        if (projectPanel != null) {
            ProjectInfo projectInfo = projectPanel.getProjectInfo();
            ((WebConfig) config).setProjectKey(projectInfo.getProjectKey());
            ((WebConfig) config).setQueryId(projectInfo.getQueryId());
        }
        if (priorityPanel != null) {
            config.setPriorities(priorityPanel.getPriorities());
        }
        if (findUserByName != null) {
            ((WebConfig) config).setFindUserByName((Boolean) findUserByName.getValue());
        }
        return config;
    }

    public void setData(ConnectorConfig config) {
        this.config = config;
        setCommonFields();
    }

    private void setCommonFields() {
        if (serverPanel != null) {
            serverPanel.setServerInfo(((WebConfig) config).getServerInfo());
        }
        if (priorityPanel != null) {
            priorityPanel.setPriorities(config.getPriorities());
        }
        if (projectPanel != null) {
            projectPanel.setProjectInfo(((WebConfig) config).getProjectInfo());
        }
        if (findUserByName != null) {
            findUserByName.setValue(((WebConfig) config).isFindUserByName());
        }

        EditorUtil.setNullSafe(this.labelText, config.getLabel());
    }
}

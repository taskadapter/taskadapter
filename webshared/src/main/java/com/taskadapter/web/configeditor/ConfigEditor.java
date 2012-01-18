package com.taskadapter.web.configeditor;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.*;
import com.taskadapter.model.GTaskDescriptor;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Alexey Skorokhodov
 */
public abstract class ConfigEditor extends FormLayout {
    protected CheckBox findUserByName;
    private List<Validatable> toValidate = new ArrayList<Validatable>();

    // TODO the parent editor class must save / load data itself instead of letting the children do this
    private ServerPanel serverPanel;
    protected ProjectPanel projectPanel;
    private PriorityPanel priorityPanel;
    private FieldsMappingPanel fieldsMappingPanel;
    private ConnectorConfig config;

    protected ConfigEditor() {
        addStyleName("bordered_panel");
        setImmediate(false);
//        setWidth("100%");
        setMargin(true);
        setSpacing(true);
    }

    public abstract ConnectorConfig getPartialConfig();

    protected void setIfNotNull(AbstractField field, Object value) {
        if (value != null) {
            field.setValue(value);
        }
    }

    protected TextField createLabeledText(AbstractLayout layout, String caption, String tooltip) {
        TextField field = new TextField(caption);
        field.setDescription(tooltip);
        layout.addComponent(field);
        return field;
    }

    protected void addFindUsersByNameElement() {
        findUserByName = new CheckBox("Find users based on assignee's name (usually requires 'Admin' permission)");
        findUserByName.setDescription("This option can be useful when you need to export a new MSP project file to Redmine/Jira/Mantis/....\n" +
                "Task Adapter can load the system's users by resource names specified in the MSP file\n" +
                "and assign the new tasks to them.\n" +
                "Note: this operation usually requires 'Admin' permission in the system.");
    }

    protected void addServerPanel(WebServerInfo info) {
        serverPanel = new ServerPanel();
        toValidate.add(serverPanel);
        addComponent(serverPanel);
        serverPanel.setServerInfo(info);
    }

    protected void addProjectPanel(ConfigEditor editor, ProjectProcessor projectProcessor, ProjectInfo info) {
        projectPanel = new ProjectPanel(editor, projectProcessor);
        toValidate.add(projectPanel);
        addComponent(projectPanel);
        projectPanel.setProjectInfo(info);
    }

    protected void addPriorityPanel(ConfigEditor editor, Descriptor descriptor,Priorities priorities) {
        priorityPanel = new PriorityPanel(editor, descriptor);
        toValidate.add(priorityPanel);
        addComponent(priorityPanel);
        priorityPanel.setPriorities(priorities);
    }

    protected void addFieldsMappingPanel(AvailableFieldsProvider fieldsProvider, Map<GTaskDescriptor.FIELD, Mapping> mapping) {
        fieldsMappingPanel = new FieldsMappingPanel(fieldsProvider, mapping);
        toValidate.add(fieldsMappingPanel);
        addComponent(fieldsMappingPanel);
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
     * @throws ValidationException
     */
    public void validate() throws ValidationException {
    }

    public ConnectorConfig getConfig() {
        ConnectorConfig config = getPartialConfig();
        if (serverPanel != null) {
            ((WebConfig)config).setServerInfo(serverPanel.getServerInfo());
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
        return config;
    }

    public void setDataTMPMETHOD(ConnectorConfig config) {
        this.config = config;

        setCommonFields();
    }

    protected void setCommonFields() {
        if (serverPanel != null) {
            serverPanel.setServerInfo(((WebConfig) config).getServerInfo());
        }
        if (priorityPanel != null) {
            priorityPanel.setPriorities(config.getPriorities());
        }
        if (projectPanel != null) {
            projectPanel.setProjectInfo(((WebConfig) config).getProjectInfo());
        }

        // TODO add this
//        EditorUtil.setNullSafe(this.labelText, config.getLabel());
    }
}

package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.LookupOperation;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Alexey Skorokhodov
 */
public class JiraEditor extends ConfigEditor {

    private static final String SAVE_GROUP_LABEL = "Set these fields when EXPORTING to Jira";

    private TextField jiraComponent;
    private TextField affectedVersion;
    private TextField fixForVersion;
    private CustomFieldsPanel customFieldsTable;

    public JiraEditor(ConnectorConfig config) {
        super(config);
        buildUI();
        addFieldsMappingPanel(JiraDescriptor.instance.getAvailableFieldsProvider(), config.getFieldsMapping());

        setDataToForm();
    }

    private void setDataToForm() {
        JiraConfig jiraConfig = (JiraConfig) config;
        setIfNotNull(affectedVersion, jiraConfig.getAffectedVersion());
        setIfNotNull(fixForVersion, jiraConfig.getFixForVersion());
        setIfNotNull(jiraComponent, jiraConfig.getComponent());
        Map<String, String> trackers = jiraConfig.getCustomFields();
        for (String key : trackers.keySet()) {
            addTrackerOnTable(key, trackers.get(key));
        }
    }

    private void buildUI() {
        addServerPanel();
        addProjectPanel(this, new JiraProjectProcessor(this));

        // SET THESE FIELDS WHEN EXPORTING
        FormLayout saveGroup = new FormLayout();
        saveGroup.setCaption(SAVE_GROUP_LABEL);
        addComponent(saveGroup);

        this.jiraComponent = createLabeledText(saveGroup, "Project Component", "Component inside the Jira project");

        LookupOperation loadComponentsOperation = new LoadComponentsOperation(this, JiraDescriptor.instance);
        EditorUtil.createLookupButton(getWindow(),
                "...",
                "Show list of available components on the given server.",
                loadComponentsOperation, jiraComponent, true);

        this.affectedVersion = createLabeledText(saveGroup, "Set 'Affected version' to:", "Set this 'affected version' value when submitting issues to Jira.");
        LoadVersionsOperation loadVersionsOperation = new LoadVersionsOperation(this, JiraDescriptor.instance);
        EditorUtil.createLookupButton(getWindow(),
                "...",
                "Show list of available versions.",
                loadVersionsOperation, affectedVersion, true);

        this.fixForVersion = createLabeledText(saveGroup, "Set 'Fix for version' to:", "Set this 'fix for version' value when submitting issues to Jira.");
        EditorUtil.createLookupButton(getWindow(),
                "...",
                "Show list of available versions.",
                loadVersionsOperation, fixForVersion, true);

        addComponent(new CustomFieldsPanel());
        addPriorityPanel(this, JiraDescriptor.instance, config.getPriorities());
    }

    @Override
    public ConnectorConfig getPartialConfig() {
        JiraConfig newConfig = new JiraConfig();

        newConfig.setAffectedVersion((String) affectedVersion.getValue());
        newConfig.setFixForVersion((String) fixForVersion.getValue());
        newConfig.setComponent((String) jiraComponent.getValue());
        Map<String, String> trackers = new TreeMap<String, String>();
        // TODO fix this: custom fields are NOT saved
//        for (TableItem tableItem : customFieldsTable.getItems()) {
//            String idText = tableItem.getValue(0);
//            String nameText = tableItem.getValue(1);
//            trackers.put(idText, nameText);
//        }
        newConfig.setCustomFields(trackers);
        return newConfig;
    }

    private void addTrackerOnTable(String id, String name) {
//        TableItem tableItem = new TableItem(customFieldsTable, SWT.NONE);
//        tableItem.setText(new String[]{id, name});
    }

    //
    private void removeTrackerFromTable() {
//        int[] selectionIndexes = customFieldsTable.getSelectionIndices();
//        customFieldsTable.remove(selectionIndexes);
//        idEditor.getEditor().dispose();
//        nameEditor.getEditor().dispose();
//        customFieldsTable.update();
    }

}
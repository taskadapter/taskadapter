package com.taskadapter.connector.mantis.editor;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.configeditor.ConfigEditor;

/**
 * @author Alexey Skorokhodov
 */
public class MantisEditor extends ConfigEditor {

    public MantisEditor(ConnectorConfig config) {
        super(config);
    }

    // TODO implement the panel
/*
    	@Override
    	public ConnectorConfig getResult() throws ValidationGUIException {
    		MantisConfig config = new MantisConfig();

    		config.setSaveIssueRelations(saveRelations.getSelection());
    		config.setFindUserByName(findUserByName.getSelection());

    		return config;
    	}

    	@Override
    	protected void createComposite() {
    		Composite composite = new Composite(tabComposite, SWT.NONE);
    		RowLayout rowLayout = new RowLayout();
    		rowLayout.type = SWT.VERTICAL;
    		composite.setLayout(rowLayout);

    		createLabelSection(composite, 4);

    		Composite serverAndProjectGroup = new Composite(composite, SWT.NONE);
    		final GridLayout gridLayout1 = new GridLayout(2, false);
    		serverAndProjectGroup.setLayout(gridLayout1);

    		serverPanel = addServerPanel(serverAndProjectGroup);

    		projectPanel = addProjectPanel(this, serverAndProjectGroup, new MantisProjectProcessor(this));
    		projectPanel.setProjectKeyRequired(true);

    		addFindUsersByNameElement(serverAndProjectGroup);

    		final GridData gridData4col = new GridData();
    		gridData4col.horizontalSpan = 4;
    		findUserByName.setLayoutData(gridData4col);

    		addFieldsMappingSection(composite);
    		addSaveRelationSection(composite);
    	}

    	@Override
    	protected void setDataToForm(ConnectorConfig oldConfig) {
    		MantisConfig config = (MantisConfig) oldConfig;

    		setBooleanIfNotNull(saveRelations,
    				config.getSaveIssueRelations());

    		serverPanel.setServerInfo(config.getServerInfo());
    		ProjectInfo projectInfo = new ProjectInfo();
    		projectInfo.setProjectKey(config.getProjectKey());
    		projectInfo.setQueryId(config.getQueryId());
    		projectPanel.setProjectInfo(projectInfo);

    		setBooleanIfNotNull(findUserByName, config.getFindUserByName());
    	}
*/

    @Override
    public ConnectorConfig getPartialConfig() {
        return config;
    }
}

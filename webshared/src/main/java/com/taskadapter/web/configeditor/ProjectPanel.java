package com.taskadapter.web.configeditor;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
import com.vaadin.data.Property;
import com.vaadin.ui.*;

import java.util.List;

/**
 * "Project info" panel with Project Key, Query Id.
 *
 * @author Alexey Skorokhodov
 */
public class ProjectPanel extends Panel implements Validatable {
    private static final String DEFAULT_PANEL_CAPTION = "Project Info";
    private static final int COLUMNS_NUMBER = 2;

    private TextField projectKey;
    private TextField queryId;

    private Label projectKeyLabel;
    private Label queryIdLabel;
    private Button showQueriesButton;
    private static final String TEXT_AREA_WIDTH = "120px";
    
    /**
     * Project provider.
     */
    private final DataProvider<List<? extends NamedKeyedObject>> projectProvider;
    
    /**
     * "Show project info" callback.
     */
    private final SimpleCallback projectInfoCallback;
    
    /**
     * Query provider.
     */
    private final DataProvider<List<? extends NamedKeyedObject>> queryProvider;
    
    /**
     * Project key property.
     */
    private final Property projectKeyProperty;
    
    /**
     * Query id property.
     */
    private final Property queryIdProperty;
    
    /**
     * Window provider.
     */
    private final WindowProvider windowProvider;

    /**
     * Creates a new project panel.
     * @param windowProvider window provider.
     * @param projectKey project key, required.
     * @param queryId query id, optional. 
     * @param projectProvider project provider, optional.
     * @param projectInfoCallback project info callback, optional.
     * @param queryProvider query provider, optional.
     */
	public ProjectPanel(WindowProvider windowProvider,
			Property projectKey,
			Property queryId,
			DataProvider<List<? extends NamedKeyedObject>> projectProvider,
			SimpleCallback projectInfoCallback,
			DataProvider<List<? extends NamedKeyedObject>> queryProvider) {
        super(DEFAULT_PANEL_CAPTION);
        this.windowProvider = windowProvider;
		this.projectKeyProperty = projectKey;
		this.queryIdProperty = queryId;
		this.projectProvider = projectProvider;
		this.projectInfoCallback = projectInfoCallback;
		this.queryProvider = queryProvider;
        buildUI();
    }

    private void buildUI() {
        GridLayout gridLayout = new GridLayout();
        addComponent(gridLayout);

        gridLayout.setColumns(COLUMNS_NUMBER);
        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);

        projectKeyLabel = new Label("Project key:");
        gridLayout.addComponent(projectKeyLabel);
        gridLayout.setComponentAlignment(projectKeyLabel, Alignment.MIDDLE_LEFT);

        final HorizontalLayout keyHorizontalLayout = new HorizontalLayout();
        gridLayout.addComponent(keyHorizontalLayout);

        projectKey = new TextField();
		projectKey.setPropertyDataSource(projectKeyProperty);
        keyHorizontalLayout.addComponent(projectKey);
        projectKey.setWidth(TEXT_AREA_WIDTH);


        Button infoButton = EditorUtil.createButton("Info", "View the project info",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        loadProject();
                    }
                }
        );
        infoButton.setEnabled(projectInfoCallback != null);
        keyHorizontalLayout.addComponent(infoButton);

        Button projectKeyButton = EditorUtil.createLookupButton(
                windowProvider,
                "...",
                "Show list of available projects on the server.",
                "Select project",
                "List of projects on the server",
                projectProvider,
                projectKeyProperty,
                false
        );
        projectKeyButton.setEnabled(projectProvider != null);
        keyHorizontalLayout.addComponent(projectKeyButton);


        if (queryIdProperty != null) {
            queryIdLabel = new Label("Query ID:");
            gridLayout.addComponent(queryIdLabel);
            gridLayout.setComponentAlignment(queryIdLabel, Alignment.MIDDLE_LEFT);

            final HorizontalLayout idHorizontalLayout = new HorizontalLayout();
            gridLayout.addComponent(idHorizontalLayout);
	        queryId = new TextField();
	        queryId.setDescription("Custom query/filter ID (number). You need to create a query on the server before accessing it from here.\n"
	                + "Read help for more details.");
	        idHorizontalLayout.addComponent(queryId);
	        queryId.setWidth(TEXT_AREA_WIDTH);
			queryId.setPropertyDataSource(queryIdProperty);
	
	        showQueriesButton = EditorUtil.createLookupButton(
	                windowProvider,
	                "...",
	                "Show available saved queries on the server.",
	                "Select Query",
	                "List of saved queries on the server",
	                queryProvider,
	                queryIdProperty,
	                false
	        );
	        // TODO maybe set "enabled" basing on whether or not loadSavedQueriesOperation is NULL?
	        // then can delete the whole "features" mechanism
	        showQueriesButton.setEnabled(queryProvider != null);
	        idHorizontalLayout.addComponent(showQueriesButton);
        }
    }

    private void loadProject() {
        try {
        	projectInfoCallback.callBack();
        } catch (ValidationException e) {
			windowProvider.getWindow().showNotification(
					"Please, update the settings", e.getMessage());
        }
    }

    private String getProjectKey() {
        return (String) projectKey.getValue();
    }

    private String getQueryId() {
		return queryId == null ? null : (String) queryId.getValue();
    }

    public void setProjectKeyLabel(String text) {
        projectKeyLabel.setValue(text);
    }

    @Override
    public void validate() throws ValidationException {
        if (!Strings.isNullOrEmpty(getQueryId())) {
            try {
                Integer.parseInt(getQueryId());
            } catch (NumberFormatException e) {
                throw new ValidationException("Query Id must be a number");
            }
        }

        if (getProjectKey().trim().isEmpty()) {
            throw new ValidationException("Project Key is required");
        }
    }
}

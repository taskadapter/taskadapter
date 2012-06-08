package com.taskadapter.web.configeditor;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebConfig;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.MethodProperty;
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

    private final ConfigEditor editor;
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

	public ProjectPanel(ConfigEditor editor, 
			DataProvider<List<? extends NamedKeyedObject>> projectProvider,
			SimpleCallback projectInfoCallback,
			DataProvider<List<? extends NamedKeyedObject>> queryProvider) {
        super(DEFAULT_PANEL_CAPTION);
        this.editor = editor;
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
		final WebConfig config = (WebConfig) editor.getConfig();
		final MethodProperty<String> projectKeyProperty = new MethodProperty<String>(
				config, "projectKey");
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
                editor,
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
		final AbstractProperty queryIdProperty = new AbstractProperty() {
			private static final long serialVersionUID = 1L;

			@Override
			public void setValue(Object newValue) throws ReadOnlyException,
					ConversionException {
				final String strValue = newValue.toString();
				if (strValue.isEmpty()) {
					config.setQueryId(null);
				} else {
					try {
						config.setQueryId(Integer.valueOf(strValue));
					} catch (NumberFormatException e) {
						throw new ConversionException(e);
					}
					fireValueChange();
				}
			}
			
			@Override
			public Object getValue() {
				return config.getQueryId() == null ? "" : config.getQueryId();
			}
			
			@Override
			public Class<?> getType() {
				return String.class;
			}
		};
		queryId.setPropertyDataSource(queryIdProperty);

        showQueriesButton = EditorUtil.createLookupButton(
                editor,
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

    private void loadProject() {
        try {
        	projectInfoCallback.callBack();
        } catch (ValidationException e) {
            editor.getWindow().showNotification("Please, update the settings", e.getMessage());
        }
    }

    private String getProjectKey() {
        return (String) projectKey.getValue();
    }

    private String getQueryId() {
        return (String) queryId.getValue();
    }

    public void setProjectKeyLabel(String text) {
        projectKeyLabel.setValue(text);
    }

    public void hideQueryId() {
        queryId.setVisible(false);
        queryIdLabel.setVisible(false);
        showQueriesButton.setVisible(false);
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

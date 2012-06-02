package com.taskadapter.web.configeditor;

import com.google.common.base.Strings;
import com.taskadapter.PluginManager;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebConfig;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.*;

import java.util.Collection;

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

    private final ProjectProcessor projectProcessor;
    private final PluginManager pluginManager;
    private Label projectKeyLabel;
    private Label queryIdLabel;
    private Button showQueriesButton;
    private static final String TEXT_AREA_WIDTH = "120px";

	public ProjectPanel(ConfigEditor editor, ProjectProcessor projectProcessor,
			PluginManager pluginManager) {
        super(DEFAULT_PANEL_CAPTION);
        this.editor = editor;
        this.projectProcessor = projectProcessor;
        this.pluginManager = pluginManager;
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
		final WebConfig config = (WebConfig) editor.getOrigConfig();
		projectKey.setPropertyDataSource(new MethodProperty<String>(config,
				"projectKey"));
        keyHorizontalLayout.addComponent(projectKey);
        projectKey.setWidth(TEXT_AREA_WIDTH);


        Collection<ProjectProcessor.EditorFeature> features = projectProcessor.getSupportedFeatures();

        Button infoButton = EditorUtil.createButton("Info", "View the project info",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        loadProject();
                    }
                }
        );
        infoButton.setEnabled(features.contains(ProjectProcessor.EditorFeature.LOAD_PROJECT_INFO));
        keyHorizontalLayout.addComponent(infoButton);

		LookupOperation loadProjectsOperation = new LoadProjectsOperation(
				editor, pluginManager.getPluginFactory(projectProcessor
						.getDescriptor().getID()));
        Button projectKeyButton = EditorUtil.createLookupButton(
                editor,
                "...",
                "Show list of available projects on the server.",
                "Select project",
                "List of projects on the server",
                loadProjectsOperation,
                projectKey,
                false
        );
        projectKeyButton.setEnabled(features.contains(ProjectProcessor.EditorFeature.LOAD_PROJECTS));
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
		queryId.setPropertyDataSource(new AbstractProperty() {
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
		});

        LookupOperation loadSavedQueriesOperation = projectProcessor.getLoadSavedQueriesOperation(editor);

        showQueriesButton = EditorUtil.createLookupButton(
                editor,
                "...",
                "Show available saved queries on the server.",
                "Select Query",
                "List of saved queries on the server",
                loadSavedQueriesOperation,
                queryId,
                false
        );
        // TODO maybe set "enabled" basing on whether or not loadSavedQueriesOperation is NULL?
        // then can delete the whole "features" mechanism
        showQueriesButton.setEnabled(features.contains(ProjectProcessor.EditorFeature.LOAD_SAVED_QUERIES));
        idHorizontalLayout.addComponent(showQueriesButton);
    }

    private void loadProject() {
        try {
            if (getProjectKey().isEmpty()) {
                // TODO need to use the field value and
                // disable the button
                // instead of this simple check
                throw new ValidationException("Please, provide the project key first");
            }

            projectProcessor.loadProject(getProjectKey());

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

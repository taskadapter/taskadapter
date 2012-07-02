package com.taskadapter.web.configeditor;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
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
    private TextField queryValue;

    private Label projectKeyLabel;
    private Label queryValueLabel;
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
    private final Property queryValueProperty;

    /**
     * Window provider.
     */
    private final WindowProvider windowProvider;

    /**
     * Creates a new project panel.
     *
     * @param windowProvider      window provider.
     * @param projectKey          project key, required.
     * @param queryValue          query id, optional.
     * @param projectProvider     project provider, optional.
     * @param projectInfoCallback project info callback, optional.
     * @param queryProvider       query provider, optional.
     */
    public ProjectPanel(WindowProvider windowProvider,
                        Property projectKey,
                        Property queryValue,
                        DataProvider<List<? extends NamedKeyedObject>> projectProvider,
                        SimpleCallback projectInfoCallback,
                        DataProvider<List<? extends NamedKeyedObject>> queryProvider) {
        super(DEFAULT_PANEL_CAPTION);
        this.windowProvider = windowProvider;
        this.projectKeyProperty = projectKey;
        this.queryValueProperty = queryValue;
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


        if (queryValueProperty != null) {
            queryValueLabel = new Label("Query ID:");
            gridLayout.addComponent(queryValueLabel);
            gridLayout.setComponentAlignment(queryValueLabel, Alignment.MIDDLE_LEFT);

            final HorizontalLayout queryHorizontalLayout = new HorizontalLayout();
            gridLayout.addComponent(queryHorizontalLayout);
            queryValue = new TextField();
            queryValue.setDescription("Custom query/filter ID (number). You need to create a query on the server before accessing it from here.\n"
                    + "Read help for more details.");
            queryHorizontalLayout.addComponent(queryValue);
            queryValue.setWidth(TEXT_AREA_WIDTH);
            queryValue.setPropertyDataSource(queryValueProperty);

            showQueriesButton = EditorUtil.createLookupButton(
                    windowProvider,
                    "...",
                    "Show available saved queries on the server.",
                    "Select Query",
                    "List of saved queries on the server",
                    queryProvider,
                    queryValueProperty,
                    false
            );

            setQueryLabels();
            // TODO maybe set "enabled" basing on whether or not loadSavedQueriesOperation is NULL?
            // then can delete the whole "features" mechanism
            showQueriesButton.setEnabled(queryProvider != null);
            queryHorizontalLayout.addComponent(showQueriesButton);
        }
    }

    private boolean isQueryInteger() {
        return queryValueProperty.getType().equals(Integer.class);
    }

    private void setQueryLabels() {
        if (isQueryInteger()) {
            queryValueLabel.setValue("Query ID:");
            queryValue.setDescription("Custom query/filter ID (number). You need to create a query on the server before accessing it from here.\n"
                    + "Read help for more details.");
        } else {
            queryValueLabel.setValue("Query:");
            queryValue.setDescription("You can set query string to filter issues.\n"
                    + "Read help for more details.");
        }
    }

    private void loadProject() {
        try {
            projectInfoCallback.callBack();
        } catch (ValidationException e) {
            windowProvider.getWindow().showNotification(
                    "Please, update the settings", e.getMessage());
        } catch (ConnectorException e) {
            // TODO: format exceptions.
            windowProvider.getWindow().showNotification(
                    "Failed to load data", e.getMessage());
        }
    }

    private String getProjectKey() {
        return (String) projectKey.getValue();
    }

    private String getQueryValue() {
        return queryValue == null ? null : (String) queryValue.getValue();
    }

    public void setProjectKeyLabel(String text) {
        projectKeyLabel.setValue(text);
    }

    @Override
    public void validate() throws ValidationException {
        if (!Strings.isNullOrEmpty(getQueryValue()) && isQueryInteger()) {
            try {
                Integer.parseInt(getQueryValue());
            } catch (NumberFormatException e) {
                throw new ValidationException("Query Id must be a number");
            }
        }

        if (getProjectKey().trim().isEmpty()) {
            throw new ValidationException("Project Key is required");
        }
    }
}

package com.taskadapter.web.configeditor;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
import com.vaadin.data.Property;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.taskadapter.web.configeditor.EditorUtil.textInput;
import static com.taskadapter.web.ui.Grids.addTo;

/**
 * "Project info" panel with Project Key, Query Id.
 */
public class ProjectPanel extends Panel implements Validatable {
    private final static Logger logger = LoggerFactory.getLogger(ProjectPanel.class);

    private static final String DEFAULT_PANEL_CAPTION = "Project Info";
    private static final String TEXT_AREA_WIDTH = "120px";

    private TextField projectKey;
    private TextField queryValue;

    private Label projectKeyLabel;
    private Label queryValueLabel;

    private final DataProvider<List<? extends NamedKeyedObject>> projectProvider;

    /**
     * "Show project info" callback.
     */
    private final SimpleCallback projectInfoCallback;

    private final DataProvider<List<? extends NamedKeyedObject>> queryProvider;

    private final Property<String> projectKeyProperty;

    private final Property<String> queryValueProperty;

    private final ExceptionFormatter exceptionFormatter;

    /**
     * Creates a new project panel.
     *
     * @param projectKey          project key, required.
     * @param queryValue          query id, optional.
     * @param projectProvider     project provider, optional.
     * @param projectInfoCallback project info callback, optional.
     * @param queryProvider       query provider, optional.
     * @param exceptionFormatter  exception formatter, required.
     */
    public ProjectPanel(Property<String> projectKey,
                        Property<String> queryValue,
                        DataProvider<List<? extends NamedKeyedObject>> projectProvider,
                        SimpleCallback projectInfoCallback,
                        DataProvider<List<? extends NamedKeyedObject>> queryProvider,
                        ExceptionFormatter exceptionFormatter) {
        super(DEFAULT_PANEL_CAPTION);
        this.projectKeyProperty = projectKey;
        this.queryValueProperty = queryValue;
        this.projectProvider = projectProvider;
        this.projectInfoCallback = projectInfoCallback;
        this.queryProvider = queryProvider;
        this.exceptionFormatter = exceptionFormatter;
        buildUI();
    }

    private void buildUI() {
        GridLayout grid = new GridLayout(4, 2);
        setContent(grid);

        grid.setSpacing(true);

        projectKeyLabel = new Label("Project key:");
        addTo(grid, Alignment.MIDDLE_LEFT, projectKeyLabel);

        projectKey = textInput(projectKeyProperty, TEXT_AREA_WIDTH);
        addTo(grid, Alignment.MIDDLE_CENTER, projectKey);
        projectKey.setNullRepresentation("");

        Button infoButton = EditorUtil.createButton("Info", "View the project info",
                (Button.ClickListener) event -> loadProject()
        );
        infoButton.setEnabled(projectInfoCallback != null);
        addTo(grid, Alignment.MIDDLE_CENTER, infoButton);

        Button showProjectsButton = EditorUtil.createLookupButton(
                "...",
                "Show list of available projects on the server.",
                "Select project",
                "List of projects on the server",
                projectProvider,
                projectKeyProperty,
                false, exceptionFormatter
        );
        showProjectsButton.setEnabled(projectProvider != null);
        addTo(grid, Alignment.MIDDLE_CENTER, showProjectsButton);

        if (queryValueProperty != null) {
            queryValueLabel = new Label("Query ID:");
            addTo(grid, Alignment.MIDDLE_LEFT, queryValueLabel);

            queryValue = textInput(queryValueProperty, TEXT_AREA_WIDTH);
            queryValue.setDescription("Custom query/filter ID (number). You need to create a query on the server before accessing it from here.\n"
                    + "Read help for more details.");
            addTo(grid, Alignment.MIDDLE_CENTER, queryValue);
            queryValue.setNullRepresentation("");

            Button showQueriesButton = EditorUtil.createLookupButton(
                    "...",
                    "Show available saved queries on the server.",
                    "Select Query",
                    "List of saved queries on the server",
                    queryProvider,
                    queryValueProperty,
                    false, exceptionFormatter
            );

            setQueryLabels();
            // TODO maybe set "enabled" basing on whether or not loadSavedQueriesOperation is NULL?
            // then can delete the whole "features" mechanism
            showQueriesButton.setEnabled(queryProvider != null);
            addTo(grid, Alignment.MIDDLE_CENTER, showQueriesButton);
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
        } catch (BadConfigException e) {
            logger.error(e.toString());
            String localizedMessage = exceptionFormatter.formatError(e);
            Notification.show(localizedMessage);
        } catch (ConnectorException e) {
            String localizedMessage = exceptionFormatter.formatError(e);
            logger.error(e.toString());
            Notification.show(localizedMessage);
        }
    }

    private String getQueryValue() {
        return queryValue == null ? null : queryValue.getValue();
    }

    public void setProjectKeyLabel(String text) {
        projectKeyLabel.setValue(text);
    }

    @Override
    public void validate() throws BadConfigException {
        if (!Strings.isNullOrEmpty(getQueryValue()) && isQueryInteger()) {
            try {
                Integer.parseInt(getQueryValue());
            } catch (NumberFormatException e) {
                // TODO !! create a specific exception and move the string into messages file.
                throw new BadConfigException("Query Id must be a number");
            }
        }

        // TODO !!! will result in NPE if getProjectKey can return NULL. (can it?)
        if (projectKey.getValue().trim().isEmpty()) {
            throw new ProjectNotSetException();
        }
    }
}

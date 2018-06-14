package com.taskadapter.web.configeditor;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.model.GProject;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.configeditor.server.StringToLongNonFormattingConverter;
import com.taskadapter.webui.Page;
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
import scala.Option;

import java.util.List;

import static com.taskadapter.web.configeditor.EditorUtil.textInput;
import static com.taskadapter.web.ui.Grids.addTo;
import static com.taskadapter.web.ui.MessageUtils.nvl;

/**
 * "Project info" panel with Project Key, Query Id.
 */
public class ProjectPanel extends Panel implements Validatable {
    private final static Logger logger = LoggerFactory.getLogger(ProjectPanel.class);

    private static final String DEFAULT_PANEL_CAPTION = "Project Info";
    private static final String TEXT_AREA_WIDTH = "120px";

    private TextField projectKey;
    private Label queryIdLabel;
    private TextField queryIdValue;

    private Label projectKeyLabel;
    private Label queryTextLabel;
    private TextField queryTextValue;

    private final DataProvider<List<? extends NamedKeyedObject>> projectProvider;

    private final DataProvider<GProject> projectInfoLoader;
    private final DataProvider<List<? extends NamedKeyedObject>> queryProvider;

    private final Property<String> projectKeyProperty;

    private final Option<Property<Long>> queryIdProperty;
    private final Option<Property<String>> queryTextProperty;

    private final ExceptionFormatter exceptionFormatter;

    public ProjectPanel(Property<String> projectKey,
                        Option<Property<Long>> queryIdProperty,
                        Option<Property<String>> queryTextProperty,
                        DataProvider<List<? extends NamedKeyedObject>> projectProvider,
                        DataProvider<GProject> projectInfoLoader,
                        DataProvider<List<? extends NamedKeyedObject>> queryProvider,
                        ExceptionFormatter exceptionFormatter) {
        super(DEFAULT_PANEL_CAPTION);
        this.projectKeyProperty = projectKey;
        this.queryIdProperty = queryIdProperty;
        this.queryTextProperty = queryTextProperty;
        this.projectProvider = projectProvider;
        this.projectInfoLoader = projectInfoLoader;
        this.queryProvider = queryProvider;
        this.exceptionFormatter = exceptionFormatter;
        buildUI();
    }

    private GridLayout grid = new GridLayout(4, 2);

    private void buildUI() {
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
        infoButton.setEnabled(projectInfoLoader != null);
        addTo(grid, Alignment.MIDDLE_CENTER, infoButton);

        Button showProjectsButton = EditorUtil.createLookupButton(
                "...",
                "Show list of available projects on the server.",
                "Select project",
                "List of projects on the server",
                projectProvider,
                exceptionFormatter, namedKeyedObject -> {
                    projectKeyProperty.setValue(namedKeyedObject.getKey());
                    return null;
                }
        );
        showProjectsButton.setEnabled(projectProvider != null);
        addTo(grid, Alignment.MIDDLE_CENTER, showProjectsButton);

        if (queryIdProperty.isDefined()) {
            addQueryIdWithLoader(queryIdProperty.get());
        }
        if (queryTextProperty.isDefined()) {
            addQueryText(queryTextProperty.get());
        }
    }

    private void addQueryIdWithLoader(Property<Long> stringProperty) {
        queryIdLabel = new Label(Page.message("editConfig.projectPanel.queryId"));
        queryIdLabel.setDescription(Page.message("editConfig.projectPanel.queryId.description"));
        addTo(grid, Alignment.MIDDLE_LEFT, queryIdLabel);

        queryIdValue = textInput(stringProperty, TEXT_AREA_WIDTH);
        queryIdValue.setDescription(Page.message("editConfig.projectPanel.queryId.description"));
        addTo(grid, Alignment.MIDDLE_CENTER, queryIdValue);
        queryIdValue.setNullRepresentation("");
        queryIdValue.setValidationVisible(true);
        queryIdValue.setConverter(new StringToLongNonFormattingConverter());

        Button showQueriesButton = EditorUtil.createLookupButton(
                "...",
                "Show available saved queries on the server.",
                "Select Query",
                "List of saved queries on the server",
                queryProvider,
                exceptionFormatter,namedKeyedObject -> {
                    stringProperty.setValue(Long.valueOf(namedKeyedObject.getKey()));
                    return null;
                }
        );

        // TODO maybe set "enabled" basing on whether or not loadSavedQueriesOperation is NULL?
        // then can delete the whole "features" mechanism
        showQueriesButton.setEnabled(queryProvider != null);
        addTo(grid, Alignment.MIDDLE_CENTER, showQueriesButton);
    }

    private void addQueryText(Property<String> stringProperty) {
        queryTextLabel = new Label(Page.message("editConfig.projectPanel.queryText"));
        queryTextLabel.setDescription(Page.message("editConfig.projectPanel.queryText.description"));
        addTo(grid, Alignment.MIDDLE_LEFT, queryTextLabel);

        queryTextValue = textInput(stringProperty, TEXT_AREA_WIDTH);
        queryTextValue.setDescription(Page.message("editConfig.projectPanel.queryId.description"));
        addTo(grid, Alignment.MIDDLE_CENTER, queryTextValue);
        queryTextValue.setNullRepresentation("");
    }

    private void loadProject() {
        try {
            GProject gProject = projectInfoLoader.loadData();
            showProjectInfo(gProject);
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

    private static void showProjectInfo(GProject project) {
        String msg = "Key:  " + project.getKey()
                + "\nName: " + project.getName()
                + "\nHomepage: " + nvl(project.homepage())
                + "\nDescription: " + nvl(project.description());
        Notification.show("Project Info", msg, Notification.Type.HUMANIZED_MESSAGE);
    }

    private String getQueryIdValue() {
        return queryIdValue == null ? null : queryIdValue.getValue();
    }

    public void setProjectKeyLabel(String text) {
        projectKeyLabel.setValue(text);
    }

    @Override
    public void validate() throws BadConfigException {
        if (!Strings.isNullOrEmpty(getQueryIdValue())) {
            try {
                Integer.parseInt(getQueryIdValue());
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

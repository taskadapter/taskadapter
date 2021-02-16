package com.taskadapter.web.configeditor;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.model.GProject;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.webui.Page;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToLongConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;

import java.util.List;

import static com.taskadapter.web.configeditor.EditorUtil.textInput;
import static com.taskadapter.web.ui.MessageUtils.nvl;

/**
 * "Project info" panel with Project Key, Filter Id.
 */
public class ProjectPanel extends FormLayout implements Validatable {
    private final static Logger logger = LoggerFactory.getLogger(ProjectPanel.class);

    private TextField projectKeyField;
    private Label queryIdLabel;
    private TextField queryIdValue;

    private Label projectKeyLabel;
    private Label queryTextLabel;
    private TextField queryTextValue;

    private final DataProvider<List<? extends NamedKeyedObject>> projectProvider;
    private final DataProvider<GProject> projectInfoLoader;
    private final DataProvider<List<? extends NamedKeyedObject>> queryProvider;

    private final Binder<?> binder;
    private final String projectKeyProperty;
    private final Option<String> queryIdProperty;
    private final Option<String> queryTextProperty;
    private final ExceptionFormatter exceptionFormatter;

    public ProjectPanel(Binder<?> binder,
                        String projectKey,
                        Option<String> queryIdProperty,
                        Option<String> queryTextProperty,
                        DataProvider<List<? extends NamedKeyedObject>> projectProvider,
                        DataProvider<GProject> projectInfoLoader,
                        DataProvider<List<? extends NamedKeyedObject>> queryProvider,
                        ExceptionFormatter exceptionFormatter) {
        this.binder = binder;
        this.projectKeyProperty = projectKey;
        this.queryIdProperty = queryIdProperty;
        this.queryTextProperty = queryTextProperty;
        this.projectProvider = projectProvider;
        this.projectInfoLoader = projectInfoLoader;
        this.queryProvider = queryProvider;
        this.exceptionFormatter = exceptionFormatter;
        buildUI();
    }

    private void buildUI() {
        setResponsiveSteps(
                new ResponsiveStep("40em", 1),
                new ResponsiveStep("50em", 2),
                new ResponsiveStep("20em", 3),
                new ResponsiveStep("20em", 4));

        projectKeyLabel = new Label("Project key");
        projectKeyField = textInput(binder, projectKeyProperty);

        Button infoButton = EditorUtil.createButton("Info", "View the project info",
                event -> loadProject()
        );
        infoButton.setEnabled(projectInfoLoader != null);

        Button showProjectsButton = EditorUtil.createLookupButton(
                "...",
                "Show list of available projects on the server.",
                "Select project",
                projectProvider,
                exceptionFormatter, namedKeyedObject -> {
                    projectKeyField.setValue(namedKeyedObject.getKey());
                    return null;
                }
        );
        showProjectsButton.setEnabled(projectProvider != null);

        add(projectKeyLabel, projectKeyField, infoButton, showProjectsButton);

        if (queryIdProperty.isDefined()) {
            addQueryIdWithLoader(queryIdProperty.get());
        }
        if (queryTextProperty.isDefined()) {
            addQueryText(queryTextProperty.get());
        }
    }

    private void addQueryIdWithLoader(String stringProperty) {
        queryIdLabel = new Label(Page.message("editConfig.projectPanel.queryId"));
        queryIdLabel.getElement()
                .setProperty("title", Page.message("editConfig.projectPanel.queryId.description"));

        queryIdValue = new TextField();
        binder.forField(queryIdValue)
                .withConverter(new StringToLongConverter("Not a number"))
                .withNullRepresentation(0L)
                .bind(stringProperty);
        // set tooltip
        queryIdValue.getElement()
                .setProperty("title", Page.message("editConfig.projectPanel.queryId.description"));

        Button showQueriesButton = EditorUtil.createLookupButton(
                "...",
                "Show available saved queries on the server.",
                "Select a saved query",
                queryProvider,
                exceptionFormatter,namedKeyedObject -> {
                    queryIdValue.setValue(namedKeyedObject.getKey());
                    return null;
                }
        );

        // TODO maybe set "enabled" basing on whether or not loadSavedQueriesOperation is NULL?
        // then can delete the whole "features" mechanism
        showQueriesButton.setEnabled(queryProvider != null);

        add(queryIdLabel, queryIdValue, showQueriesButton);
    }

    private void addQueryText(String stringProperty) {
        queryTextLabel = new Label(Page.message("editConfig.projectPanel.queryText"));
        queryTextLabel.getElement()
                .setProperty("title", Page.message("editConfig.projectPanel.queryText.description"));

        queryTextValue = textInput(binder, stringProperty);
        queryTextValue.getElement()
                .setProperty("title", Page.message("editConfig.projectPanel.queryId.description"));
        add(queryTextLabel, queryTextValue);
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
                + "\nHomepage: " + nvl(project.getHomepage())
                + "\nDescription: " + nvl(project.getDescription());
        Notification.show(msg);
    }

    private String getQueryIdValue() {
        return queryIdValue == null ? null : queryIdValue.getValue();
    }

    public void setProjectKeyLabel(String text) {
        projectKeyLabel.setText(text);
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
        if (projectKeyField.getValue().trim().isEmpty()) {
            throw new ProjectNotSetException();
        }
    }
}
